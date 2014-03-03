#!/bin/sh

#Discovery Startup Script
echo "Please enter the correct path to your java executable"


LIB=../lib
DIST=../lib
#CLASSPATH=:${DIST}/snmp-discoverer.jar:${LIB}/snmp4j/snmp4j-1.11.2.jar:${LIB}/mibble/mibble-mibs-2.9.2.jar:${LIB}/mibble/grammatica-1.5.jar:${LIB}/mibble/mibble-parser-2.9.2.jar:${LIB}/log4j/log4j-1.2.14.jar:${LIB}/apache-commons/commons-io-2.0.1.jar:${LIB}/apache-commons/commons-beanutils-1.8.3.jar:${LIB}/apache-commons/commons-logging-1.1.1.jar:${LIB}/saxon/saxon9.jar
CLASSPATH=".:${LIB}/*";
echo $CLASSPATH
echo "Fire up snmp walk process"
java -classpath ${CLASSPATH} net.itransformers.snmptoolkit.Walk $*