package com.controller;

import com.po.Dto;
import com.po.ItripUser;
import com.serivce.IUserSerivce;
import com.util.DtoUtil;
import com.util.ErrorCode;
import com.util.MD5Util;
import com.util.SMSUtil;
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
        ItripUser itripUser = null;
        try {
            itripUser = new ItripUser();
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
                if (flag) {
                    //用户添加成功，发送激活码
                   String checkma= SMSUtil.testcheck(itripUser.getUsercode());
                   jedis.setex(itripUser1.getUsercode(),120,checkma);
                    return DtoUtil.returnSuccess("注册成功");
                } else {
                    return DtoUtil.returnFail("注册失败", ErrorCode.AUTH_UNKNOWN);
                }
            } else {
                //数据库有用户。用户状态=0，jedis中没有用户
                if(itripUser1.getActivated()==0 && jedis.get(itripUser1.getUsercode())==null){
                    //注册
                    String checkma= SMSUtil.testcheck(itripUser.getUsercode());
                    jedis.setex(itripUser1.getUsercode(),120,checkma);
                    return DtoUtil.returnFail("用户已存在，但未激活，重新注册", ErrorCode.AUTH_USER_ALREADY_EXISTS);
                }
                return DtoUtil.returnFail("用户已注册", ErrorCode.AUTH_USER_ALREADY_EXISTS);
            }
        } catch (Exception e) {
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }
    }
    //手机激活
    @RequestMapping(value = "/validatephone")
    public Dto validatephone(HttpServletRequest request, HttpServletResponse response, String user,String code){
        System.out.println("激活方法进入。。。");
        if(jedis.get(user)!=null){
            if(jedis.get(user).equals(code)){
      ItripUser itripUser=new ItripUser();
     itripUser.setUsercode(user);
     iUserSerivce.updateActivated(itripUser);
      return DtoUtil.returnSuccess("激活成功");
            }else {
               return DtoUtil.returnSuccess("激活失败验证码错误");
            }
        }else{
            return DtoUtil.returnFail("激活失败",ErrorCode.AUTH_ACTIVATE_FAILED);
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
            String check=SMSUtil.testcheck(user);
            jedis.setex(user,120,check);
            return DtoUtil.returnFail("用户已存在，但未激活，重新激活",ErrorCode.AUTH_USER_ALREADY_EXISTS);
        }
    }
    //手机登陆
    @RequestMapping(value = "/dologin")
    public Dto dologin(HttpServletRequest request,HttpServletResponse response,ItripUser itripUser){
        return null;
    }
}
