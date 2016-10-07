package net.itransformers.idiscover.v2.core;

import net.itransformers.utils.CIDRUtils;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

/**
 * Created by niau on 9/7/16.
 */
public class IPv4BogonIdentitifier {
    static Logger logger = Logger.getLogger(IPv4BogonIdentitifier.class);


    private String ipAddress;


    public IPv4BogonIdentitifier(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean identifyBogon() {

        try {

            CIDRUtils cidrUtils = null;
            cidrUtils = new CIDRUtils("0.0.0.0/8");

            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("127.0.0.0/8");
            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("128.0.0.0/8");
            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("169.254.0.0/16");
            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("192.0.0.0/24");
            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("192.0.2.0/24");
            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("224.0.0.0/4");
            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("240.0.0.0/4");
            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("255.255.255.255/32");
            if (cidrUtils.isInRange(ipAddress)) {
                return true;
            }

        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }
        return false;

    }
}
