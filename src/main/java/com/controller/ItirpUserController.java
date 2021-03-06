

package com.controller;

import com.po.Dto;
import com.po.ItripUser;
import com.serivce.IUserSerivce;
import com.util.*;
import com.util.vo.ItripUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api")
public class ItirpUserController {
    private Jedis jedis=new Jedis("127.0.0.1",6379);
    @Autowired
    private IUserSerivce iUserSerivce;

    public IUserSerivce getiUserSerivce() {
        return iUserSerivce;
    }

    public void setiUserSerivce(IUserSerivce iUserSerivce) {
        this.iUserSerivce = iUserSerivce;
    }


/*手机注册*/

    @RequestMapping(value = "/registerbyphone")
    public Dto registerbyphone(HttpServletRequest request, HttpServletResponse response, @RequestBody ItripUserVO itripUserVO) {
        System.out.println("进入注册方法");
        try {
          ItripUser  itripUser = new ItripUser();
            itripUser.setUsertype(0);
            itripUser.setUsercode(itripUserVO.getUserCode());
            itripUser.setUsername(itripUserVO.getUserName());
            itripUser.setActivated(0);
            itripUser.setUserpassword(itripUserVO.getUserPassword());
           ItripUser itripUser1= iUserSerivce.findByUserCode(itripUser);
            if (itripUser1==null) {
                //没有注册
                itripUser.setUserpassword(MD5Util.getMd5(itripUser.getUserpassword(), 32));
               boolean flag = iUserSerivce.insert(itripUser);
                if(flag){
                    //用户添加成功，发送激活码
                   String checkma= SMSUtil.testcheck(itripUser.getUsercode());
                   jedis.setex(itripUser.getUsercode(),120,checkma);
                    return DtoUtil.returnSuccess();
                } else {
                    return DtoUtil.returnFail("注册失败", ErrorCode.AUTH_UNKNOWN);
                }
            } else {
                //数据库有用户。判断 用户状态未激活状态，jedis中没有存放用户
                if(itripUser1.getActivated()==0 && jedis.get(itripUser1.getUsercode())==null){
                    //注册用户
                    String checkma= SMSUtil.testcheck(itripUser.getUsercode());
                    jedis.setex(itripUser1.getUsercode(),120,checkma);
                    return DtoUtil.returnFail("用户已存在，但未激活，重新激活", ErrorCode.AUTH_USER_ALREADY_EXISTS);
                }
                    return DtoUtil.returnFail("用户已注册", ErrorCode.AUTH_USER_ALREADY_EXISTS);

            }
        } catch (Exception e) {
            e.getMessage();
            return DtoUtil.returnFail("系统异常", ErrorCode.AUTH_UNKNOWN);
        }
    }
    //手机激活
    @RequestMapping(value = "/validatephone")
    public Dto validatephone(HttpServletRequest request, HttpServletResponse response, String user,String code){
        System.out.println("激活方法进入。。。");

        System.out.println("user"+user);
        System.out.println("code"+code);
        System.out.println("jedis.get(user)"+jedis.get(user));//null
        System.out.println("jedis.get(code)"+jedis.get(code));//null*//*

        if(jedis.get(user)!=null){
            //判断redis中有无，通过key找到相应的value和code短信验证码做对比
            if(jedis.get(user).equals(code)){
      ItripUser itripUser=new ItripUser();
      //将手机号存入
     itripUser.setUsercode(user);
     //更新状态对象，激活
     iUserSerivce.updateActivated(itripUser);
      return DtoUtil.returnSuccess("激活成功");
            }else {
               return DtoUtil.returnSuccess("激活失败验证码错误");
            }
        }else{
            return DtoUtil.returnFail("激活失败，查看手机号是否注册",ErrorCode.AUTH_AUTHENTICATION_FAILED);
        }
    }
    //手机单点激活
    @RequestMapping(value = "/activate")
    public Dto activate(HttpServletRequest request,HttpServletResponse response,String user,String code) {
        System.out.println("单点激活进入。。。");
        if (jedis.get(user) != null) {
            if (jedis.get(user).equals(code)) {
                ItripUser itripUser = new ItripUser();
                itripUser.setUsercode(user);
                iUserSerivce.updateActivated(itripUser);
                return DtoUtil.returnSuccess("激活成功");
            }else{
                return DtoUtil.returnSuccess("激活失败，验证码错误");
            }
        }else{
            String check=null;
            //判断为邮箱还是手机号
            if(user.lastIndexOf(".")==-1) {
                 check = SMSUtil.testcheck(user);
            }else{
                ItripUser user1=new ItripUser();
                user1.setUsercode(user);
                 check= EmailUtil.emailregister(iUserSerivce.findByUserCode(user1));
            }
            jedis.setex(user,120,check);
            return DtoUtil.returnFail("用户已存在，但未激活，重新激活",ErrorCode.AUTH_USER_ALREADY_EXISTS);
        }
    }

    //邮箱注册
    @RequestMapping(value = "/doregister")
    public  Dto doregister(HttpServletRequest request,HttpServletResponse response,@RequestBody ItripUserVO itripUserVO){
        System.out.println("邮箱注册进入。。。。");
        System.out.println("itrip"+itripUserVO.toString());
        ItripUser  itripUser = new ItripUser();
        itripUser.setUsercode(itripUserVO.getUserCode());
        itripUser.setUserpassword(itripUserVO.getUserPassword());
        itripUser.setUsertype(0);
        itripUser.setUsername(itripUserVO.getUserName());
        itripUser.setActivated(0);
        ItripUser itripUser1= iUserSerivce.findByUserCode(itripUser);
        if (itripUser1==null) {
            itripUser.setUserpassword(MD5Util.getMd5(itripUser.getUserpassword(), 32));
            boolean flag = iUserSerivce.insert(itripUser);
            if(flag){
                //用户添加成功，发送激活码
             String checkma= EmailUtil.emailregister(itripUser);
                jedis.setex(itripUser.getUsercode(),120,checkma);
                System.out.println("jedis"+jedis.get(itripUser.getUsercode()));
                return DtoUtil.returnSuccess();
            } else {
                return DtoUtil.returnFail("邮箱注册失败", ErrorCode.AUTH_UNKNOWN);
            }

             }else {
            //数据库有用户。判断 用户状态未激活状态，jedis中没有存放用户
            if(itripUser1.getActivated()==0 && jedis.get(itripUser1.getUsercode())==null){
                //注册用户
                String checkma= EmailUtil.emailregister(itripUser);
                jedis.setex(itripUser1.getUsercode(),120,checkma);
                return DtoUtil.returnFail("用户已存在，但未激活，重新激活", ErrorCode.AUTH_USER_ALREADY_EXISTS);
            }else {
                return DtoUtil.returnFail("用户已注册", ErrorCode.AUTH_USER_ALREADY_EXISTS);
            }
        }

    }
@RequestMapping(value = "/ckusr")
public Dto ckusr(HttpServletRequest request,HttpServletResponse response,String name) {
    ItripUser itripUser = new ItripUser();
    itripUser.setUsercode(name);
    if ( null==iUserSerivce.findByUserCode(itripUser) ) {
        return DtoUtil.returnSuccess("邮箱可用");
    } else {
        return DtoUtil.returnFail("用户已存在", "111111111");
    }

}

}

