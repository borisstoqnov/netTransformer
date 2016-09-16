package net.itransformers.ws.resourceManager;

//import net.itransformers.resourcemanager.ResourceManager;
//import net.itransformers.resourcemanager.config.ParamType;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import javax.annotation.Resource;
//
//@Controller
//@RequestMapping(value="/resource")
public class ResourceManagerController {
//    final Logger logger = Logger.getLogger(ResourceManagerController.class);
//
//    @Resource(name="resourceManager")
//    private ResourceManager resourceManager;
//
////    @RequestMapping(value = "/listdata", method = RequestMethod.GET)
////    @ResponseBody
////    public Contacts listData() {
////        return new Contacts(contactService.findAll());
////    }
////
////    @RequestMapping(value="/{id}", method=RequestMethod.GET)
////    @ResponseBody
////    public Contact findContactById(@PathVariable Long id) {
////        return contactService.findById(id);
////    }
//
//    @RequestMapping(value="/findResource", method=RequestMethod.GET)
//    public String findResource() {
//        return "hello";
//    }
//
//    @RequestMapping(value="/", method=RequestMethod.POST)
//    public void createResource(@RequestBody String resourceName) {
//        logger.info("Creating resource: " + resourceName);
//        resourceManager.createResource(resourceName);
//    }
//
//    @RequestMapping(value="/selectionParam", method=RequestMethod.POST)
//    public void createSelectionParam(@RequestBody String resourceName, @RequestBody ParamType resourceParamType) {
//        logger.info("resourceManager.createSelectionParam: " + resourceName);
//        resourceManager.createSelectionParam(resourceName, resourceParamType);
//    }
//
////    @RequestMapping(value="/{id}", method=RequestMethod.PUT)
////    @ResponseBody
////    public void update(@RequestBody Contact contact,
////                       @PathVariable Long id) {
////        logger.info("Updating contact: " + contact);
////        contactService.save(contact);
////        logger.info("Contact updated successfully with info: " + contact);
////    }
////
////    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
////    @ResponseBody
////    public void delete(@PathVariable Long id) {
////        logger.info("Deleting contact with id: " + id);
////        Contact contact = contactService.findById(id);
////        contactService.delete(contact);
////        logger.info("Contact deleted successfully");
////    }
}
