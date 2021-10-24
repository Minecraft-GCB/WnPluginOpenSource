package plugins.wnplugin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import plugins.wnplugin.WnPlugin;

import java.io.File;

public class ChatConfig {
    private static Plugin plugin = WnPlugin.getPlugin(WnPlugin.class);
    private static FileConfiguration config;
    private static boolean enable_group;
    private static boolean enable_private;
    private static boolean allow_operator;
    private static boolean allow_transfer;
    private static int group_max;
    private static int join_max;
    private static int max_group_pep;

    static {
        File f = new File(plugin.getDataFolder(), "chat.yml");
        if(!f.exists()){
            WnPlugin.instance.saveResource("chat.yml",true);
        }
        config = YamlConfiguration.loadConfiguration(f);
        setEnable_group(config.getBoolean("enable_group_chat"));
        setEnable_private(config.getBoolean("enable_private_chat"));
        setAllow_operator(config.getBoolean("allow_operator"));
        setAllow_transfer(config.getBoolean("allow_transfer"));
        setGroup_max(config.getInt("group_max"));
        setJoin_max(config.getInt("join_max"));
        setMax_group_pep(config.getInt("max_group_pep"));
    }

    public static boolean isEnable_group() {
        return enable_group;
    }

    public static void setEnable_group(boolean enable_group) {
        ChatConfig.enable_group = enable_group;
    }

    public static boolean isEnable_private() {
        return enable_private;
    }

    public static void setEnable_private(boolean enable_private) {
        ChatConfig.enable_private = enable_private;
    }

    public static boolean isAllow_operator() {
        return allow_operator;
    }

    public static void setAllow_operator(boolean allow_operator) {
        ChatConfig.allow_operator = allow_operator;
    }

    public static boolean isAllow_transfer() {
        return allow_transfer;
    }

    public static void setAllow_transfer(boolean allow_transfer) {
        ChatConfig.allow_transfer = allow_transfer;
    }

    public static int getGroup_max() {
        return group_max;
    }

    public static void setGroup_max(int group_max) {
        ChatConfig.group_max = group_max;
    }

    public static int getJoin_max() {
        return join_max;
    }

    public static void setJoin_max(int join_max) {
        ChatConfig.join_max = join_max;
    }

    public static int getMax_group_pep() {
        return max_group_pep;
    }

    public static void setMax_group_pep(int max_group_pep) {
        ChatConfig.max_group_pep = max_group_pep;
    }
}
