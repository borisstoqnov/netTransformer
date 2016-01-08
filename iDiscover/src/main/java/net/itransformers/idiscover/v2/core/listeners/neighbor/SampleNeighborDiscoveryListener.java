package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.v2.core.NeighborDiscoveryListener;
import net.itransformers.idiscover.v2.core.NeighborDiscoveryResult;
import net.itransformers.idiscover.v2.core.listeners.node.SampleNodeDiscoveryListener;
import org.apache.log4j.Logger;

/**
 * Created by niau on 1/7/16.
 */
public class SampleNeighborDiscoveryListener implements NeighborDiscoveryListener {
    static Logger logger = Logger.getLogger(SampleNodeDiscoveryListener.class);

    @Override
    public void neighborDiscovered(NeighborDiscoveryResult discoveryResult) {

        logger.info("Neighbor has been discovered" + discoveryResult);


    }
}
