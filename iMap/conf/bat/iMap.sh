#!/bin/sh

#iMap Startup Script
echo "If there is a problem! Please enter the correct path to your java"

LIB=../lib
version=0.1-SNAPSHOT
CLASSPATH=".:${LIB}/iMap-${version}.jar:${LIB}/utils-${version}.jar:${LIB}/snmp2xml-${version}.jar:${LIB}/snmp4j-1.11.2.jar:${LIB}/mibble-mibs-2.9.2.jar:${LIB}/grammatica-1.5.jar:${LIB}/mibble-parser-2.9.2.jar:${LIB}/log4j-1.2.16.jar:${LIB}/commons-io-1.3.2.jar:${LIB}/commons-beanutils-1.8.3.jar:${LIB}/commons-logging-1.1.1.jar:${LIB}/saxon-9.1.0.8.jar"
#DEBUG_JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005
JAVA_OPTS="-Xms512m -Xmx1024m"
java ${JAVA_OPTS} -classpath ${CLASSPATH} InetMap -s iMap/conf/txt/settings.properties %*
