package net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer.snmpmanager;

import net.itransformers.snmp2xml4j.snmptoolkit.*;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by niau on 8/22/16.
 */
public class SnmpManagerCreator {

    static Logger logger = Logger.getLogger(SnmpManagerCreator.class);

    MibLoaderHolder mibLoaderHolder;


    public SnmpManagerCreator(MibLoaderHolder mibLoaderHolder) {
        this.mibLoaderHolder = mibLoaderHolder;
    }

    public SnmpManager create(Map<String, String> snmpConnParams) {

        String version = snmpConnParams.get("version");
        String protocol = snmpConnParams.get("protocol");
        SnmpManager snmpManager = null;

        if ("3".equals(version) && "udp".equals(protocol)) {
            snmpManager = new SnmpUdpV3Manager(mibLoaderHolder.getLoader());
        } else if ("3".equals(version) && "tcp".equals(protocol)) {
            snmpManager = new SnmpTcpV3Manager(mibLoaderHolder.getLoader());
        } else if ("2".equals(version) && "udp".equals(protocol)) {
            snmpManager = new SnmpUdpV2Manager(mibLoaderHolder.getLoader());
        } else if ("2c".equals(version) && "udp".equals(protocol)) {
            snmpManager = new SnmpUdpV2Manager(mibLoaderHolder.getLoader());
        } else if ("2".equals(version) && "tcp".equals(protocol)) {
            snmpManager = new SnmpTcpV2Manager(mibLoaderHolder.getLoader());
        } else if ("2c".equals(version) && "tcp".equals(protocol)) {
            snmpManager = new SnmpTcpV2Manager(mibLoaderHolder.getLoader());
        } else if ("1".equals(version) && "udp".equals(protocol)) {
            snmpManager = new SnmpTcpV1Manager(mibLoaderHolder.getLoader());
        } else if ("1".equals(version) && "tcp".equals(protocol)) {
            snmpManager = new SnmpTcpV1Manager(mibLoaderHolder.getLoader());
        } else {
            logger.info("Unsupported combination of protocol: " + protocol + " and version " + version);
            throw new RuntimeException("Unsupported combination of protocol: \" + protocol + \" and version \" + version");

        }
        snmpManager.setParameters(snmpConnParams);
        return snmpManager;

    }



}
