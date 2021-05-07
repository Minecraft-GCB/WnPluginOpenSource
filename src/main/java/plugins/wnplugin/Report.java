package plugins.wnplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Report implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if("report".equals(command.getName())){
            if(args.length == 2){

            }
        }
        return false;
    }
}
