package net.itransformers.mrtdumper;

import org.javamrt.mrt.AS;
import org.javamrt.mrt.MRTRecord;
import org.javamrt.mrt.Prefix;

import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 8/9/14
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class Checker {

    private Prefix prefix = null;
    private InetAddress peer = null;
    private AS originator = null;
    private AS traverses = null;
    private MRTRecord mrt;
    public Checker(Prefix prefix, InetAddress peer, AS originator, AS traverses, MRTRecord mrt) {
        this.prefix = prefix;
        this.peer = peer;
        this.originator = originator;
        this.traverses = traverses;
        this.mrt = mrt;
    }

    public  boolean checkPrefix() {

        if (prefix == null)
            return true;
        return prefix.equals(mrt.getPrefix());
    }

    public boolean checkPeer() {
        if (peer == null)
            return true;
        return peer.equals(mrt.getPeer());
    }

    public boolean checkASPath() {
        if (originator == null) {
            if (traverses == null)
                return true;
            //
            // check whether AS is traversed by the prefix
            //
            return mrt.getASPath().contains(traverses);
        }
        //
        // check whether the AS originates the prefix
        //
        return originator.equals(mrt.getASPath().generator());
    }
}
