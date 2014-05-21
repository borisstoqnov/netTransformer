set LIB=..\lib
rem JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
set BASE_DIR=..
set CLASSPATH=".;%LIB%\*"
:: CLASSPATH=".;%LIB%\iDiscover-0.1-SNAPSHOT.jar;%LIB%\snmptoolkit-0.1-SNAPSHOT.jar;%LIB%\topologyViewer-0.1-SNAPSHOT.jar;%LIB%\topologyManagerConfig-0.1-SNAPSHOT.jar;%LIB%\utils-0.1-SNAPSHOT.jar;%LIB%\launcher-0.1-SNAPSHOT.jar;%LIB%\screencap-1.0.jar;%LIB%\rightclickApi-0.1-SNAPSHOT.jar;%LIB%\rightClick-0.1-SNAPSHOT.jar;%LIB%\fulfilmentFactory-0.1-SNAPSHOT.jar;%LIB%\parameterFactory-0.1-SNAPSHOT.jar;%LIB%\resourceManager-0.1-SNAPSHOT.jar;%LIB%\jta26\jta26.jar;%LIB%\jsch-0.1.44.jar;%LIB%\json-simple-1.1.1.jar;%LIB%\gritty-0.0.2.jar;%LIB%\commons-net-3.0.1.jar;%LIB%\log4j-1.2.16.jar;%LIB%\commons-io-1.3.2.jar;%LIB%\commons-beanutils-1.8.3.jar;%LIB%\collections-generic-4.01.jar;%LIB%\colt-1.2.0.jar;%LIB%\concurrent-1.3.4.jar;%LIB%\jung-algorithms-2.0.1.jar;%LIB%\jung-api-2.0.1.jar;%LIB%\jung-graph-impl-2.0.1.jar;%LIB%\jung-io-2.0.1.jar;%LIB%\jung-visualization-2.0.1.jar;%LIB%\stax-api-1.0.1.jar;%LIB%\wstx-asl-3.2.6.jar;%LIB%\saxon-9.1.0.8.jar;%LIB%\snmp4j-1.11.2.jar;%LIB%\mibble-parser-2.9.2.jar;%LIB%\mibble-mibs-2.9.2.jar;%LIB%\grammatica-1.5.jar"
java -Dbase.dir=%BASE_DIR% %JAVA_OPTS% -classpath %CLASSPATH% net.itransformers.topologyvierwer.gui.launcher.TopologyViewerLauncher %*

