package plugins.wnplugin;

import org.bukkit.scheduler.BukkitRunnable;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
public class Mail extends BukkitRunnable {
    @Override
    public void run(){
        final String SMTP_HOST = "smtp.qq.com"; // SMTP 服务器地址
        final String PORT = "465"; // SMTP 端口
        final String TIMEOUT = "10000"; // 超时设置
        final String USERNAME = "......"; // 使用的邮箱
        final String PASSWORD = "......"; // 授权码或密码
        Properties properties = new Properties(); // Properties 收集信息
        properties.setProperty("mail.transport.protocol", "SMTP"); // 为了使用 SMTP
        properties.setProperty("mail.smtp.host", SMTP_HOST); // 设置服务器
        properties.setProperty("mail.smtp.port", PORT); // 设置端口
        properties.setProperty("mail.smtp.auth", "true"); // 不会现在还有哪个邮件服务商不需要验证的吧？
        properties.setProperty("mail.smtp.timeout", TIMEOUT); // 避免超时
        Authenticator authenticator = new Authenticator() { // 以下代码是进行验证的标准代码，从此开始
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        }; // 到此结束
        Session session = Session.getInstance(properties, authenticator); // 建立连接
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(USERNAME));
            // 发件人，如果和登录用的地址不一样，有些 SMTP 服务器不发（认为是盗用）
            final String PLAYER_MAIL = "2468835317@qq.com";
            // 收件人，一般是玩家邮箱
            final String MAIL_HTML = "<!DOCTYPE html><html>HTML 文本内容</html>";
            // 邮件正文，应当从配置读取
            final String SUBJECT = "主题内容";
            // 主题，建议不要为空，否则有些 IMAP 服务器不收这种邮件（会视为垃圾邮件丢掉）
            msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(PLAYER_MAIL));
            // 设置收件人
            msg.setSubject(SUBJECT);
            msg.setContent(MAIL_HTML, "text/html;charset=utf-8");
            // 你甚至可以把本站通过 HTML 发给客户！但大多数邮件客户端都禁止邮件使用 JavaScript（不会执行）
            msg.saveChanges(); // 保存一下
            Transport.send(msg); // 无连接发送
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
