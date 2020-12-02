package com.serivce.Impl;

import com.mapper.ItripUserMapper;
import com.po.ItripUser;
import com.serivce.IUserSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserSerivceImpl implements IUserSerivce {
@Autowired
private ItripUserMapper itripUserMapper;

    public ItripUserMapper getItripUserMapper() {
        return itripUserMapper;
    }

    public void setItripUserMapper(ItripUserMapper itripUserMapper) {
        this.itripUserMapper = itripUserMapper;
    }

    @Override
    public ItripUser findByUserCode(ItripUser itripUser) {
       return itripUserMapper.findByUserCode(itripUser);
    }

    @Override
    public boolean insert(ItripUser itripUser) {
        int flag=itripUserMapper.insert(itripUser);
        if (flag>0){
            return true;
        }
        return false;
    }

    @Override
    public int updateActivated(ItripUser itripUser) {
        return itripUserMapper.updateActivated(itripUser);
    }

    @Override
    public boolean dologin(ItripUser itripUser) {
        return itripUserMapper.dologin(itripUser);
    }
}
