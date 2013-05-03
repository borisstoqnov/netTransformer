#!/bin/sh

#bgpPeeringMap Startup Script
echo "If there is a problem! Please drop us an email at info@itransformers.net"

version=0.1-SNAPSHOT
LIB=../lib
CLASSPATH=".:${LIB}/bgpPeeringMap-${version}.jar:${LIB}/utils-${version}.jar:${LIB}/snmptoolkit-${version}.jar:${LIB}/snmp4j-1.11.2.jar:${LIB}/mibble-mibs-2.9.2.jar:${LIB}/grammatica-1.5.jar:${LIB}/mibble-parser-2.9.2.jar:${LIB}/log4j-1.2.16.jar:${LIB}/commons-io-1.3.2.jar:${LIB}/commons-beanutils-1.8.3.jar:${LIB}/commons-logging-1.1.1.jar:${LIB}/saxon-9.1.0.8.jar"
#DEBUG_JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005
JAVA_OPTS="-Xms512m -Xmx4048m"
java ${JAVA_OPTS} -classpath ${CLASSPATH} net.itransformers.bgpPeeringMap.bgpPeeringMap -s bgpPeeringMap/conf/bgpPeeringMap.properties %*
