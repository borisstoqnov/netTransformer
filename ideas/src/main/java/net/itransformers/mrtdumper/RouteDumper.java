package net.itransformers.mrtdumper;

import net.itransformers.utils.CmdLineParser;
import org.javamrt.mrt.*;
import org.javamrt.utils.Debug;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class RouteDumper {

    public static void main(String args[]) throws IOException {
        Map<String, String> params = CmdLineParser.parseCmdLine(args);
        PrintWriter writer = null;
        if (params.containsKey("-o")) {
            writer = new PrintWriter(params.get("-o"));
        } else {
            writer = new PrintWriter(System.out);
        }
        File file = null;
        if (params.containsKey("-f")) {
            file = new File(params.get("-f"));
        } else {
            usage();
            System.exit(1);
        }
        dumpToXmlString(new File[]{file}, writer);
        writer.close();
	}

    public static void dumpToXmlString(File[] files, Writer writer) throws IOException {
        MRTRecord record;
        BGPFileReader in;
        AS traverses = null;
        AS originator = null;
        InetAddress peer = null;
        Prefix prefix = null;
        Checker checker = null;
        boolean oldall = true;
        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
        writer.append("<root>\n");
        for (File file1 : files) {
            try {
                in = new BGPFileReader(file1);
                while (!in.eof()) {
                    try {
                        if ((record = in.readNext()) == null){
                            checker = new Checker(prefix,peer,originator,traverses,record);
                            break;
                        }
                        if (record instanceof Open
                                || record instanceof KeepAlive
                                || record instanceof Notification){
                            checker = new Checker(prefix,peer,originator,traverses,record);
                            continue;
                        }
                        if (record instanceof StateChange) {
                            if (oldall && checker.checkPeer()) {
                                writer.append(toXmlString(record));
                                continue;
                            }
                        }
                        if ((record instanceof TableDump)
                                || (record instanceof Bgp4Update)) {
                            checker = new Checker(prefix,peer,originator,traverses,record);

                            try {
                                if (!checker.checkPrefix())
                                    continue;
                                if (!checker.checkPeer())
                                    continue;
                                if (!checker.checkASPath())
                                    continue;
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                                System.err.printf("record = %s\n",record);
                            }
                            writer.append(toXmlString(record));
                        }
                    } catch (RFC4893Exception rfce) {
                        boolean printRFC4893violations = false;
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
                in.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + file1.getAbsolutePath());
            } catch (Exception ex) {
                System.err.println("Exception caught when reading " + file1.getAbsolutePath());
                ex.printStackTrace(System.err);
            }
        }
        writer.append("\n</root>");
    }

    private static String toXmlString(MRTRecord record){
        if (record instanceof TableDump) {
            return toXmlString((TableDump)record);
        } else if (record instanceof Bgp4Update) {
            return toXmlString((Bgp4Update)record);
        } else if (record instanceof StateChange){
            return toXmlString((StateChange)record);
        }
        return null;
    }

    private static String toXmlString(StateChange stateChange) {
        return null;
    }

    private static String toXmlString(Bgp4Update bgp4Update) {
        String peerString = ipAddressString(bgp4Update.getPeer());

        String result = "<"+"BGP4MP"+ " time=\"" + bgp4Update.getTime() + "\""
                + // this comes from MRTRecord
                "updateType=\""+"?" + "\"" +"peerString=\"" + peerString + "\""
                + "peerAS=\""+bgp4Update.getPeerAS() + "\"" + "prefix=\"" + bgp4Update.getPrefix().toString()+"\""+"/>";

        if (bgp4Update.getAttributes() != null)
            result += '|' + bgp4Update.getAttributes().toString();

        return result;
    }

    private static String toXmlString (TableDump tableDump)
    {
        StringBuffer dumpString = new StringBuffer();
        String cleanPeer = // this.Peer.getHostAddress ().replaceFirst("(:0){2,7}", ":").replaceFirst("^0::", "::");
                ipAddressString(tableDump.getPeer());
        dumpString =
                new StringBuffer("\n<"+tableDump.getType()+"  origTime=\""+tableDump.getOrigTime()+"\"");
        dumpString.append (" updateType=\"B\" cleanPeer=\"" + cleanPeer  +"\" ");
        if (tableDump.getPeerAS() != null)
            dumpString.append (" AS=\""+tableDump.getPeerAS().toString()+"\"");
        dumpString.append (" prefix=\"" + tableDump.getPrefix().toString()+"\"");
        dumpString.append (">\n");
        Attributes tableDumpAttributes = getTableDumpAttributes(tableDump);

        try {
            dumpString.append (toXmlString(tableDumpAttributes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dumpString.append ("</"+tableDump.getType()+">");
        return dumpString.toString();
    }

    private static String ipAddressString(InetAddress ia)
    {
        return ia.getHostAddress().
                // replaceFirst("^[^/]*/", "").
                        replaceFirst(":0(:0)+","::").
                replaceFirst("^0:","").
                replaceFirst(":::", "::");
    }

    private static String toXmlString (Attributes attributes) throws Exception {
        StringBuilder toStr = new StringBuilder();
        toStr.append("\t<attributes>\n");
        for (int i = MRTConstants.ATTRIBUTE_AS_PATH; i < MRTConstants.ATTRIBUTE_TOTAL; i++) {
//            System.out.println(attributes.elementAt(i));
            if (attributes.getAttribute(i)!=null){
                String type = "";
                if (i== MRTConstants.ATTRIBUTE_ORIGIN){
                    type="Origin";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                } else if(i== MRTConstants.ATTRIBUTE_AS_PATH){
                    type="ASPath";
                    String[] ASes=attributes.getAttribute(i).toString().split(" ");
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">");
                    LinkedHashMap<String,Integer> map = new LinkedHashMap<String, Integer>();
                    for (String as : ASes)
                    {
                        Integer f = map.get(as);

                        if (f == null)
                            map.put(as, 1);
                        else
                            map.put(as, ++f);
                    }
                    Iterator<Map.Entry<String, Integer>> entries = map.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, Integer> thisEntry = (Map.Entry) entries.next();
                        String key = thisEntry.getKey();
                        Integer value = thisEntry.getValue();
                        if (entries.hasNext()){
                            toStr.append("\n\t\t\t<AS count=\""+value+"\">"+key+"</AS>");
                        }else{
                            toStr.append("\n\t\t\t<AS count=\""+value +"\" " +"last=\"true\">"+key+"</AS>");
                        }
                    }

//                    for (Map.Entry<String, Integer> entry : map.entrySet()){
//                        if (entry.){
//                            toStr.append("\n\t\t\t<AS count=\""+entry.getValue()+"\">"+entry.getKey()+"</AS>");
//                        }else{
//                            toStr.append("\n\t\t\t<AS count=\""+entry.getValue() +"\"" +"last=\"true\">"+entry.getKey()+"</AS>");
//                        }
//                    }
                    toStr.append("\n\t\t</attribute>\n");

                } else if(i == MRTConstants.ATTRIBUTE_NEXT_HOP){
                    type="nextHop";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");
                }  else if(i== MRTConstants.ATTRIBUTE_LOCAL_PREF){
                    type="localPref";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }   else if(i== MRTConstants.ATTRIBUTE_MULTI_EXIT){
                    type="MED";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                } else if(i== MRTConstants.ATTRIBUTE_COMMUNITY){
                    type="Community";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_ATOMIC_AGGREGATE){
                    type="ATOMIC_AGGREGATE";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_AGGREGATOR){
                    type="AGGREGATOR";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_ORIGINATOR_ID){
                    type="ORIGINATOR_ID";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_CLUSTER_LIST){
                    type="CLUSTER_LIST";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_DPA){
                    type="DPA";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_ADVERTISER){
                    type="ADVERTISER";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_CLUSTER_ID){
                    type="CLUSTER_ID";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_MP_REACH){
                    type="MP_REACH";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_MP_UNREACH){
                    type="MP_UNREACH";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_EXT_COMMUNITIES){
                    type="EXT_COMMUNITIES";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_AS4_PATH){
                    type="AS4_PATH";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_AS4_AGGREGATOR){
                    type="AS4_AGGREGATOR";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_ASPATHLIMIT){
                    type="ASPATHLIMIT";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }else if(i== MRTConstants.ATTRIBUTE_TOTAL){
                    type="TOTAL";
                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.getAttribute(i).toString()).append("</attribute>\n");

                }
            }
//            if (attributes.elementAt(i) != null && !"".equals(attributes.elementAt(i)))   {
//
//            } else {
//				if (i == MRTConstants.ATTRIBUTE_LOCAL_PREF || i == MRTConstants.ATTRIBUTE_MULTI_EXIT){
//                    toStr.append("\t\t<attribute").append(" type="+"\""+type+"\""+">").append(attributes.elementAt(i).toString()).append("</attribute>\n");
//
//                    //toStr.append("\t\t<attribute>").append("0").append("</attribute>\n");
//                } else if (i == MRTConstants.ATTRIBUTE_ATOMIC_AGGREGATE) {
//                    toStr.append("\t\t<attribute>").append("NAG").append("</attribute>\n");
//                }
//			}
        }
        toStr.append("\t</attributes>\n");
        return toStr.toString();
    }

    private static Attributes getTableDumpAttributes(TableDump tableDump){
        try {
            Field field = tableDump.getClass().getDeclaredField("attributes");
            field.setAccessible(true);
            return (Attributes) field.get(tableDump);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

	private static void usage() {
		PrintStream ps = System.err;

		ps.println("route_btoa <options> f1 ...");
		ps.println("  -h        print this help message");
		ps.println("  -m        legacy compatibility wth MRT: include all records");
		ps.println("  -4        print IPv4 prefixes only");
		ps.println("  -6        print IPv6 prefixes only");
		ps.println("  -p peer   print updates from a specific peer only");
		ps.println("  -P prefix print updates for a specific prefix only");
		if (Debug.compileDebug)
			ps.println("  -D        enable debugging");
		ps.println("  -o as     print updates generated by one AS only");
		ps.println("  -t as     print updates where AS is in ASPATH");
        ps.println(" -f fName   dump to a file with name FileName");
        ps.println(" -x         dump in a XML file");
        ps.println("         -4 and -6 together are not allowed");
		ps.println(" f1 ... are filenames or URL's");
        ps.println(" Use URL's according to the server's policies");
		ps.println(" Only prints records in machine readable format\n");

	}

}
