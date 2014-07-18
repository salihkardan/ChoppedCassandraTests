##### Table of Contents  
- [What is Chop?](#whatis)
- [How to Setup Chop](#setup)  
  - [How to Setup Webapp Instance](#webappsetup)
  - [How to Setup Runner Instance](#runnersetup)
- [How to Start Webapp](#start)  
- [Chop Configuration](#config)  
- [Available Chop Commands](#commands)  
- [How will you reach the cluster information defined in stack.json file](#stack.json)  
- [Go](#go)

<a name="whatis"/>
### What is Chop?
Judo Chop is a simple distributed performance testing framework. Just annotate your JUnit tests with **TimeChop** or **IterationChop** annotations telling Judo Chop how to chop it up. Judo Chop uses your own project's JUnit Test Cases as drivers to bombard your application, service, or server.

The source code and more details about Chop can be found here : [Chop](https://github.com/usergrid/usergrid/tree/two-dot-o/chop).  

<a name="setup"/>
###How to Setup Chop

[Chop](https://github.com/usergrid/usergrid/tree/two-dot-o) is part of Apache Usergrid project and its development still in progress on Github.
You can clone or fork the project from Github. After you cloned code, you need to build chop with the command below from parent directory of chop folder.
  
    $ mvn clean install
    
<a name="webappsetup"/>
####How to Setup Webapp Instance
1. Things to be done on the AWS Instance
  1. Install JDK

    You should install your preferred JDK.

  2. Copy necessary certificate file
  
    After buid completes, a certificate file ("jssecacerts") is created under webapp module. You have to copy it under "$JAVA_HOME/jre/lib/security/", otherwise your webapp cannot make https REST calls to runner instances.

  3. Copy service script of chop 
  
    There is simple service script that can be found [here](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/chop-webapp) to run start, stop, restart and status service commands. Copy that script under /etc/init.d/ folder and set CHOP_HOME environment variable to **"/opt/chop"** and create **"webapp"** directory under CHOP_HOME

2. Things to be done locally
  1. Upload and start chop webapp 
  
    Please refer first item of [configuration](#config) section below. Then switch your current directory to webapp. Then run the following commands to upload and start chop webapp. 
    $ mvn wagon:upload-single
    $ mvn wagon:sshexec


<a name="runnersetup"/>
####How to Setup Runner Instance
1. Install JDK

    You should install your preferred JDK.

2. Copy necessary certificate file
  
    After buid completes, a certificate file ("jssecacerts") is created under webapp module. You have to copy it under "$JAVA_HOME/jre/lib/security/", otherwise your webapp cannot make https REST calls to runner instances.

<a name="start"/>   
####How to Start Webapp

If you have followed [setup](#webappsetup) section, then your webapp should be running and you can check it via following URL:

    https://{chop.coordinator.url}:8443/VAADIN
Default username and password is `user:pass`. Then you need create your own user account and enter AWS credentials and deploy pem file that you downloaded during starting aws intances. 

<a name="config"/>
### Chop Configuration
There are some configurations that you need to do before start chopping. 

1. You should provide the following information inside maven's settings.xml file.

    <servers>
      <server>
          <id>ec2-coordinator-instance</id> <!-- This field should remain the same! -->
          <username>ubuntu</username>
          <privateKey>/path/to/file.pem</privateKey>
      </server>
    <servers>

    <profiles>
      <profile>
          <id>deploy-chop-webapp</id>
          <activation>
              <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
              <chop.coordinator.url>webapp.ip.address</chop.coordinator.url>
          </properties>
      </profile>
      <profile>
          <id>chop-runner</id>
          <activation>
              <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
              <chop.coordinator.username>your.user</chop.coordinator.username>
              <chop.coordinator.password>your.password</chop.coordinator.password>
          </properties>
      </profile> 
    </profiles>

Note: You need to set the same **username** and **password**  provided here on chop webapp. 

2. [pom.xml](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/pom.xml)
        
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

3. [stack.json](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/stack.json) : This file contains stack and cluster configuration that will be setup with **mvn chop:setup** command.

4. [setup script](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/install_cassandra.sh) : This is the script which will run on each cluster instance during setup of stack and clusters. In my script I installed Cassandra and make some configurations for Cassandra.  

Inside your setup script, you will need IP addresses of AWS instances. You can reach instance IPs and host name with the following environmental variables, since they are already injected as environmental variables. Here are the names of environment variables which you can use:   

**'cluster-name'** is the name of cluster which you defined in [stack.json](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/stack.json) file.

    {cluster-name}_PRIVATE_ADDRS
    {cluster-name}_PRIVATE_HOSTS
    {cluster-name}_PUBLIC_ADDRS
    {cluster-name}_PUBLIC_HOSTS 

<a name="commands"/>
### Available Chop Commands
Chop has a maven plugin which you can use while setting up clusters, starting tests etc.
Here is a list of available commands: 


    mvn chop:runner  --> creates runner.jar file which includes your tests cases and its dependencies
    mvn chop:deploy  --> deploy runner.jar file to coordinator
    mvn chop:setup   --> setup stack and clusters defined in stack.json file
    mvn chop:start   --> starts tests
    mvn chop:stop    --> stop tests
    mvn chop:reset   --> if you stop tests, you need to call reset before calling start again
    mvn chop:destroy --> destroy stack and clusters which are set up on AWS. 
    mvn chop:help    --> help

<a name="stack.json"/>
### How will you reach the cluster information defined in stack.json file ?

Cluster information will injected at runtime, so you can reach all properties (i.e. cluster name, number of nodes in cluster, instance IPs ) of your cluster while writing your tests.  All you need is to use `@ChopCluster( name = "" )` annotation. 

    @ChopCluster( name = "Cassandra" ) // this name should be same with the name you defined in stack.json file.
    public static ICoordinatedCluster casCluster;

<a name="go"/>
### Go
You need to use maven chop commands in order. First you need to create runner.jar file with **mvn chop:runner** command, then you need to deploy runner.jar file with **mvn chop:deploy** command. After deployment, invoke **mvn chop:setup** command to setup stack and clusters defined in stack.json file. Finally to start tests call **mvn chop:start** command. 
