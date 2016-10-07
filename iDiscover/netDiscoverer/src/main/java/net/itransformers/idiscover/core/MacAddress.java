package net.itransformers.idiscover.core;

/**
 * Created by niau on 6/26/16.
 */
public class MacAddress {
    String macAddress;
    String portName;

    public MacAddress(String macAddress, String portName) {
        this.macAddress = macAddress;
        this.portName = portName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }
}
