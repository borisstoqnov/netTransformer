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

import net.itransformers.ideas.*;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class StrategiesBindingTestCase {
    @Test
    public void testBinding() throws IOException, JAXBException {
        String xml = FileUtils.readFileToString(new File("ideas/src/main/resource/xml/strategies.xml"));
        InputStream is1 = new ByteArrayInputStream(xml.getBytes());
        StrategiesType strategiesType = net.itransformers.resourcemanager.util.JaxbMarshalar.unmarshal(StrategiesType.class, is1);
        List<StrategyType> strategyList = strategiesType.getStrategy();
        Assert.assertEquals(strategyList.size(),2);
        StrategyType strategy = strategyList.get(0);
        Assert.assertEquals(strategy.getName(),"str1");
        List<StepType> stepsList = strategy.getStep();
        Assert.assertEquals(stepsList.size(),3);
        Assert.assertEquals(stepsList.size(),3);
        StepType step = stepsList.get(0);
        Assert.assertEquals(step.getName(),"replace P");
        BusinessConstraintsType bconstraints = step.getBusinessConstraints();
        List<BusinessConstraintType> bconstraintList = bconstraints.getBusinessConstraint();
        Assert.assertEquals(bconstraintList.size(),3);
        BusinessConstraintType bconstraint = bconstraintList.get(0);
        Assert.assertEquals(bconstraint.getName(),"Risk");
        Assert.assertEquals(bconstraint.getValue(),"High");

    }
}
