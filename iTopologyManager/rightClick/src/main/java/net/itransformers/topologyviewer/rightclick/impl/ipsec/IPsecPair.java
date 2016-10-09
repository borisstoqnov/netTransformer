package net.itransformers.topologyviewer.rightclick.impl.ipsec;

/**
 * Created by Bobi on 6/19/2016.
 */
public class IPsecPair {
    public String routerName;
    public String routerIP;
    public String neighbourName;
    public String neighbourIP;
    protected String enablepass;
    protected String telnetpass;
    protected String username;


    public IPsecPair(String routerName, String routerIP, String neighbourName, String neighbourIP, String username, String enablepass, String telnetpass)
    {
        this.routerName = routerName;
        this.routerIP = routerIP;
        this.neighbourName = neighbourName;
        this.neighbourIP = neighbourIP;
        this.username = username;
        this.enablepass = enablepass;
        this.telnetpass = telnetpass;
    }

    public String getRouterName() {
        return routerName;
    }

    public String getRouterIP() {
        return routerIP;
    }

    public String getNeighbourName() {
        return neighbourName;
    }

    public String getNeighbourIP() {
        return neighbourIP;
    }

    public String getTelnetpass() {
        return telnetpass;
    }

    public String getEnablepass() {
        return enablepass;
    }

    public String getUsername() { return username; }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public void setRouterIP(String routerIP) {
        this.routerIP = routerIP;
    }

    public void setNeighbourName(String neighbourName) {
        this.neighbourName = neighbourName;
    }

    public void setNeighbourIP(String neighbourIP) {
        this.neighbourIP = neighbourIP;
    }
    @Override public String toString(){
        return this.routerName + "=" + this.routerIP + ";" + this.neighbourName + "=" + this.neighbourIP;
    }
}

