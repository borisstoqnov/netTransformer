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

package net.itransformers.assertions;

import net.itransformers.utils.CmdLineParser;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 6/7/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class AssertionExecutor {
    public AssertionResult[] execute(File assertionsConfig, AssertionLevel level) {
        return null;
    }
    public AssertionResult[] execute(Assertion[] assertions, AssertionLevel level){
        return null;
    }

    public static void main(String[] args) {
//        Map<String, String> params = CmdLineParser.parseCmdLine(args);
//        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("assertions-config.xml");
//           NetworkDiscoverer discoverer = applicationContext.getBean("sdnDiscovery", NetworkDiscoverer.class);
//        AssertionExecutor assertionExecutor = applicationContext.getBean("assertionExecutor", AssertionExecutor.class);
//        assertionExecutor.execute()

    }
}
