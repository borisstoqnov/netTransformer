set LIB="..\lib"
rem set JAVA_HOME="%LIB%\jdk1.6.0_25"
set CLASSPATH=".;%LIB%\iDiscover-1.0.jar;%LIB%\utils-1.0.jar;%LIB%\snmp2xml-1.0.jar;%LIB%\resourceManager-1.0.jar;%LIB%\snmp4j-1.11.2.jar;%LIB%\mibble-mibs-2.9.2.jar;%LIB%\grammatica-1.5.jar;%LIB%\mibble-parser-2.9.2.jar;%LIB%\log4j-1.2.15.jar;%LIB%\commons-io-1.3.2.jar;%LIB%\commons-beanutils-1.8.3.jar;%LIB%\commons-logging-1.1.1.jar;%LIB%\saxon9.jar"

set JAVA_OPTS=-Xms256m -Xmx512m %DEBUG_JAVA_OPTS%
java %JAVA_OPTS% -classpath %CLASSPATH% net.itransformers.idiscover.core.DiscoveryManager -m ..\mibs -f iDiscover/conf/xml/discoveryManager.xml %*
rem set DEBUG_JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005