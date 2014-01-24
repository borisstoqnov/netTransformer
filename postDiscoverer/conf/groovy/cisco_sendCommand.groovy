
/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 1/23/14
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */


prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r"
logedIn = "false"
logedInPowerMode = "false"
logedInConfigMode = "false"
hostname = ""
status = ["success": 1, "failure": 2]

def result = sendCommand()

return result

def sendCommand() {
    def returnFlag = 2
    def result = null
    send (params["command"] + defaultTerminator)

    expect _re(powerUserPrompt + "\$") {
        returnFlag = status["success"]
        result = it.getBuffer()
    }
    return ["status": returnFlag, "data": result]


}