/*
 * cisco_ip_interfaces_eval.groovy
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

import net.itransformers.utils.TableUtil
import com.sun.xml.internal.fastinfoset.util.StringArray


status = ["success": 1, "failure": 2]

prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r"


def evalresult = [:]
def result = status["failure"]
String[] columns;
Integer [] columnPositions;
//println (System.currentTimeMillis())

//expect _re(params["hostname"]+powerUserPrompt+defaultTerminator) {
//
//}

expect ([
        _re("([^\n]*)\r\n") {    String headerLine = it.getMatch(1)
            columns = TableUtil.getColumnNames(headerLine," ");
            columnPositions = TableUtil.getColumnIndexes(headerLine,columns);
            println("cols------->"+Arrays.asList(columns))
            result = status["success"]

        }
])
println (System.currentTimeMillis())

expect ([
        _re("([^\n]*)\r\n") {
            String row = it.getMatch(1)
            String [] rowContents = TableUtil.getCells(columnPositions, row)
            println("----------->"+Arrays.asList(rowContents))
            it.exp_continue()
        },
        _gl(powerUserPrompt) {
            //String row = it.getMatch(1)
        }
])
println (System.currentTimeMillis())

if (result == status["success"]) {
    evalresult.put("ip interfaces", ["status": status["success"], "message":   " evaluation sucessful!"])
} else {
    evalresult.put("ip interfaces", ["status": status["failure"], "message":  " evaluation failed!"])
}

return evalresult