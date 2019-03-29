package com.hpu.study_plan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RequestMapping("/group")
@RestController
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @RequestMapping(value="/", method= RequestMethod.GET)
    public ModelAndView groupCreateUI(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String phoneNumber = session.getId();

        logger.info("进入首页");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("group/create");

        return modelAndView;
    }



}
