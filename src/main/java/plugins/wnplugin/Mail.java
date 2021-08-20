package plugins.wnplugin;

import org.bukkit.scheduler.BukkitRunnable;
import plugins.wnplugin.config.EmailConfig;
import plugins.wnplugin.util.MailUtil;

public class Mail extends BukkitRunnable {
    @Override
    public void run(){
        MailUtil.send("2468835317@qq.com", "test",EmailConfig.getEmailTitle(), EmailConfig.getEmailContent());
        //TestUtil.send();
    }
}
