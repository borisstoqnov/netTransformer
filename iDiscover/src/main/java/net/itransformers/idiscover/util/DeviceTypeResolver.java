package net.itransformers.idiscover.util;

/**
 * Created by niau on 8/22/16.
 */
public  class DeviceTypeResolver {


    public static String getDeviceType(String sysDescr ) {
        if (sysDescr == null) return "UNKNOWN";
        String sysDescrToUpper = sysDescr.toUpperCase();

        if (sysDescrToUpper.toUpperCase().contains("ProCurve".toUpperCase())) {
            return "HP";
        } else if (sysDescrToUpper.contains("Huawei".toUpperCase())) {
            return "HUAWEI";
        } else if (sysDescrToUpper.contains("Juniper".toUpperCase())) {
            return "JUNIPER";
        } else if (sysDescrToUpper.contains("Cisco".toUpperCase())) {
            return "CISCO";
        } else if (sysDescrToUpper.contains("Tellabs".toUpperCase())) {
            return "TELLABS";
        } else if (sysDescrToUpper.contains("SevOne".toUpperCase())) {
            return "SEVONE";
        } else if (sysDescrToUpper.contains("Riverstone".toUpperCase())) {
            return "RIVERSTONE";
        } else if (sysDescrToUpper.contains("ALCATEL".toUpperCase())) {
            return "ALCATEL";
        } else if (sysDescrToUpper.contains("Linux".toUpperCase())) {
            return "LINUX";
        } else if (sysDescrToUpper.contains("Windows".toUpperCase())) {
            return "WINDOWS";
        } else {
            return "UNKNOWN";
        }
    }
}
