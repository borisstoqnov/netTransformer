package net.itransformers.topologyviewer.fulfilmentfactory.util;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public class JaxbMarshalar {
    public static <T> void marshal(T obj, OutputStream outputStream, String rootTagName) throws JAXBException {
        Class docClass = obj.getClass();
        String packageName = docClass.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal( new JAXBElement<T>(new QName("",rootTagName), docClass, obj), outputStream );
    }

    @SuppressWarnings("unchecked")
    public static <T> T unmarshal( Class<T> docClass, InputStream inputStream ) throws JAXBException {
        String packageName = docClass.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<T> doc = (JAXBElement<T>)u.unmarshal( new StreamSource(inputStream), docClass );
        return doc.getValue();
    }

}
