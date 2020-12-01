package com.po;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 用户实体类
 * */
public class ItripUser implements Serializable {
    private Long id;
	private int code;//手机或者邮箱注册code操作码
    private String usercode;//手机号或者邮箱号

    private String  activecode;//手机激活码

    private String userpassword;

    private Integer usertype=0;//是否激活

    private Long flatid;

    private String username;

    private String wechat;

    private String qq;

    private String weibo;

    private String baidu;

    private Date creationdate;
    private String screationdate;

    private Long createdby;

    private Date modifydate;

    private Long modifiedby;

    private Integer activated;

    public ItripUser(String usercode, String userpassword) {
        this.usercode = usercode;
        this.userpassword = userpassword;
    }

    public ItripUser(Long id, String usercode, String userpassword, Integer usertype, Long flatid, String username, String wechat, String qq, String weibo, String baidu, Date creationdate, Long createdby, Date modifydate, Long modifiedby, Integer activated) {
        this.id = id;
        this.usercode = usercode;
        this.userpassword = userpassword;
        this.usertype = usertype;
        this.flatid = flatid;
        this.username = username;
        this.wechat = wechat;
        this.qq = qq;
        this.weibo = weibo;
        this.baidu = baidu;
        this.creationdate = creationdate;
        this.createdby = createdby;
        this.modifydate = modifydate;
        this.modifiedby = modifiedby;
        this.activated = activated;
    }

    public ItripUser() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode == null ? null : usercode.trim();
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword == null ? null : userpassword.trim();
    }

    public Integer getUsertype() {
        return usertype;
    }

    public void setUsertype(Integer usertype) {
        this.usertype = usertype;
    }

    public Long getFlatid() {
        return flatid;
    }

    public void setFlatid(Long flatid) {
        this.flatid = flatid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat == null ? null : wechat.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo == null ? null : weibo.trim();
    }

    public String getBaidu() {
        return baidu;
    }

    public void setBaidu(String baidu) {
        this.baidu = baidu == null ? null : baidu.trim();
    }

    public Date getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public Long getCreatedby() {
        return createdby;
    }

    public void setCreatedby(Long createdby) {
        this.createdby = createdby;
    }

    public Date getModifydate() {
        return modifydate;
    }

    public void setModifydate(Date modifydate) {
        this.modifydate = modifydate;
    }

    public Long getModifiedby() {
        return modifiedby;
    }

    public void setModifiedby(Long modifiedby) {
        this.modifiedby = modifiedby;
    }

    public Integer getActivated() {
        return activated;
    }

    public void setActivated(Integer activated) {
        this.activated = activated;
    }

    public String getScreationdate() {
        if(creationdate!=null){
            screationdate=new SimpleDateFormat("yyyy-MM-dd").format(creationdate);
        }
        return screationdate;
    }

    public void setScreationdate(String screationdate) {
        if(screationdate!=null&&!screationdate.trim().equals("")){
            try {
                creationdate=new SimpleDateFormat("yyyy-MM-dd").parse(screationdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        this.screationdate = screationdate;
    }

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getActivecode() {
		return activecode;
	}

	public void setActivecode(String activecode) {
		this.activecode = activecode;
	}

    @Override
    public String toString() {
        return "ItripUser{" +
                "id=" + id +
                ", code=" + code +
                ", usercode='" + usercode + '\'' +
                ", activecode='" + activecode + '\'' +
                ", userpassword='" + userpassword + '\'' +
                ", usertype=" + usertype +
                ", flatid=" + flatid +
                ", username='" + username + '\'' +
                ", wechat='" + wechat + '\'' +
                ", qq='" + qq + '\'' +
                ", weibo='" + weibo + '\'' +
                ", baidu='" + baidu + '\'' +
                ", creationdate=" + creationdate +
                ", screationdate='" + screationdate + '\'' +
                ", createdby=" + createdby +
                ", modifydate=" + modifydate +
                ", modifiedby=" + modifiedby +
                ", activated=" + activated +
                '}';
    }
}