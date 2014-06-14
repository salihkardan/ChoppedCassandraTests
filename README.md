### What is Chop?
Judo Chop is a simple distributed performance testing framework which work with Maven. Just annotate your JUnit tests with **TimeChop** or **IterationChop** annotations telling Judo Chop how to chop it up. Judo Chop uses your own project's JUnit Test Cases as drivers to bombard your application, service, or server.

You can reach the source code and more details here: [Chop](https://github.com/usergrid/usergrid/tree/two-dot-o/chop).  



### Chop Configuration
There are some configurations that you need to do before start chopping. 

1) [pom.xml](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/pom.xml) : You need to configure **username**, **password** and **chop.coordinator.url** parameters which will be same in the coordinator web interface. 
        
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>2.4</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>test-jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>org.apache.usergrid.chop</groupId>
                <artifactId>chop-maven-plugin</artifactId>
                <version>${chop.version}</version>
                <configuration>
                    <username>${chop.coordinator.username}</username>
                    <password>${chop.coordinator.password}</password>
                    <endpoint>https://${chop.coordinator.url}:8443</endpoint>
                    <testPackageBase>ctest</testPackageBase>
                    <runnerCount>2</runnerCount>
                </configuration>
            </plugin>
        </plugins>

Since you will not want to commit your user specific information such username and password, you can put those information into settings.xml file of maven.

    <servers>
      <server>
          <id>ec2-coordinator-instance</id> <!-- This field should remain the same! -->
          <username>ubuntu</username>
          <privateKey>/home/salih/salih.pem</privateKey>
      </server>
    <servers>

    <profiles>
      <profile>
          <id>deploy-chop-webapp</id>
          <activation>
              <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
              <chop.coordinator.url>54.85.138.123</chop.coordinator.url>
          </properties>
      </profile>
      <profile>
          <id>chop-runner</id>
          <activation>
              <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
              <chop.coordinator.username>user</chop.coordinator.username>
              <chop.coordinator.password>pass</chop.coordinator.password>
          </properties>
      </profile> 
    </profiles>

2) [stack.json](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/stack.json) : This file contains stack and cluster configuration that will be setup with **mvn chop:setup** command.

3) [setup script](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/install_cassandra.sh) : This is the script which will run on each cluster instance during setup of stack and clusters. In my script I installed Cassandra and make some configurations for cluster setup.  

Inside your setup script, you will need ip address of AWS instances. You can reach instance ips and host name with these environmental variables, since they are already injected as environmental variable before you run you script. Here are the names of environment variables which you can use:   

**'cluster-name'** is the name of cluster which you defined in [stack.json](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/stack.json) file.

    {cluster-name}_PRIVATE_ADDRS
    {cluster-name}_PRIVATE_HOSTS
    {cluster-name}_PUBLIC_ADDRS
    {cluster-name}_PUBLIC_HOSTS 

### Available Chop Commands


    mvn chop:runner  --> creates runner.jar file which includes your tests cases and its dependencies
    mvn chop:deploy  --> deploy runner.jar file to coordinator
    mvn chop:setup   --> setup stack and clusters defined in stack.json file
    mvn chop:start   --> starts tests
    mvn chop:stop    --> stop tests
    mvn chop:reset   --> if you stop tests, you need to call reset before calling start again
    mvn chop:destroy --> destroy stack and clusters which are set up on AWS. 


### How will you reach the cluster information defined in stack.json file ?

Cluster information will injected at runtime, so you can reach all properties such name of cluster, number of nodes, instance IPs etc. of your cluster while writing your tests.  All you need is to use `@ChopCluster( name = "" )` annotation. 

    @ChopCluster( name = "Cassandra" ) // this name should be same with the name you defined in stack.json file.
    public static ICoordinatedCluster casCluster;

### Go
You need to use chop commands in order, first you need to create runner.jar file with **mvn chop:runner: command, then you need to deploy runner.jar file with **mvn chop:deploy** command. After deployment, invoke **mvn chop:setup** command to setup stack and clusters defined in stack.json file. Finally to start tests call **mvn chop:start** command. 

