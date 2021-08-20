package plugins.wnplugin.runnable;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.Set;

public class KickPlayer extends BukkitRunnable{
    Player p;
    String name;
    int mode;
    JavaPlugin plugin;
    public KickPlayer(Player player, int mode, JavaPlugin plugin){
        p = player;
        this.plugin=plugin;
        this.mode = mode;
    }

    @Override
    public void run(){
        if(mode == 1){
            p.kickPlayer(ChatColor.RED + "尝试放置方块次数过多，自动踢出！");
            plugin.getServer().getBanList(BanList.Type.NAME).addBan(p.getName(),ChatColor.RED + "尝试放置方块次数过多，占用服务器资源",null,null);
        }
        else if(mode == 2){
            p.kickPlayer(ChatColor.RED + "服务器进入维修模式，您不在修复人员名单内！");
        }
        else if(mode == 3){
            p.kickPlayer(ChatColor.RED + "你不应该使用这个用户名登录！因为服务器记录中存在一个相同用户名的玩家，由于两个名称仅大小写不同，所以你无法进入！");
            plugin.getServer().getBanList(BanList.Type.NAME).addBan(p.getName(),ChatColor.RED + "重复的用户名",null,null);
        }
        else{
            p.kickPlayer(ChatColor.RED + "您已被踢出服务器！Code:" + mode);
        }
    }
}
