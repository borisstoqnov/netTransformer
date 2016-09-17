package net.itransformers.utils.hash;

import org.apache.log4j.Logger;

import java.security.*;
import java.math.*;

/**
 * Created by niau on 8/25/16.
 */
public class HashGenerator {

    static Logger logger = Logger.getLogger(HashGenerator.class);


    public static String generateMd5(String s) throws Exception {
        if (s != null) {
            if (!s.isEmpty()) {
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.update(s.getBytes(), 0, s.length());
                return (new BigInteger(1, m.digest()).toString(16));
            } else {
                logger.error("Trying to generate a hash from an empty string!!!");
                return null;

            }
        } else {
            logger.error("Trying to generate a hash from a null!!!");

            return null;
        }
    }
}
