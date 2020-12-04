package com.controller;

import com.alibaba.fastjson.JSON;
import com.po.Dto;
import com.po.ItripUser;
import com.serivce.IUserSerivce;
import com.util.*;
import com.util.vo.ItripTokenVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

@RestController
@RequestMapping(value = "/api")
public class ItirpUserLoginController {
    Jedis jedis = new Jedis("127.0.0.1", 6379);
    @Autowired
    private IUserSerivce iUserSerivce;

    public IUserSerivce getiUserSerivce() {
        return iUserSerivce;
    }

    public void setiUserSerivce(IUserSerivce iUserSerivce) {
        this.iUserSerivce = iUserSerivce;
    }

    //手机登陆
    @RequestMapping(value = "/dologin")
    public Dto dologin(HttpServletRequest request, HttpServletResponse response, String name, String password) {
        System.out.println("登陆方法进入。。。");
        //前台传值判空
        if (EmptyUtils.isNotEmpty(name) && EmptyUtils.isNotEmpty(password)) {
            ItripUser user = iUserSerivce.dologin(new ItripUser(name.trim(), MD5Util.getMd5(password.trim(), 32)));
            if (EmptyUtils.isNotEmpty(user)) {
                //不为空，数据库里有值登陆成功
                //将name作为key,存入redis
                String redisrtoken = jedis.get(name);
                //判断用户redis有效期中有没有登陆但未注销
                if (EmptyUtils.isEmpty(redisrtoken)) {
                    //redis的tocken为空，未登录状态
                    System.out.println("该用户在短时间内处于未登录状态");
                    //生成tocken
                    String token = TokenUtil.getTokenGenerator(request.getHeader("user-agent"), user);
                    //token存入redis
                    //判断前缀字符串
                    if (token.startsWith("token:PC-")) {
                        String userjson = JSON.toJSONString(user);
                        System.out.println("userjson"+userjson);
                        jedis.setex(token, 3600, userjson);
                        jedis.setex(name, 3600, token);
                    }
                    //获得当前系统时间
                    ItripTokenVO tokenVO = new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis() + 60 * 60 * 1000, Calendar.getInstance().getTimeInMillis());
                    return DtoUtil.returnDataSuccess(tokenVO);
                } else {
                    //有效时间内处于登陆状态
                    try {
                        String newtocken = TokenUtil.replaceToken(request.getHeader("user-agent"), redisrtoken, user);
                        //删除redis的tocken,删除以name作为key存放的值
                        jedis.del(redisrtoken);
                        jedis.del(name);
                        //缓存新的tocken在redis
                        if (newtocken.startsWith("token:PC-")) {
                            String redisjson = JSON.toJSONString(user);
                            jedis.setex(newtocken, 3600, redisjson);
                            jedis.setex(name, 3600, newtocken);
                        }
                        ItripTokenVO tokenVO = new ItripTokenVO(newtocken, Calendar.getInstance().getTimeInMillis() + 60 * 60 * 1000, Calendar.getInstance().getTimeInMillis());
                        return DtoUtil.returnDataSuccess(tokenVO);
                    } catch (TokenValidationFailedException e) {
                        return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_TOCKEN_EXCEPTION);
                    }
                }
            } else {
                return DtoUtil.returnFail("这个用户不存在，或请检查密码和账户",ErrorCode.AUTH_AUTHENTICATION_FAILED);
            }
        } else {
            return DtoUtil.returnFail("密码或账号为空，请重新输入",ErrorCode.AUTH_PARAMETER_ERROR);
        }
    }

    @RequestMapping(value = "/logout")
    public Dto logout(HttpServletRequest request,HttpServletResponse response){
        System.out.println("注销方法进入。。。");
        String tocken=request.getHeader("token");
        System.out.println("注销方法的token"+tocken);
        //tocken过期判断
        if(TokenUtil.validate(request.getHeader("user-agent"),tocken)){
            TokenUtil.delete(tocken);
            return DtoUtil.returnSuccess("退出成功");
        }
        return DtoUtil.returnFail("token过期",ErrorCode.AUTH_TOCKEN_OVERDUD);
    }
}
