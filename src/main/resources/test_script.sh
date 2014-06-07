#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
casVersion=2.0.7
wget -P /opt https://archive.apache.org/dist/cassandra/$casVersion/apache-cassandra-$casVersion-bin.tar.gz
tar -xpf /opt/apache-cassandra-$casVersion-bin.tar.gz -C /opt
rm /opt/apache-cassandra-$casVersion-bin.tar.gz

# export environmental variable
envFile=/etc/profile.d/cassandra.sh
if [ ! -f "$envFile" ]; then
	touch /etc/profile.d/cassandra.sh
fi

if ! grep -q "export" $envFile ; then
	echo "export CASSANDRA_HOME=/opt/apache-cassandra-$casVersion/" >> $envFile
	echo "export PATH=$PATH:/opt/apache-cassandra-$casVersion/bin"  >> $envFile
fi
chmod +x $envFile
. /etc/profile

file=/opt/apache-cassandra-$casVersion/conf/cassandra.yaml
# set listen_address
localIP=`hostname -I`
sed -i "s/listen_address:.*/listen_address: $localIP/g" $file
sed -i "s/rpc_address:.*/rpc_address: $localIP/g" $file

# set seeds
master=""
IFS=' ' read -a array <<< "$CASSANDRA_PRIVATE_ADDRS"
for index in "${!array[@]}"
do
	if [ "$index" == "0" ]; then
		master=${array[$index]}
	fi
    	echo "$index ${array[index]}"
done
sed -i "s/- seeds:.*/- seeds: \"$master\"/g" $file

sudo sh /opt/apache-cassandra-$casVersion/bin/cassandra >> /dev/null