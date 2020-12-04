package com.mapper;

import com.po.ItripUser;
import com.util.vo.ItripUserVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItripUserMapper {
    int deleteByPrimaryKey(Long id);
//增加方法
  public  int insert(ItripUser record);

    int insertSelective(ItripUser record);

    ItripUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ItripUser record);

    int updateByPrimaryKey(ItripUser record);
    /**通过手机号或邮箱号确认该用户是否注册过**/
    public ItripUser findByUserCode(ItripUser itripUser);
    /**手机号激活账户*/
     public int updateActivated(ItripUser itripUser);
     /**登陆*/
     public ItripUser dologin(ItripUser itripUser);
}