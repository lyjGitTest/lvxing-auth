package com.serivce;

import com.po.ItripUser;

public interface IUserSerivce {
    //查找重复账户
    public ItripUser findByUserCode(ItripUser itripUser);
    //增加方法
    public  boolean insert(ItripUser itripUser);
    //修改用户状态
    public int updateActivated(ItripUser itripUser);
    //登陆
    public ItripUser dologin(ItripUser itripUser);
}
