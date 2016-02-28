/*
 * FulfilmentAdapter.java
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

package net.itransformers.topologyviewer.fulfilmentfactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class FulfilmentAdapter {
    private Fulfilment fulfilment;
    private Map<String,String> parameters;
    private Map<String, String> fulfilmentFactoryParams;
    private Logger logger;
    private File projectPath;

    public FulfilmentAdapter(File projectPath, Fulfilment fulfilment, Map<String, String> parameters,
                             Map<String, String> fulfilmentFactoryParams, Logger logger) {
        this.projectPath = projectPath;
        this.fulfilment = fulfilment;
        this.parameters = parameters;
        this.fulfilmentFactoryParams = fulfilmentFactoryParams;
        this.logger = logger;
    }
    public void fulfil() throws IOException {
        fulfilment.fulfil(projectPath, parameters, fulfilmentFactoryParams, logger);
    }
}
