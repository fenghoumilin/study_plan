package com.hpu.study_plan.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.hpu.study_plan.service.UserService;
import com.hpu.study_plan.utils.RequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/user")
public class UserController {


    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value="/registerUI", method= RequestMethod.GET)
    public ModelAndView userRegisterUI() {

        logger.info("进入注册页面");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/register");

        return modelAndView;
    }

    @RequestMapping(value="/update", method= RequestMethod.GET)
    public ModelAndView userUpdate() {



        userService.updatePassword(1, "111111");


        logger.info("进入注册页面");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        return modelAndView;
    }

    @RequestMapping(value="/register", method= RequestMethod.POST)
    public ModelAndView userRegister(HttpServletRequest request, HttpServletResponse response) {

        String nick = request.getParameter("nick");
        String password = request.getParameter("password");
        logger.info("nick = {}, password = {}", nick, password);


        logger.info("sssss");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("success");
        logger.info("ok");

        return modelAndView;
    }

    @RequestMapping(value="/login", method= RequestMethod.POST)
    public String userLogin(HttpServletRequest request, HttpServletResponse response) {

        String name = request.getParameter("username");
        String password = request.getParameter("password");




        JsonNode requestJson = RequestParser.getPostParameter(request);


        return "";
    }

    @RequestMapping(value="/loginOut", method= RequestMethod.POST)
    public String userLoginOut(HttpServletRequest request, HttpServletResponse response) {

        JsonNode requestJson = RequestParser.getPostParameter(request);


        return "";
    }

}
