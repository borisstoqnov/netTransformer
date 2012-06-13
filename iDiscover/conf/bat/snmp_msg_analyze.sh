#!/bin/sh

#SNMP log message decoder
echo "Please enter the correct path to your java executable"

LIB=../lib
DIST=../lib
CLASSPATH=:${DIST}\discovery-manager.jar:${LIB}\snmp4j\snmp4j-1.11.2.jar
/jre1.6.0_26/bin/java -classpath ${CLASSPATH} $com.discovery.util.SnmpMessageAnalyzer $*