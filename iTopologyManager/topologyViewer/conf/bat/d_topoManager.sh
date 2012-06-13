#!/bin/sh

echo "Echo"
LIB=../lib

CLASSPATH="..:${LIB}/utils.jar:${LIB}/saxon/saxon9.jar:${LIB}/topology-viewer.jar:${LIB}/rightclick.jar:${LIB}/dump/dump.jar:${LIB}/fulfilment-factory.jar:${LIB}/parameter-factory.jar:${LIB}/resource-manager.jar:$LIB/jta26/jta26.jar:${LIB}/jsch/jsch-0.1.44.jar:${LIB}/gritty/gritty-0.02.jar:${LIB}/commons-net-3.0.1/commons-net-3.0.1.jar:${LIB}/log4j/log4j-1.2.14.jar:${LIB}/apache-commons/commons-io-2.0.1.jar:${LIB}/jung/collections-generic-4.01.jar:${LIB}/jung/colt-1.2.0.jar:${LIB}/jung/concurrent-1.3.4.jar:$LIB/jung/j3d-core-1.3.1.jar:${LIB}/jung/jung-3d-2.0.1.jar:${LIB}/jung/jung-3d-demos-2.0.1.jar:${LIB}/jung/jung-algorithms-2.0.1.jar:${LIB}/jung/jung-api-2.0.1.jar:${LIB}/jung/jung-graph-impl-2.0.1.jar:${LIB}/jung/jung-io-2.0.1.jar:${LIB}/jung/jung-jai-2.0.1.jar:${LIB}/jung/jung-jai-samples-2.0.1.jar:${LIB}/jung/jung-samples-2.0.1.jar:${LIB}/jung/jung-visualization-2.0.1.jar:${LIB}/jung/stax-api-1.0.1.jar:${LIB}/jung/vecmath-1.3.1.jar:${LIB}/jung/wstx-asl-3.2.6.jar:"
echo $CLASSPATH
echo $LIB
java -classpath ${CLASSPATH} net.itransformers.topologyviewer.gui.TopologyViewer -t directed -d network -g directed -f topology-viewer/conf/xml/directed.xml $*

