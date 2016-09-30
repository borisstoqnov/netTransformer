package net.itransformers.idiscover.v2.core.factory.spring;

import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.NetworkDiscovererFactory;
import net.itransformers.idiscover.v2.core.AsyncNetworkDiscoverer;
import net.itransformers.idiscover.v2.core.version_manager.DirectoryVersionManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.util.Map;

/**
 * Created by vasko on 9/30/2016.
 */
public class SpringBasedNetworkDiscovererFactory implements NetworkDiscovererFactory{

    @Override
    public NetworkDiscoverer createNetworkDiscoverer(String type, Map<String, String> properties) {
        NetworkDiscoverer discoverer = createParallelNodeDiscoverer(properties);
        if ("async_parallel".equals(type)) {
            return new AsyncNetworkDiscoverer(discoverer);
        } else {
            return discoverer;
        }
    }

    private NetworkDiscoverer createParallelNodeDiscoverer(Map<String, String> properties) {
        String projectPath = properties.get("projectPath");
        if (projectPath == null) {
            throw new IllegalArgumentException("Missing projectPath parameter");
        }
        String version = properties.get("version");
        if (version == null) {
            throw new IllegalArgumentException("Missing version parameter");
        }
        return createNetworkDiscoverer(projectPath, version, -1, -1);
    }

    private NetworkDiscoverer createNetworkDiscoverer(String projectPath, String version, int initialNumberOfThreads, int maxNumberOfThreads) {

        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:netDiscoverer/netDiscoverer.xml");
        AbstractBeanDefinition projectPathBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(String.class)
                .addConstructorArgValue(projectPath).getBeanDefinition();

        BeanDefinition labelDirNameBeanDefinition = BeanDefinitionBuilder.
                rootBeanDefinition(String.class)
                .addConstructorArgValue(version).getBeanDefinition();

        ctx.registerBeanDefinition("projectPath", projectPathBeanDefinition);
        ctx.registerBeanDefinition("labelDirName", labelDirNameBeanDefinition);
        ctx.refresh();

        NetworkDiscoverer discoverer = ctx.getBean("parallelSnmpDiscovery", NetworkDiscoverer.class);

        return  discoverer;

    }

}
