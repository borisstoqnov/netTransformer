package net.itransformers.idiscover.v2.core;

import org.junit.Assert;
import org.junit.Test;

import static net.itransformers.idiscover.util.IPv6formatConvertor.IPv6Convertor;

/**
 * Created by niki on 4/9/2014.
 */
public class IPv6FormatConvertorTestCase {
    String ipAddress="10.20.2.AC.10.D.10.0.0.0.0.0.0.0.0.0.0.";
    @Test
    public void simpleConversion1(){
        final String oldS =   "10.0.10.0.17.0.1.0.0.0.0.0.0.0.0.0.13";

        String newS1 = IPv6Convertor(oldS);
        Assert.assertEquals("IPv6 Addresses are different",newS1,"10:10:17:1::13");

    }
    @Test
    public void simpleConversion2(){
        final String oldS = "10.FE.80.0.0.0.0.0.0.C.0.0.FF.FE.0.65.0";

        String newS1 = IPv6Convertor(oldS);
        Assert.assertEquals("IPv6 Addresses are different",newS1,"10:FE80::C00:FF:FE00:6500");

    }
    @Test
    public void simpleConversion3(){
        final String oldS = "FE.0.0.0.0.0.0.0.0.A.0.0.0.0.0.4";

        String newS1 = IPv6Convertor(oldS);
        Assert.assertEquals("IPv6 Addresses are different",newS1,"10:FE80::C00:FF:FE00:6500");

    }
    @Test
    public void simpleConversion4(){
        final String oldS = "2.10.20.1.4.70.1F.B.A.BD.0.0.0.5.0.13.0.0";

        String newS1 = IPv6Convertor(oldS);
        Assert.assertEquals("IPv6 Addresses are different",newS1,"0210:2001:0470:1F0B:0ABD:0000:0005:0013:0000");

    }

}
