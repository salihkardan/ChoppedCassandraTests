package ctest;

import com.datastax.driver.core.Session;
import com.google.inject.Inject;
import org.apache.usergrid.chop.api.IterationChop;
import org.apache.usergrid.chop.stack.ChopCluster;
import org.apache.usergrid.chop.stack.ICoordinatedCluster;
import org.apache.usergrid.chop.stack.Instance;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith( JukitoRunner.class )
@UseModules( GuiceModule.class )
@IterationChop( iterations = 2, threads = 1 )
public class TestCassandra {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleCassandraClient.class);

    @ChopCluster( name = "Cassandra" )
    public static ICoordinatedCluster testCluster;

    @Inject
    CassandraClient client;

    @Test
    public void testCreation() {
        LOG.debug( "Created a CassandraClient: {}", client );
        assertNotNull( client );
    }

    @Test
    public void connectionTest(){
        String connectionIP = getIPList(testCluster).get(0);
        LOG.info("Testing connection with ip : {}", connectionIP);
        client.connect(connectionIP);
    }

    @Test
    public void checkSchema(){
        String connectionIP = getIPList(testCluster).get(0);
        client.connect(connectionIP);
        client.createSchema();
        Session session = client.getSession();
        try{
            session.execute("CREATE KEYSPACE IF NOT EXISTS simplex WITH replication " +
                    "= {'class':'SimpleStrategy', 'replication_factor':3};");
        }catch (Exception e){
            System.out.println("Keyspace already exists");
        }
    }

    @Test
    public void checkNumberOfRecords(){
        String connectionIP = getIPList(testCluster).get(0);
        client.connect(connectionIP);
        client.loadData();
        assertEquals(2, client.getNoOfRows());
    }

    @Test
    public void testCluster() {
        if( testCluster == null ) {
            LOG.info( "Test cluster is null, skipping testCluster()..." );
            return;
        }
        assertEquals( "Cassandra", testCluster.getName() );
        assertEquals( 1, testCluster.getSize() );
        assertEquals( 1, testCluster.getInstances().size() );

        for( Instance instance : testCluster.getInstances() ) {
            LOG.info( "Instance is at {} {}", instance.getPublicDnsName(), instance.getPublicIpAddress() );
        }
    }

    public ArrayList<String> getIPList(ICoordinatedCluster cluster){
        ArrayList<String> ipList = new ArrayList<String>();
        for ( Instance temp : cluster.getInstances() ) {
            ipList.add(temp.getPrivateIpAddress());
        }
        return ipList;
    }

}
