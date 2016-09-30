package net.itransformers.idiscover.v2.core.factory.spring;

import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.NetworkDiscovererFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Created by vasko on 9/30/2016.
 */
public class SpringBasedNetworkDiscovererFactory implements NetworkDiscovererFactory{

    @Override
    public NetworkDiscoverer createNetworkDiscoverer(String projectPath, String version) {
        return this.createNetworkDiscoverer(projectPath, version, -1, -1);
    }

    @Override
    public NetworkDiscoverer createNetworkDiscoverer(String projectPath, String version, int initialNumberOfThreads, int maxNumberOfThreads) {
        // TODO set number of threads
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
