package net.itransformers.connectiondetails.connectiondetailsapi;


import java.io.Serializable;
import java.util.Map;

/**
 * Created by Vasil Yordanov on 26-May-16.
 */
public class IPNetConnectionDetails extends ConnectionDetails implements Cloneable, Serializable{

    public static final String IP_ADDRESS_PARAM_KEY = "ipAddress";

    public IPNetConnectionDetails() {
        super();
    }

    public IPNetConnectionDetails(String connectionType) {
        super(connectionType);
    }

    public IPNetConnectionDetails(String connectionType, Map<String, String> params) {
        super(connectionType, params);
    }


    @Override
    public boolean equals(Object obj) {
        String ip = params.get(IP_ADDRESS_PARAM_KEY);
       // String connectionType = getConnectionType();
        ConnectionDetails connectionDetails2 = (ConnectionDetails)obj;
        String ip2 = connectionDetails2.getParam(IP_ADDRESS_PARAM_KEY);

        if (ip==null || ip2==null) {
            return false;
        }
        return ip.equals(ip2);

//        if (ip == null || connectionType==null) {
//            return false;
//        }
//        String connectionType2=((ConnectionDetails) obj).getConnectionType();
//
//
//        if (ip2 == null|| connectionType2==null){
//            return false;
//        }
//        if (ip.equals(ip2) && !connectionType.equals(connectionType2))
//            System.out.println(connectionType+":"+connectionType2);
//        return ip.equals(ip2) && connectionType.equals(connectionType2);
    }

    @Override
    public int hashCode() {
        String ip = params.get(IP_ADDRESS_PARAM_KEY);
        if (ip == null ){
            return 0;
        } else {
            return ip.hashCode();
        }

    }

}
