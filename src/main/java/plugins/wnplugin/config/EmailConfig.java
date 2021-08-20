package plugins.wnplugin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import plugins.wnplugin.WnPlugin;

import java.io.File;

public class EmailConfig {
    private static Plugin plugin = WnPlugin.getPlugin(WnPlugin.class);
    private static FileConfiguration config;
    private static Boolean enable;
    private static String emailAccount;
    private static String emailPwd;
    private static String mailHost;
    private static String emailSmtpPort;
    private static Boolean sslAuthVerify;
    private static Boolean enableDebug;
    private static String emailTheme;
    private static String emailTitle;
    private static String emailContent;

    static {
        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "mail.yml"));
        enable = config.getBoolean("enable");
        emailAccount = config.getString("emailAccount");
        emailPwd = config.getString("emailPwd");
        mailHost = config.getString("emailSmtpHost");
        emailSmtpPort = config.getString("emailSmtpPort");
        sslAuthVerify = config.getBoolean("sslAuthVerify");
        enableDebug = config.getBoolean("enableDebug");
        emailTheme = config.getString("emailTheme");
        emailTitle = config.getString("emailTitle");
        emailContent = config.getString("emailContent");
    }

    public EmailConfig() {
    }

    public static String getMailHost() {
        return mailHost;
    }

    public static Boolean getEnable() {
        return enable;
    }

    public static String getEmailAccount() {
        return emailAccount;
    }

    public static String getEmailPwd() {
        return emailPwd;
    }

    public static String getEmailSmtpPort() {
        return emailSmtpPort;
    }

    public static Boolean getSslAuthVerify() {
        return sslAuthVerify;
    }

    public static Boolean getEnableDebug() {
        return enableDebug;
    }

    public static String getEmailTheme() {
        return emailTheme;
    }

    public static String getEmailTitle() {
        return emailTitle;
    }

    public static String getEmailContent() {
        return emailContent;
    }

    public static void reload() {
        plugin.reloadConfig();
        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "mail.yml"));
        enable = config.getBoolean("enable");
        emailAccount = config.getString("emailAccount");
        emailPwd = config.getString("emailPwd");
        mailHost = config.getString("emailSmtpHost");
        emailSmtpPort = config.getString("emailSmtpPort");
        sslAuthVerify = config.getBoolean("sslAuthVerify");
        enableDebug = config.getBoolean("enableDebug");
        emailTheme = config.getString("emailTheme");
        emailTitle = config.getString("emailTitle");
        emailContent = config.getString("emailContent");
    }
}
