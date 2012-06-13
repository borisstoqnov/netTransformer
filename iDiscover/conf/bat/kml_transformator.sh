#!/bin/sh

#KMLS Startup Script
echo "Please enter the correct path to your java executable"


LIB=../lib
DIST=../lib
CLASSPATH=:${DIST}/discovery-manager.jar:${DIST}/utils.jar:${DIST}/snmp-discoverer.jar:${LIB}/snmp4j/snmp4j-1.11.2.jar:${LIB}/mibble/mibble-mibs-2.9.2.jar:${LIB}/mibble/grammatica-1.5.jar:${LIB}/mibble/mibble-parser-2.9.2.jar:${LIB}/log4j/log4j-1.2.14.jar:${LIB}/apache-commons/commons-io-2.0.1.jar:${LIB}/apache-commons/commons-beanutils-1.8.3.jar:${LIB}/apache-commons/commons-logging-1.1.1.jar:${LIB}/saxon/saxon9.jar
echo $CLASSPATH
echo "Fire up discovery Process"
java -classpath ${CLASSPATH} -Xms256m -Xmx512m com.discovery.util.KMLTransformator $*
