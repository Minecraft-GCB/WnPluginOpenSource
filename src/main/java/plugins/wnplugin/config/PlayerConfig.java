package plugins.wnplugin.config;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import plugins.wnplugin.WnPlugin;

public class PlayerConfig {
    private final Player player;
    private final String player_name;
    JavaPlugin plugin = WnPlugin.instance;
    private String password_hash;
    private boolean is_register;
    private boolean is_login;

    public PlayerConfig(Player player){
        this.player = player;
        player_name = player.getName();
        setPassword_hash(plugin.getConfig().getString("player." + getPlayer_name() + ".password"));
        setIs_register(getPassword_hash() != null);
        setIs_login(WnPlugin.Login.contains(getPlayer_name()));
    }

    public void reload(){
        setPassword_hash(plugin.getConfig().getString("player." + getPlayer_name() + ".password"));
        setIs_register(getPassword_hash() != null);
        setIs_login(WnPlugin.Login.contains(getPlayer_name()));
    }

    public String getPlayer_name() {
        return player_name;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    private void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public boolean getIs_register() {
        return is_register;
    }

    private void setIs_register(boolean is_register) {
        this.is_register = is_register;
    }

    public boolean getIs_login() {
        return is_login;
    }

    private void setIs_login(boolean is_login) {
        this.is_login = is_login;
    }
}