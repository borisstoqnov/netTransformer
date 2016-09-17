package net.itransformers.idiscover.v2.core.listeners.graphmlRenderer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class GraphmlRenderer


{
    public String render(String template, HashMap<String,Object> parameters) throws Exception
    {

        /*  first, get and initialize an engine  */
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        /*  next, get the Template  */
        Template t = ve.getTemplate( template );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        for (Map.Entry<String,Object> entry : parameters.entrySet()) {
            context.put(entry.getKey(),entry.getValue());
        }
        /* now render the template into a StringWriter */
        StringWriter writer = new StringWriter();
        t.merge( context, writer );
        /* show the World */
        return writer.toString();
    }
}