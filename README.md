# What is Chop?
Judo Chop is a simple distributed performance testing framework which work with Maven. Just annotate your JUnit tests with **TimeChop** or **IterationChop** annotations telling Judo Chop how to chop it up. Judo Chop uses your own project's JUnit Test Cases as drivers to bombard your application, service, or server.

You can reach the source code and more details here: [Chop](https://github.com/usergrid/usergrid/tree/two-dot-o/chop).  



# Chop Configuration
There are some configurations that you need to do before start chopping. 

1) [pom.xml](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/pom.xml) : You need to conifugure **username**, **password** and **coordinator-url** parameter which will be same in the coordinator web interface. 

2) [stack.json](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/stack.json) : This file contains stack and cluster configuration that will be setup with **mvn chop:setup** command.

3) [setup script](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/install_cassandra.sh) : This is the script which will run on each cluster instance during setup of stack and clusters. In my script I installed Cassandra and make some configurations for cluster setup.  

Inside your setup script, you will need ip address of AWS instances. You can reach instance ips and host name with these environmental variables, since they are already injected as environmental variable before you run you script. Here are the names of environment variables which you can use:   

**cluster-name** is the name of cluster which you defined in [stack.json](https://github.com/salihkardan/ChoppedCassandraTests/blob/master/src/main/resources/stack.json) file.

{cluster-name}_PRIVATE_ADDRS

{cluster-name}_PRIVATE_HOSTS,

{cluster-name}_PUBLIC_ADDRS, 

{cluster-name}_PUBLIC_HOSTS 

while installation of cluster


# Available Chop Commands


**1) mvn chop:runner**  --> creates runner.jar file which includes your tests cases and its dependencies

**2) mvn chop:deploy**  --> deploy runner.jar file to coordinator

**3) mvn chop:setup**   --> setup stack and clusters defined in stack.json file

**4) mvn chop:start**   --> starts tests

**5) mvn chop:stop**    --> stop tests

**6) mvn chop:reset**   --> if you stop tests, you need to call reset before calling start again

**7) mvn chop:destroy** --> destroy stack and clusters which are set up on AWS. 


# Go

