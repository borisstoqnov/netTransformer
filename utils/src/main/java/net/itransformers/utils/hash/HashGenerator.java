package net.itransformers.utils.hash;

import java.security.*;
import java.math.*;
/**
 * Created by niau on 8/25/16.
 */
public class HashGenerator {



        public static String generateMd5(String s ) throws Exception{
            MessageDigest m=MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            return(new BigInteger(1,m.digest()).toString(16));
        }


}
