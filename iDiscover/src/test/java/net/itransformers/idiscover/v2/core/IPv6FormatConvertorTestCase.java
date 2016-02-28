/*
 * IPv6FormatConvertorTestCase.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.idiscover.v2.core;

import org.junit.Assert;
import org.junit.Test;

import static net.itransformers.idiscover.util.IPv6formatConvertor.IPv6Convertor;

/**
 * Created by niki on 4/9/2014.
 */
public class IPv6FormatConvertorTestCase {
    @Test
    public void simpleConversion1(){
        final String oldS =   "10.0.10.0.17.0.1.0.0.0.0.0.0.0.0.0.13";
        String newS1 = IPv6Convertor(oldS);
        Assert.assertEquals("IPv6 Addresses are different","10:10:17:1::13",newS1);

    }
    @Test
    public void simpleConversion2(){
        final String oldS = "10.FE.80.0.0.0.0.0.0.C.0.0.FF.FE.0.65.0";

        String newS1 = IPv6Convertor(oldS);
        Assert.assertEquals("IPv6 Addresses are different","10:FE80::C00:FF:FE00:6500",newS1);

    }
    @Test
    public void simpleConversion3(){
        final String oldS = "FE.0.0.0.0.0.0.0.0.A.0.0.0.0.0";

        String newS1 = IPv6Convertor(oldS);
        Assert.assertEquals("IPv6 Addresses are different","FE::A00::",newS1);

    }
    @Test
    public void simpleConversion4(){
        final String oldS = "2.10.20.1.4.70.1F.B.A.BD.0.0.0.5.0.13.0.0";

        String newS1 = IPv6Convertor(oldS);
        Assert.assertEquals("IPv6 Addresses are different","210:2001:470:1F0B:ABD::5:13::",newS1);

    }

}
