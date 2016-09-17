package net.itransformers.ws.resourceManager;

import net.itransformers.resourcemanager.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value="/resource")
public class ResourceManagerController {
    final Logger logger = LoggerFactory.getLogger(ResourceManagerController.class);

    @Resource(name="resourceManager")
    private ResourceManager resourceManager;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<String> getResources() {
        return resourceManager.getResources();
    }
}
