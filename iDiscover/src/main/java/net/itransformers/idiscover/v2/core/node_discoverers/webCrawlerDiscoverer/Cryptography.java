/*
 * Cryptography.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.idiscover.v2.core.node_discoverers.webCrawlerDiscoverer;

import java.security.MessageDigest;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class Cryptography {

  private static final char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
      'f' };

  public static String MD5(String str) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(str.getBytes());
      return hexStringFromBytes(md.digest());
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  private static String hexStringFromBytes(byte[] b) {
    String hex = "";
    int msb;
    int lsb;
    int i;

    // MSB maps to idx 0
    for (i = 0; i < b.length; i++) {
      msb = (b[i] & 0x000000FF) / 16;
      lsb = (b[i] & 0x000000FF) % 16;
      hex = hex + hexChars[msb] + hexChars[lsb];
    }
    return (hex);
  }
}
