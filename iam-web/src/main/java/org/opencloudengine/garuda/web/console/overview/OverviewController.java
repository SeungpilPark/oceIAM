package org.opencloudengine.garuda.web.console.overview;

import org.opencloudengine.garuda.common.exception.ServiceException;
import org.opencloudengine.garuda.common.security.SessionUtils;
import org.opencloudengine.garuda.web.management.Management;
import org.opencloudengine.garuda.web.management.ManagementService;
import org.opencloudengine.garuda.web.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/console")
public class OverviewController {
    @Autowired
    @Qualifier("config")
    private Properties config;

    @Autowired
    private UserService userService;

    @Autowired
    private ManagementService managementService;

    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView list(HttpSession session) {

        try {
            Management management = (Management) session.getAttribute("management");
            if(management == null){
                ModelAndView mav = new ModelAndView("/management/list");
                List<Management> managements = managementService.selectByUserId(SessionUtils.getId());
                mav.addObject("managements", managements);
                return mav;
            }
            ModelAndView mav = new ModelAndView("/console/overview");
            mav.addObject("management", management);
            return mav;

        } catch (Exception ex) {
            throw new ServiceException("Invalid Server Error");
        }
    }
}
