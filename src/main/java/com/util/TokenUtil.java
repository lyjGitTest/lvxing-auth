package com.util;


import com.po.ItripUser;
import cz.mallat.uasparser.UserAgentInfo;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TokenUtil {
    private static String tokenPrefix = "token:";//统一加入 token前缀标识
    private static Jedis jedis = new Jedis("127.0.0.1", 6379);

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    /***
     * @param agent Http头中的user-agent信息
     * @param us 用户信息
     * @return Token格式<br />
     * 	PC：“前缀PC-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     *  <br/>
     *  Android：“前缀ANDROID-USERCODE-USERID-CREATIONDATE-RONDEM[6位]”
     *  生成token
     */
    public static String getTokenGenerator(String agent, ItripUser us) {
        try {
            UserAgentInfo userAgentInfo = UserAgentUtil.getUasParser().parse(agent);
            System.out.println("客户端浏览器类型:" + userAgentInfo.toString());
            StringBuilder sb = new StringBuilder();
            sb.append(tokenPrefix);//统一前缀
            if (userAgentInfo.getDeviceType().equals(UserAgentInfo.UNKNOWN)) {//确定是否是浏览器请求
                System.out.println("浏览器请求。。。");
                if (UserAgentUtil.CheckAgent(agent)) {//移动浏览器
                    sb.append("MOBILE-");
                } else {//pc浏览器
                    sb.append("PC-");
                }
            } else if (userAgentInfo.getDeviceType()
                    .equals("Personal computer")) {//个人电脑

                sb.append("PC-");
            } else {//手机
                sb.append("MOBILE-");
            }
            sb.append(MD5Util.getMd5(us.getUsercode(), 32) + "-");//加密用户名称
            sb.append(us.getId() + "-");
            sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + "-");
            sb.append(MD5Util.getMd5(agent, 6));// 识别客户端的简化实现——6位MD5码
            System.out.println("sb:" + sb);
            return sb.toString();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /*token时间验证*/
    public static boolean validate(String agent, String token) {
        if (jedis.get(token) == null) {// token不存在
            return false;
        }
        try {
            Date TokenGenTime;// token生成时间
            String agentMD5;
            String[] tokenDetails = token.split("-");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            TokenGenTime = formatter.parse(tokenDetails[3]);
            long passed = Calendar.getInstance().getTimeInMillis()
                    - TokenGenTime.getTime();
            if (passed > 60 * 60 * 1000)
                return false;
            agentMD5 = tokenDetails[4];
            if (MD5Util.getMd5(agent, 6).equals(agentMD5))
                return true;
        } catch (ParseException e) {
            return false;
        }
        return false;
    }

    /*删除token*/
    public static void delete(String token) {
        if (jedis.get(token) != null) {
            jedis.del(token);
        }
    }

    /*置换token*/
    public static String replaceToken(String agent, String token,ItripUser user)
            throws TokenValidationFailedException {
        Date TokenGenTime;// token生成时间
        try {
            String[] tokenDetails = token.split("-");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            System.out.println("tokenDetails"+"0"+tokenDetails[0]+"1"+tokenDetails[1]+"2"+tokenDetails[2]+"3"+tokenDetails[3]+"4"+tokenDetails[4]);
            TokenGenTime = formatter.parse(tokenDetails[3]);
        } catch (ParseException e) {
            throw new TokenValidationFailedException("token格式错误:" + token);
        }

        long passed = Calendar.getInstance().getTimeInMillis()
                - TokenGenTime.getTime();// token已产生时间
        if (passed <  60000) { // 置换保护期内
            throw new TokenValidationFailedException("token处于置换保护期内，剩余"
                    + (60000 - passed) / 1000
                    + "(s),禁止置换");
        }
        // 置换token（生成新的token）
       String otoken=getTokenGenerator(agent,user);
        return otoken;
    }

}
