/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.util;

public class IPv6formatConvertor {
     public  static String IPv6Convertor(String oldS){
         String s= oldS;
         String [] test  = s.split("\\.");
         if (test.length<18){
             System.out.println(test.length);
             s = "00."+s;
         }
        String newS = "";
        int dotIndex = -1;
        while (true) {
            dotIndex = s.indexOf(".");
            if (dotIndex == -1) break;
            int octet1 = Integer.parseInt(s.substring(0, dotIndex), 16);
            s = s.substring(dotIndex+1);
            dotIndex = s.indexOf(".");
            int octet2;
            if (dotIndex == -1) {
                octet2  = Integer.parseInt(s, 16);
            } else {
                octet2 = Integer.parseInt(s.substring(0,dotIndex), 16);
                s = s.substring(dotIndex+1);
            }

            newS += String.format("%02X%02X:", octet1, octet2);
        }
        System.out.println(oldS);

        if (newS.endsWith(":")){
            s = newS.substring(0,newS.length()-1);
        }   else {
            s = newS;
        }
        System.out.println(s);
        String newS1 = "";
        while (true) {
            dotIndex = s.indexOf(":");
            if (dotIndex == -1) {
                long octetPair = Long.parseLong(s.substring(0), 16);
                s = s.substring(dotIndex+1);
                if (octetPair == 0) {
                        if (!newS1.endsWith("::"))
                        newS1 += ":";
                        continue;

                }
                newS1 += String.format("%X",octetPair);
                break;
            }   else {
                long octetPair = Long.parseLong(s.substring(0, dotIndex), 16);
                s = s.substring(dotIndex+1);
                if (octetPair == 0) {
                        if (!newS1.endsWith("::"))
                        newS1 += ":";
                        continue;

                }
                newS1 += String.format("%X:",octetPair);
            }

        }
         return newS1;

     }
     public static void main(String[] args) {
//        final String oldS = "FE..0.0.0.0.0.0.0.A.0.0.0.0.0.4";
         final String oldS =   "10.0.10.0.17.0.1.0.0.0.0.0.0.0.0.0.13";
        // final String oldS = "10.FE.80.0.0.0.0.0.0.C.0.0.FF.FE.0.65.0";

         String newS1 = IPv6Convertor(oldS);
         System.out.println(newS1);
     }
}
