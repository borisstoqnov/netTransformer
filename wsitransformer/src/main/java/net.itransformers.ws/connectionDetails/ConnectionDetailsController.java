package net.itransformers.ws.connectionDetails;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by niau on 9/23/16.
 */
@Controller
@RequestMapping(value="/connections")
public class ConnectionDetailsController {
    final Logger logger = LoggerFactory.getLogger(ConnectionDetailsController.class);
    @Resource(name="connectionList")
    private ConnectionDetailsManager connectionDetailsAPI;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public LinkedHashMap getConnectionDetails() {
        return (LinkedHashMap) connectionDetailsAPI.getConnectionDetails();
    }

    @RequestMapping(value = "/", method=RequestMethod.POST)
    @ResponseBody
    public void createConnection(@RequestBody String name, @RequestBody ConnectionDetails connectionDetails){
        connectionDetailsAPI.createConnection(name,connectionDetails);
    }
    @RequestMapping(value = "/{connectionDetailsName}", method=RequestMethod.GET)
    @ResponseBody
    public ConnectionDetails getConnectionDetail(@PathVariable String connectionDetailsName) {
        return connectionDetailsAPI.getConnection(connectionDetailsName);
    }

    @RequestMapping(value = "/{connectionDetailsName}", method=RequestMethod.PUT)
    @ResponseBody
    public void updateConnection(@PathVariable String connectionDetailsName, @RequestBody String newConnectionDetailName){
       connectionDetailsAPI.updateConnection(connectionDetailsName,newConnectionDetailName);
    }
    @RequestMapping(value = "/{connectionDetailsName}", method=RequestMethod.DELETE)
    @ResponseBody

    public void deleteConnection(@PathVariable String connectionDetailsName){
         connectionDetailsAPI.deleteConnection(connectionDetailsName);
    }


    @RequestMapping(value = "/{connectionDetailsName}/type", method=RequestMethod.GET)
    @ResponseBody
    String createConnectionType(@PathVariable String connectionDetailsName){
        return connectionDetailsAPI.getConnection(connectionDetailsName).getConnectionType();
    }


    @RequestMapping(value = "/{connectionDetailsName}/type", method=RequestMethod.POST)
    @ResponseBody
    void createConnectionType(@PathVariable String connectionDetailsName ,@RequestBody String connectionDetailsType){
        connectionDetailsAPI.getConnection(connectionDetailsName).setConnectionType(connectionDetailsType);
    }

    @RequestMapping(value = "/{connectionDetailsName}/type", method=RequestMethod.PUT)
    @ResponseBody
    void updateConnectionType(@PathVariable String connectionDetailsName ,@RequestBody String connectionDetailsType){
        connectionDetailsAPI.getConnection(connectionDetailsName).setConnectionType(connectionDetailsType);
    }

    @RequestMapping(value = "/{connectionDetailsName}/params", method=RequestMethod.POST)
    @ResponseBody
    void createConnectionParam(@PathVariable String connectionDetailsName, @RequestBody String paramName, @RequestBody String paramValue){
       Map<String,String> params = connectionDetailsAPI.getConnection(connectionDetailsName).getParams();
       params.put(paramName, paramValue);
    }
    @RequestMapping(value = "/{connectionDetailsName}/params/{paramName}", method=RequestMethod.PUT)
    @ResponseBody
    void updateConnectionParam(@PathVariable String connectionDetailsName, @PathVariable String paramName, @RequestBody String paramValue){
        Map<String,String> params = connectionDetailsAPI.getConnection(connectionDetailsName).getParams();
        params.put(paramName,paramValue);

    }
    @RequestMapping(value = "/{connectionDetailsName}/params/{paramName}", method=RequestMethod.DELETE)
    @ResponseBody
    void deleteConnectionParam(@PathVariable String connectionDetailsName, @PathVariable String paramName){
       connectionDetailsAPI.getConnection(connectionDetailsName).getParams().remove(paramName);
    }
    @RequestMapping(value = "/{connectionDetailsName}/params/{paramName}", method=RequestMethod.GET)
    @ResponseBody
    String getConnectionParam(@PathVariable String connectionDetailsName, @PathVariable String paramName){
        return  connectionDetailsAPI.getConnection(connectionDetailsName).getParams().get(paramName);
    }
    @RequestMapping(value = "/{connectionDetailsName}/params", method=RequestMethod.GET)
    @ResponseBody
    Map<String, String> getConnectionParams(@PathVariable String connectionDetailsName){
        return  connectionDetailsAPI.getConnection(connectionDetailsName).getParams();

    }

}
