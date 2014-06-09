package ctest;

import com.datastax.driver.core.*;

public interface CassandraClient {

    void connect(String node);

    void close();

    void createSchema();

    void loadData();

    void querySchema();

    Cluster getCluster();

    void setCluster(Cluster cluster);

    Session getSession();

    void setSession(Session session);

    int getNoOfRows();
}
