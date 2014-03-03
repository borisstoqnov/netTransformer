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