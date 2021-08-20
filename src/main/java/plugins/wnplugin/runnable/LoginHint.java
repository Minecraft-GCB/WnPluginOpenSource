package plugins.wnplugin.runnable;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LoginHint extends BukkitRunnable {
    private int count;
    private final String message;
    private final Player player;
    public LoginHint(int count, String message, Player player){
        this.count = count;
        this.message = message;
        this.player = player;
    }

    @Override
    public void run(){
        if(count>0){
            player.sendMessage("");
        }
        else{

        }
    }
}
