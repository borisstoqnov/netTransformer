set LIB="..\lib"
set DIST="..\lib"
set CLASSPATH=".;%DIST%\discovery-manager.jar;%LIB%\snmp4j\snmp4j-1.11.2.jar;"
java -classpath %CLASSPATH% com.discovery.util.SnmpMessageAnalyzer %*