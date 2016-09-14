package net.itransformers.topologyviewer.rightclick.spring;

import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import net.itransformers.topologyviewer.rightclick.RightClickHandlerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Created by vasko on 9/14/2016.
 */
public class SpringAwareRightClickHandlerFactory implements RightClickHandlerFactory, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public RightClickHandler createRightClickHandler(String name) {
        return (RightClickHandler) beanFactory.getBean(name);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
