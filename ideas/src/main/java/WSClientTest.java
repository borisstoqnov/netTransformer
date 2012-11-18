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

import net.itransformers.ws.upload.Exception_Exception;
import net.itransformers.ws.upload.Node;
import net.itransformers.ws.upload.TreeImporterImplService;

public class WSClientTest {
    public static void main(String[] args) throws Exception_Exception {
        net.itransformers.ws.upload.TreeImporter  port = new TreeImporterImplService().getTreeImporterImplPort();
        System.out.println(port.importNode(5L, new Node()));
    }
}
