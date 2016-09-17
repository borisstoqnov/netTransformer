package net.itransformers.utils.ipsec;

/**
 * Created by Bobi on 6/19/2016.
 */
public class IPsecPair {
    public String routerName;
    public String routerIP;
    public String neighbourName;
    public String neighbourIP;

    public IPsecPair(String routerName, String routerIP, String neighbourName, String neighbourIP)
    {
        this.routerName = routerName;
        this.routerIP = routerIP;
        this.neighbourName = neighbourName;
        this.neighbourIP = neighbourIP;
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

