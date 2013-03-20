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

package net.itransformers.topologyviewer.parameterfactory;

import net.itransformers.topologyviewer.parameterfactory.config.*;
import net.itransformers.topologyviewer.parameterfactory.util.JaxbMarshalar;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterFactoryBuilder {
    private Map<String,ParamFactoryType> parameterFactoryTypeMap = new HashMap<String, ParamFactoryType>();
    private Map<String,TypeType> parameterFactoryElementTypesMap = new HashMap<String, TypeType>();
    private File configFile;

    public ParameterFactoryBuilder(File configFile) throws JAXBException, IOException {
        this.configFile = configFile;
        FileInputStream is = null;
        try {
            is = new FileInputStream(configFile);
            ParamFactoriesType factoriesType = JaxbMarshalar.unmarshal(ParamFactoriesType.class, is);
            for (ParamFactoryType paramFactoryType : factoriesType.getParamFactory()){
                parameterFactoryTypeMap.put(paramFactoryType.getName(),paramFactoryType);
            }
            for (TypeType typeType : factoriesType.getParamFactoryElementTypes().getType()) {
                parameterFactoryElementTypesMap.put(typeType.getName(), typeType);
            }
        } finally {
            if (is != null) is.close();
        }
    }

    public ParameterFactory buildParameterFactory(String factoryName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        final ParamFactoryType paramFactoryType = parameterFactoryTypeMap.get(factoryName);
        if (paramFactoryType == null) {
            throw new RuntimeException(String.format("Can not find parameter factory '%s' in file : %s",factoryName,configFile.getAbsolutePath()));
        }
        List<ParamFactoryElementType> factoryElements = paramFactoryType.getParamFactoryElement();
        List<ParameterFactoryElement> factoryElementList = new ArrayList<ParameterFactoryElement>();
        for (ParamFactoryElementType paramFactoryElementType : factoryElements) {
            String factoryType = paramFactoryElementType.getType();
            final TypeType typeType = parameterFactoryElementTypesMap.get(factoryType);
            if (typeType == null){
                throw new RuntimeException("type for factory element type="+factoryType);
            }
            String factoryElementClazz = typeType.getClazz();
            Class<?> clazz = Class.forName(factoryElementClazz);
            ParameterFactoryElement factoryElement = (ParameterFactoryElement)clazz.newInstance();
            List<ParamType> params = typeType.getParam();
            Map<String, String> paramsMap = new HashMap<String, String>();
            for (ParamType param : params) {
                paramsMap.put(param.getName(),param.getValue());
            }
            Map<String, String> elementParams = new HashMap<String, String>();
            List<ParamType> elementParamsType = paramFactoryElementType.getParam();
            for (ParamType elementParam : elementParamsType) {
                elementParams.put(elementParam.getName(),elementParam.getValue());
            }
            factoryElement.init(paramsMap,elementParams);
            factoryElementList.add(factoryElement);
        }
        return new ParameterFactory(factoryElementList);
    }

}
