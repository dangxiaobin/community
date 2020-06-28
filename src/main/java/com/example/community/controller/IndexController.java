package com.example.community.controller;

import com.example.community.entity.User;
import com.example.community.mapper.UserMapper;
import org.omg.CORBA.UnknownUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller //主要用于spring容器识别这是一个前端控制页面，用于页面跳转等
public class IndexController {

    @Autowired
    UserMapper userMapper;



    @GetMapping("/") //路由
    public String index(HttpServletRequest request){
        //在访问主页时获取cookies中的token,查询数据库中的用户信息token,如果存在就把该用户设置到session中
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){

            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())){
                    String token = cookie.getValue();
                    User user = userMapper.selectUserByToken(token);
                    if (user != null){
                        request.getSession().setAttribute("user",user);
                    }
                }
            }
        }
       // model.addAttribute("name",name); //将浏览器传来的参数设置到model中，用于后端页面获取
        return "index"; //默认将跳转到resources/templates/hello.html页面
    }
}
