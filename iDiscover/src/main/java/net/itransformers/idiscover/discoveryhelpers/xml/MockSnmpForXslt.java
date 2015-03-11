package net.itransformers.idiscover.discoveryhelpers.xml;

/**
 * Created by vasko on 09.03.15.
 */
public class MockSnmpForXslt {

    public String getNameByDNS(String ipAddress) {
        return null;
    }

    public String getName(String ipAddress, String community, String timeout, String retries) {
        return null;
    }

    public String getSymbolByOid(String mibName, String oid) {
        return null;
    }

    public String getByOid(String ipAddress, String oid, String community, String timeout, String retries) {
        return null;
    }

    public boolean setByOID(String hostName, String oid, String community, String value) {
        return false;
    }

    public String walkByString(String hostName, String[] params, String community) {
        return null;
    }
}
