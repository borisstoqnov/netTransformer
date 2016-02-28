/*
 * TableUtil.java
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

package net.itransformers.utils;

import java.util.*;


public class TableUtil {
    // Returns column names from the header line

    public static String[] getColumnNames(String headerLine, String delimiter){
        ArrayList<String> result = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(headerLine, delimiter);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result.toArray(new String[result.size()]);
    }
    //Returns the positions starting from zero of the columns

    public static Integer[] getColumnIndexes(String headerLine, String[] columns){
        ArrayList<Integer> result = new ArrayList<Integer>();
        int pos = 0;
        for (String column : columns) {
            int idx = headerLine.indexOf(column);
            if (idx == -1) throw new IllegalArgumentException(
                    String.format("can not find column '%s' in headerLine: %s",column,headerLine ));
            pos+=idx;
            result.add(pos);
            pos += column.length();
            headerLine = headerLine.substring(idx+column.length(),headerLine.length());

        }
        return result.toArray(new Integer[result.size()]);
    }
    //Returns the cell content from the specified row

    public static String[] getCells(Integer[] columnIndexes, String row) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i=0; i < columnIndexes.length; i++) {
            String cell;
            if (i == columnIndexes.length - 1) {
                cell = row.substring(columnIndexes[i]).trim();
            } else {
                cell = row.substring(columnIndexes[i], columnIndexes[i+1]).trim();
            }
            result.add(cell);
        }
        return result.toArray(new String[result.size()]);
    }

    public static void main(String[] args) {
        String text=
"Interface              IP-Address      OK? Method Status                Protocol\n" +
            "Ethernet0/0            unassigned      YES NVRAM  administratively down down    \n" +
            "Ethernet0/1            unassigned      YES NVRAM  administratively down down    \n" +
            "Ethernet0/2            unassigned      YES NVRAM  administratively down down    \n" +
            "Ethernet0/3            unassigned      YES NVRAM  administratively down down    \n" +
            "Serial1/0              unassigned      YES NVRAM  administratively down down    \n" +
            "Serial1/1              172.16.35.5     YES NVRAM  up                    up      \n" +
            "Serial1/2              unassigned      YES NVRAM  administratively down down    \n" +
            "Serial1/3              unassigned      YES NVRAM  administratively down down    \n" +
            "Loopback0              192.168.5.5     YES NVRAM  up                    up      \n" +
            "Tunnel0                unassigned      YES unset  up                    up      \n" +
            "Vlan1                  unassigned      YES NVRAM  administratively down down";
        String headerLine = "Interface              IP-Address      OK? Method Status                Protocol";
        String row = "Ethernet0/0            unassigned      YES NVRAM  administratively down down    ";


        String[] columns = getColumnNames(headerLine," ");
        Integer[] columnIndexes = getColumnIndexes(headerLine, columns);

        System.out.println(Arrays.asList(columnIndexes));
        String[] cells = getCells(columnIndexes,  row);
        System.out.println(Arrays.asList(cells));

    }
}
