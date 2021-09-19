package plugins.wnplugin.runnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import plugins.wnplugin.WnPlugin;
import plugins.wnplugin.config.PlayerConfig;

import java.util.Objects;
import java.util.concurrent.Future;

public class LoginHint extends BukkitRunnable {
    private int count;
    private final Player player;
    private PlayerConfig pc;
    public LoginHint(int count, Player player){
        this.count = count;
        this.player = player;
        pc = new PlayerConfig(player);
    }

    @Override
    public void run(){
        pc.reload();
        if(pc.getIs_login())this.cancel();
        if(count>0){
            if(pc.getIs_register())player.sendMessage(Objects.requireNonNull(WnPlugin.instance.getConfig().getString("message.need-login")));
            else player.sendMessage(ChatColor.translateAlternateColorCodes('&',Objects.requireNonNull(WnPlugin.instance.getConfig().getString("message.need-register"))));
            count--;
        }
        else{
            Future<Object> future = Bukkit.getScheduler().callSyncMethod(WnPlugin.instance, () -> {
                player.kickPlayer(player.getName());
                return null;
            });
            this.cancel();
        }
    }
}
