/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.StringReader;

import ch.ethz.mxquery.query.XQCompiler;
import ch.ethz.mxquery.query.PreparedStatement;
import ch.ethz.mxquery.query.impl.CompilerImpl;
import ch.ethz.mxquery.contextConfig.CompilerOptions;
import ch.ethz.mxquery.contextConfig.Context;
import ch.ethz.mxquery.datamodel.QName;
import ch.ethz.mxquery.exceptions.MXQueryException;
import ch.ethz.mxquery.exceptions.QueryLocation;
import ch.ethz.mxquery.model.XDMIterator;
import ch.ethz.mxquery.xdmio.XDMInputFactory;
import ch.ethz.mxquery.xdmio.XDMSerializer;
import ch.ethz.mxquery.xdmio.XMLSource;


public class MXQueryTest {
    public static void main(String [] args) throws Exception {
        //Sample query, replace by your own
//        String query = "declare variable $input external; <result>{$input,$input}</result>";

        String query="\tcopy $x := <doc><el><node>this node is deleted</node></el></doc> \n" +
                "\tmodify \n" +
                "\t(\n" +
                "\t\tdelete node $x/el/node, \n" +
                "\t\tinsert node <node>this node is inserted</node> into $x/el\n" +
                "\t)\n" +
                "\treturn $x/el\n";
        boolean updateFiles = true;

        // Create new (unified) Context
        Context ctx = new Context();
        // Create a compiler options oh
        CompilerOptions co = new CompilerOptions();
        // Enable schema support
        co.setSchemaAwareness(true);
        // Enable update facility support
        co.setUpdate(true);
        // use updateable stores by default
        ctx.getStores().setUseUpdateStores(true);
        ctx.getStores().setSerializeStores(updateFiles);
        // create a XQuery compiler
        XQCompiler compiler = new CompilerImpl();
        PreparedStatement statement;

        try {
            //out of the context and the query "text" create a prepared statement,
            // considering the compiler options
            statement =  compiler.compile(ctx, query,co);
            XDMIterator result;
            String strResult = "";
            // Get an iterator from the prepared statement
            // Set up dynamic context values, e.g., current time
            result = statement.evaluate();
            // Add the contents of the external variable
            String xml = "<FitnessCenter>\n" +
                    "    <Member Level=\"platinum\">\n" +
                    "        <Name>Jeff</Name>\n" +
                    "        <FavoriteColor>lightgrey</FavoriteColor>\n" +
                    "    </Member>\n" +
                    "    <Member Level=\"gold\">\n" +
                    "        <Name>David</Name>\n" +
                    "        <FavoriteColor>lightblue</FavoriteColor>\n" +
                    "    </Member>\n" +
                    "    <Member Level=\"platinum\">\n" +
                    "        <Name>Roger</Name>\n" +
                    "        <FavoriteColor>lightyellow</FavoriteColor>\n" +
                    "    </Member>\n" +
                    "</FitnessCenter>\n";
            XMLSource xmlIt = XDMInputFactory.createXMLInput(result.getContext(), new StringReader(xml), true, Context.NO_VALIDATION, QueryLocation.OUTSIDE_QUERY_LOC);
            statement.addExternalResource(new QName("input"), xmlIt);
            // Create an XDM serializer, can take an XDMSerializerSettings object if needed
            XDMSerializer ip = new XDMSerializer();
            // run expression, generate XDM instance and serialize into String format
            strResult = ip.eventsToXML(result);
            // XQuery Update "programs" create a pending update list, not a normal result
            // apply the results to the relevant "stores"
            // currently in-memory stores
            statement.applyPUL();
            // serialize the contents of modified stores to disk
            // in this case, do not make a backup of the modified files
            if (updateFiles)
                statement.serializeStores(false);
            // Release all resources associated with the statement
            statement.close();

            System.out.println(strResult);
        } catch (MXQueryException err) {
            MXQueryException.printErrorPosition(query, err.getLocation());
            System.err.println("Error:");
            throw err;
        }

    }
}
