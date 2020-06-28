package com.example.community.controller;

import com.example.community.entity.AccessTokenParam;
import com.example.community.entity.GitHubUser;
import com.example.community.entity.User;
import com.example.community.mapper.UserMapper;
import com.example.community.provider.GitHubProvider;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizationController {

    @Autowired
    GitHubProvider gitHubProvider;

    @Autowired
    UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state, HttpServletResponse response){

        AccessTokenParam accessTokenParam = new AccessTokenParam();
        accessTokenParam.setCode(code);
        accessTokenParam.setState(state);
        accessTokenParam.setClient_id("6762eac77f372f8d64d2");
        accessTokenParam.setClient_secret("23a98015a81e55441816f76ff6eae77ecd65c579");
        accessTokenParam.setRedirect_uri("http://localhost:8083/callback");
        String Token = gitHubProvider.getAccessToken(accessTokenParam); //获取到accessToken
        String accessToken = Token.split("&")[0].split("=")[1];
        //使用access_token获取用户信息
        GitHubUser gitHubUser = gitHubProvider.getGitHubUser(accessToken);
       // System.out.println(gitHubUser.getName());

        if (gitHubUser != null){
            String token = UUID.randomUUID().toString();
            //根据accountId查询是否存在该用户
            User selectUser = userMapper.selectUserByAccountId(gitHubUser.getId());
            //如果存在就更新token，用户名及修改时间
            if (selectUser != null){
                selectUser.setToken(token);
                if (gitHubUser.getName() != null && selectUser.getName() != null && !gitHubUser.getName().equals(selectUser.getName())){
                    selectUser.setName(gitHubUser.getName());
                }

                selectUser.setModifiedTime(System.currentTimeMillis());
                userMapper.updateUser(selectUser);
            }else{
               //如果不存在添加新的用户信息
                User user = new User();
                user.setName(gitHubUser.getName());
                user.setAccountId(gitHubUser.getId());
                user.setToken(token);
                user.setCreateTime(System.currentTimeMillis());
                user.setModifiedTime(user.getCreateTime());
                userMapper.addUser(user);
            }

            response.addCookie(new Cookie("token",token));
        }

        System.out.println(gitHubUser.getName());

        return "redirect:/";
    }
}
