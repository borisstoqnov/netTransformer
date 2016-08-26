package net.itransformers.idiscover.v2.core.node_discoverers.icmp;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by niau on 8/22/16.
 */
public class IcmpStatus {

    static Logger logger = Logger.getLogger(IcmpStatus.class);
    private String ipAddressStr;

    private int timeout;

    public IcmpStatus(String ipAddressStr, int timeout) {
        this.ipAddressStr = ipAddressStr;
        this.timeout = timeout;
    }

    public IcmpStatus(String ipAddressStr) {
        this.ipAddressStr = ipAddressStr;
        this.timeout = 3000;
    }



    public boolean getIcmpStatus(String ipAddressStr) throws IOException {
        InetAddress inetAddress=InetAddress.getByName(ipAddressStr);
        boolean reachable = inetAddress.isReachable(timeout);
        logger.info("Device with " + ipAddressStr + " is reacable through icmp "+reachable +"!!!");
        return reachable;

    }
}
