package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

/**
 * Created by vasko on 08.01.16.
 */
public class MockNodeDiscoveryFilter implements NodeDiscoverFilter{
    @Override
    public boolean match(ConnectionDetails details) {
        return false;
    }
}
