package plugins.wnplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KickPlayer extends BukkitRunnable{
    Player p;
    String name;
    int mode;
    public KickPlayer(Player player,int mode){
        p = player;
        this.mode = mode;
    }

    @Override
    public void run(){
        if(mode == 1){
            p.kickPlayer(ChatColor.RED + "尝试放置方块次数过多，自动踢出！");
        }
        else if(mode == 2){
            p.kickPlayer(ChatColor.RED + "服务器进入维修模式，您不在修复人员名单内！");
        }
        else{
            p.kickPlayer(ChatColor.RED + "您已被踢出服务器！Code:" + mode);
        }
    }
}
