import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4groovy.cliconnection.CLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.TelnetCLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.RawSocketCLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.SshCLIConnection

println(params)
//CLIConnection conn
////if (params["protocol"] == "telnet") {
//    conn = new TelnetCLIConnection()
////} else if (params["protocol"] == "raw") {
////    conn = new RawSocketCLIConnection()
////} else {
//    conn = new SshCLIConnection()
////}
//
//conn.connect(params)

//Expect4Groovy.createBindings(conn, getBinding(), true)

prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r"
logedIn = "false"
logedInPowerMode = "false"
logedInConfigMode = "false"
hostname = ""
status = ["success": 1, "failure": 2]

def result;
if (login() == status["success"]) {
    if (setupTerminal() == status["success"]) {

      result = sendCommand()
    }
} else {
    result = ["status": 2, "data": "Login Failure"]
}

exit()

return result


def login() {
    if (params["protocol"] != "ssh") {
        if (user() == status["success"]) {
            if (password() == status["success"]) {
                if (showPrivilege() == status["success"]) {
                    println("Login Successfull")
                    return status["success"]
                } else {
                    if (enablePassword() == status["success"]) {
                        println("Login Successfull")
                        return status["success"]
                    } else {
                        return status["failure"]
                    }
                }

            } else {
                return status["failure"]

            }
        } else {
            return status["failure"]
        }
    } else {
        if (enablePassword() == status["success"]) {
            println("Login Successfull")
            return status["success"]
        } else {
            return status["failure"]
        }
    }
}

def user() {
    def returnFlag = 2;

    expect _re("Username:|User:") {
        send (params["username"] + defaultTerminator)
        returnFlag = status["success"]
    }
    if (returnFlag != status["success"]) {
        println "Invalid Username!"
        return status["failure"]
    } else {
        return status["success"]
    }

}

def password() {
    def returnFlag = 2;

    expect("Password:") {
        send (params["password"] + defaultTerminator)
        expect _re("(" + prompt + "\$)" + "|" + "(" + powerUserPrompt + "\$" + ")") {
            //  println("Password submitted successfully")
            returnFlag = status["success"]
            //   println("Password submitted successfully: "+returnFlag)
        }
    }
    //  println("Password submitted successfully2: "+returnFlag)

    return returnFlag;
}

def showPrivilege() {
    def returnFlag = 2
    send ("show privilege " + defaultTerminator)

    expect _re("Current privilege level is (\\d)+") {
        if (it.getMatch(1)=="15"){
            returnFlag = status["success"]

        }  else {
            returnFlag = status["failure"]

        }
    }
    return returnFlag
}

def enablePassword() {
    def returnFlag = 2
     expect _re("(" + prompt + "\$)" + "|" + "(" + powerUserPrompt + "\$" + ")") {
        send ("enable" + defaultTerminator)
        expect("Password:") {
            send (params["enable-password"] + defaultTerminator)
            expect _re(powerUserPrompt + "\$") {
                logedInPowerMode = "true"
//                send "terminal length 0" + defaultTerminator
                returnFlag = status["success"]
            }
        }
   }
    return returnFlag

}

def setupTerminal() {
    def returnFlag = 2
    send ("terminal length 0" + defaultTerminator)

    expect _re("[\r][\n]((([^"+powerUserPrompt+"]+)" + powerUserPrompt + "\$)" + "|" + "(([^"+prompt+"]+)" + prompt + "\$" + "))") {
        def match1 = it.getMatch(2)
        def match2 = it.getMatch(4)

        if (match1 == null){
            hostname = match2
            logedIn = "true"
            returnFlag = status["success"]

        }else {
            hostname = match1
            logedInPowerMode = "true"
            returnFlag = status["success"]
        }

    }
    return returnFlag
}

def sendCommand() {
//Send verification command
    def returnFlag = 2
    def result = null
        //    send "show run" + "\r"
   send (params["command"] + defaultTerminator)

    expect _re(powerUserPrompt + "\$") {
        returnFlag = status["success"]
        result = it.getBuffer()
    }
    return ["status": returnFlag, "data": result]


}

//Log in configuration mode
def sendConfigCommand() {

    if (logedInPowerMode == "true") {
        send ("configure terminal" + defaultTerminator)
        expect _re(powerUserPrompt + "\$") {
            logedInConfigMode = "true"
        }


    } else {
        println("Error Not logged in Power User Mode!")
    }

//Send Configuration command
    if (logedInConfigMode == "true") {
        send (params["command"] + defaultTerminator)
        expect _re("% Invalid input detected") {
            println("Error! " + "The command \"" + params["command"] + "\" is producing the following error: " + it.getBuffer())
        }

        expect _re(powerUserPrompt + "\$") {
            status = "1"
        }


    } else {
        println("Error Not logged in Configuration User Mode!")

    }
}


def exit(){
    send ("exit" + defaultTerminator)
    expect eof()

}

