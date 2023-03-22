#!/bin/bash

yum install -y libyaml

groupadd td-agent
useradd -c td-agent -d /var/lib/td-agent -g td-agent -s /sbin/nologin  td-agent

tar -zxvf ./fluentdOpt.tar.gz -C /opt/

tar -zxvf ./fluentdEtc.tar.gz -C /etc/
chown -R td-agent:td-agent /etc/td-agent/

tar -zxvf ./fluentdVarLog.tar.gz -C /var/log/
chown -R td-agent:td-agent /var/log/td-agent/

mkdir -p /usr/local/mysql/lib
tar -zxvf mysqlLibSo.tar.gz
cd lib
mv libcrypto.so.1.1 libmysqlclient.so.21 libmysqlclient.so.21.2.28 libssl.so.1.1 /usr/local/mysql/lib

cd ..
rmdir ./lib

mkdir -p /var/run/td-agent/
chown -R td-agent:td-agent /var/run/td-agent/
