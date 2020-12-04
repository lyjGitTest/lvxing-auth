package com.util;

import com.po.ItripUser;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailUtil {
    private JavaMailSender mailSender;
    private static String emailck=null;
    public static String emailregister(ItripUser user) {
        try {
            String account = "lyjtest2020@163.com";            //替换为发件人账号
            String password = "NFPOJZLTRPJPANZE";                    //替换为发件人账号密码
            String receiveMailAccount = user.getUsercode();    //替换为收件人账号
            // 1. 使用Properties对象封装连接所需的信息
            Properties props = new Properties();                    // 参数配置
            props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议
            props.setProperty("mail.smtp.host", "smtp.163.com");    // 发件人的邮箱的 SMTP 服务器地址
            props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
            //获取当前时间毫秒的后四位作为验证码
            Date date=new Date();
            long datetime=date.getTime();
            //保留后四位数字为验证码
            String val=""+datetime;
            emailck=val.substring(val.length()-4,val.length());
            System.out.println("验证码-->"+emailck);

            // 2. 获取Session对象
            Session session = Session.getDefaultInstance(props);
            // 3. 封装Message对象
            MimeMessage message = createMimeMessage(user, session,account, receiveMailAccount);
            // 4. 使用Transport发送邮件
            Transport transport = session.getTransport();
            transport.connect(account, password);
            transport.sendMessage(message, message.getAllRecipients());
            // 5. 关闭连接
            transport.close();
            System.out.println("发送成功！");
            return emailck;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static MimeMessage createMimeMessage(ItripUser user,Session session,String account,String sendMail)
            throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(account, "爱旅行", "UTF-8"));
        // 3. To: 收件人
        message.setRecipient(MimeMessage.RecipientType.TO,
                new InternetAddress(sendMail, user.getUsername()+"用户", "UTF-8"));
        // 4. Subject: 邮件主题
        message.setSubject("验证邮件", "UTF-8");

        // 5. Content: 邮件正文
        message.setContent("尊敬的"+user.getUsername()+"用户你的验证码是："+emailck, "text/html;charset=UTF-8");
        // 6. 设置发件时间
        message.setSentDate(new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;
    }
    public void sendMail(String to,String subject,String content){

        try {
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setFrom("lyjtest2020@163.com");
            helper.setTo("3035195148@qq.com");
            helper.setSubject("主题：XX");
            helper.setText("内容：你好");
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}


