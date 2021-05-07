package plugins.wnplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.HashMap;

public final class WnPlugin extends JavaPlugin implements Listener{
    HashMap<Player,Player> sendTrade = new HashMap<>();

    String Prefix = "[WnPlugin]";
    //Inventory anvil = Bukkit.createInventory(null,InventoryType.ANVIL,"§2Anvil");
    @Override
    public final void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new Login(),this);
        RecipeCraft();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public final void onDisable() {
        // Plugin shutdown logic
        Bukkit.broadcastMessage(Prefix + ChatColor.RED + " 插件关闭，丢失了" + sendTrade.size() + "个交易");
        saveConfig();
    }

    private void RecipeCraft(){
        ItemStack rec = new ItemStack(Material.DIAMOND_ORE);
        ShapedRecipe recipe = new ShapedRecipe(Material.DIAMOND_ORE.getKey(),rec);

        recipe.shape("111","121","111");
        recipe.setIngredient('1', Material.STONE);
        recipe.setIngredient('2', Material.DIAMOND);

        getServer().addRecipe(recipe);

        rec = new ItemStack(Material.GOLD_ORE);
        recipe = new ShapedRecipe(Material.GOLD_ORE.getKey(),rec);
        recipe.shape("111","121","111");
        recipe.setIngredient('1', Material.STONE);
        recipe.setIngredient('2', Material.GOLD_INGOT);

        getServer().addRecipe(recipe);

        rec = new ItemStack(Material.COAL_ORE);
        recipe = new ShapedRecipe(Material.COAL_ORE.getKey(),rec);

        recipe.shape("111","121","111");
        recipe.setIngredient('1', Material.STONE);
        recipe.setIngredient('2', Material.COAL);

        getServer().addRecipe(recipe);
    }

    @EventHandler
    public final void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(p.hasPlayedBefore()){
            e.setJoinMessage(ChatColor.YELLOW + p.getName() + "进入了服务器！");
        }
        else {
            e.setJoinMessage(ChatColor.YELLOW + "欢迎萌新" + p.getName() + "进入服务器！");
            //Starter Kit
            ItemStack food = new ItemStack(Material.COOKED_PORKCHOP,16);
            ItemStack sword = new ItemStack(Material.WOODEN_SWORD,1);
            p.getInventory().addItem(food,sword);
        }
        String name = e.getPlayer().getName();
        if(getConfig().getInt("player." + name + ".try") > 5){
            BukkitTask kick = new KickPlayer(e.getPlayer(),1).runTaskLater(this, 10);
            return;
        }
        if(getConfig().get("player." + p.getName() + ".allow") == null){
            getConfig().set("player." + p.getName() + ".allow",false);
        }
        if(getConfig().get("player." + p.getName() + ".allow") != null && getConfig().getBoolean("fix." + "enable") && !getConfig().getBoolean("player." + p.getName() + ".allow")){
            BukkitTask kick = new KickPlayer(e.getPlayer(),2).runTaskLater(this, 10);
        }
    }

    @EventHandler
    public final void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        String name = e.getPlayer().getName();
        if(getConfig().getBoolean("fix." + "enable") && !name.equals(getConfig().get("fix." + "allow"))){
            e.setQuitMessage(ChatColor.YELLOW + "不在修复人员名单中的玩家"  + p.getName() + "在维修模式中尝试进入服务器！已自动踢出");
        }
        else if(getConfig().get("player." + name + ".try") != null && getConfig().getInt("player." + name + ".try") > 5){
            e.setQuitMessage(ChatColor.YELLOW + p.getName() + "因尝试放置方块次数过多，被自动踢出了服务器！");
        }
        else{
            e.setQuitMessage(ChatColor.YELLOW + p.getName() + "退出了服务器！");
        }
    }

    @EventHandler
    public void onPlayerRenameItem(PrepareAnvilEvent e){
        if(e.getResult() != null && e.getResult().hasItemMeta() && e.getInventory().getRenameText() != "" && e.getInventory().getRenameText() != null){
            ItemStack result = e.getResult();
            ItemMeta im = result.getItemMeta();
            String colored = ChatColor.translateAlternateColorCodes('&', e.getInventory().getRenameText());
            if (im != null) {
                im.setDisplayName(colored);
            }
            else{
                getLogger().info(ChatColor.RED + "im is Null！");
            }
            result.setItemMeta(im);
        }
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent e){
        Player p = e.getPlayer();
        if(e.getNewGameMode()!=GameMode.SURVIVAL) {
            if (!p.hasPermission("wnplugin.changegamemode")) {
                e.setCancelled(true);
                p.setGameMode(GameMode.SURVIVAL);
                getLogger().info(ChatColor.RED + p.getName() + "被切换游戏模式，已经阻止");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        String name = e.getPlayer().getName();
        if(getConfig().get("player." + name + ".nobreak") == null){
            getConfig().set("player." + name + ".nobreak",false);
        }
        if(getConfig().getBoolean("player." + name + ".nobreak")){
            e.setCancelled(true);
            int x = e.getBlock().getX();
            int y = e.getBlock().getY();
            int z = e.getBlock().getZ();
            e.getPlayer().sendMessage(ChatColor.RED + "你无权破坏位于(" + x + "," + y + "," + z +")" + "的方块！");
        }
    }

    @EventHandler
    public void onBuildBlock(BlockCanBuildEvent e){
        String name = e.getPlayer().getName();
        if(getConfig().get("player." + name + ".build") == null){
            getConfig().set("player." + name + ".build",true);
        }
        if(!getConfig().getBoolean("player." + name + ".build")){
            e.setBuildable(false);
            e.getPlayer().sendMessage(ChatColor.RED + "你无权放置方块！");
            if(getConfig().get("player." + name + ".try") == null){
                getConfig().set("player." + name + ".try",1);
                saveConfig();
            }
            else{
                getConfig().set("player." + name + ".try",getConfig().getInt("player." + name + ".try") + 1);
                saveConfig();
                if(getConfig().getInt("player." + name + ".try") > 5){
                    e.getPlayer().kickPlayer(ChatColor.RED + "尝试放置方块次数过多，自动踢出！");
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
        if("fixlist".equals(command.getName())){
            if(args.length != 2){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            else{
                if("add".equals(args[0])){
                    if(!getConfig().getBoolean("player." + args[1] + ".allow")){
                        getConfig().set("player." + args[1] + ".allow",true);
                        saveConfig();
                        sender.sendMessage(ChatColor.GOLD + "设置成功！");
                        if(Bukkit.getPlayerExact(args[1]) != null){
                            Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.RED + sender.getName() + ChatColor.GOLD + "将您加入了修复人员名单！");
                        }
                        return true;
                    }
                    else{
                        sender.sendMessage(ChatColor.RED + "玩家" + ChatColor.GOLD + args[1] + ChatColor.RED + "已经在修复人员名单中！");
                        return true;
                    }
                }
                else if("delete".equals(args[0])){
                    if(getConfig().getBoolean("player." + args[1] + ".allow")){
                        getConfig().set("player." + args[1] + ".allow",false);
                        saveConfig();
                        sender.sendMessage(ChatColor.GOLD + "设置成功！");
                        if(Bukkit.getPlayerExact(args[1]) != null){
                            Bukkit.getPlayerExact(args[1]).sendMessage(ChatColor.RED + sender.getName() + ChatColor.GOLD + "将您移出了修复人员名单！");
                            if(getConfig().getBoolean("fix.enable")){
                                Bukkit.getPlayerExact(args[1]).kickPlayer(ChatColor.RED + "服务器进入维修模式，您不在修复人员名单内！");
                            }
                        }
                        return true;
                    }
                    else{
                        sender.sendMessage(ChatColor.RED + "玩家" + ChatColor.GOLD + args[1] + ChatColor.RED + "本不在修复人员名单中！");
                        return true;
                    }
                }
                else if("inquire".equals(args[0])){
                    if(getConfig().getBoolean("player." + args[1] + ".allow")){
                        sender.sendMessage(ChatColor.GOLD + "玩家" + ChatColor.RED + args[1] + ChatColor.GOLD + "在修复人员名单中！");
                        return true;
                    }
                    else{
                        sender.sendMessage(ChatColor.GOLD + "玩家" + ChatColor.RED + args[1] + ChatColor.GOLD + "不在修复人员名单中！");
                        return true;
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED + "参数错误！");
                    return false;
                }
            }
        }
        else if("resettry".equals(command.getName())){
            if(args.length != 1){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            else{
                if(getConfig().get("player." + args[0] + ".try") == null){
                    sender.sendMessage(ChatColor.RED + "玩家" + args[0] + "没有尝试的记录！");
                    return true;
                }
                else{
                    getConfig().set("player." + args[0] + ".try",0);
                    sender.sendMessage(ChatColor.GOLD + "重置" + args[0] + "的尝试记录成功！");
                    saveConfig();
                    return true;
                }
            }
        }
        else if("fixmode".equals(command.getName())){
            if(args.length < 1 || args.length > 2){
                sender.sendMessage(ChatColor.RED + "参数错误！");
            }
            else{
                if("enable".equals(args[0])){
                    if(args.length != 1){
                        sender.sendMessage(ChatColor.RED + "参数错误！");
                    }
                    else{
                        getConfig().set("fix.enable",true);
                        saveConfig();
                        Player[] playerlist = Bukkit.getOnlinePlayers().toArray(new Player[0]);
                        for(int i=0;i<playerlist.length;i++){
                            if(!getConfig().getBoolean("player." + playerlist[i].getName() + ".allow")){
                                playerlist[i].kickPlayer(ChatColor.RED + "服务器进入维修模式，您不在修复人员名单内！");
                            }
                        }
                        sender.sendMessage(ChatColor.GOLD + "设置成功！");
                        return true;
                    }
                }
                else if("disable".equals(args[0])){
                    if(args.length != 1){
                        sender.sendMessage(ChatColor.RED + "参数错误！");
                    }
                    else{
                        getConfig().set("fix.enable",false);
                        saveConfig();
                        sender.sendMessage(ChatColor.GOLD + "设置成功！");
                        return true;
                    }
                }
            }
        }
        if(sender instanceof Player){
            Player p = (Player) sender;
            if("hello".equals(command.getName())){
                p.sendMessage(ChatColor.LIGHT_PURPLE + "你好呀," + p.getName() + ",欢迎来到我的服务器!qwq!");
                return true;
            }
            if("healself".equals(command.getName())){
                p.setHealth(20);
                p.sendMessage(ChatColor.GOLD + "治疗成功！");
                getLogger().info(ChatColor.BLUE + p.getName()+"治疗了自己！");
                return true;
            }
            if("setlevel".equals(command.getName())){
                if(args.length < 1 || args.length > 2){
                    p.sendMessage(ChatColor.RED + "参数错误！");
                    return false;
                }
                else{
                    int num;
                    try {
                        num = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        p.sendMessage(ChatColor.RED + "参数错误，期望int，却得到String");
                        return false;
                    }
                    if(args.length == 1){
                        p.setLevel(num);
                        p.sendMessage(ChatColor.GOLD + "设置成功！");
                        return true;
                    }
                    else{
                        Player target = Bukkit.getPlayerExact(args[1]);
                        if (target != null) {
                            target.setLevel(num);
                            p.sendMessage(ChatColor.GOLD + "设置成功！");
                            return true;
                        }
                        else{
                            getLogger().info(ChatColor.RED + "Target is Null！");
                        }
                    }
                }
            }
            if("setexp".equals(command.getName())){
                if(args.length < 1 || args.length > 2){
                    p.sendMessage(ChatColor.RED + "参数错误！");
                    return false;
                }
                else{
                    float num;
                    try{
                        num = Float.parseFloat(args[0]);
                    }catch(NumberFormatException e){
                        p.sendMessage(ChatColor.RED + "参数错误，期望float，却得到String");
                        return false;
                    }
                    if (args.length == 1){
                        if(num < 0 || num > 100){
                            p.sendMessage(ChatColor.RED + "参数错误，期望一个介于0.0F和100.0F间的float");
                            return true;
                        }
                        else{
                            num = num / 100.0F;
                            p.setExp(num);
                            p.sendMessage(ChatColor.GOLD + "设置成功！");
                            return true;
                        }
                    }
                    else{
                        if(num < 0 || num > 100){
                            p.sendMessage(ChatColor.RED + "参数错误，期望一个介于0.0F和100.0F间的float");
                            return true;
                        }
                        else{
                            num = num / 100.0F;
                            Player target = Bukkit.getPlayerExact(args[1]);
                            if (target != null) {
                                target.setExp(num);
                                p.sendMessage(ChatColor.GOLD + "设置成功！");
                                return true;
                            }
                            else{
                                getLogger().info(ChatColor.RED + "Target is Null！");
                                p.sendMessage(ChatColor.RED + "设置失败！可能是因为玩家不在线，导致target为Null！");
                                return true;
                            }
                        }
                    }
                }
            }
            if("cleanplayer".equals(command.getName())){
                if(args.length > 1){
                    p.sendMessage(ChatColor.RED + "参数错误！");
                    return false;
                }
                else{
                    if(args.length == 0 || p.getName().equals(args[0])){
                        p.getInventory().clear();
                        p.sendMessage(ChatColor.GOLD + "已经清除您的背包！");
                        return true;
                    }
                    else{
                        Player target = Bukkit.getPlayerExact(args[0]);
                        if(target != null){
                            target.getInventory().clear();
                            p.sendMessage(ChatColor.GOLD + "已经清除" + ChatColor.RED + target.getName() + ChatColor.GOLD + "的背包！");
                            target.sendMessage(ChatColor.GOLD + "您被" + ChatColor.RED + p.getName() + ChatColor.GOLD + "清除了背包！");
                            return true;
                        }
                        else{
                            getLogger().info(ChatColor.RED + "Target is Null！");
                            p.sendMessage(ChatColor.RED + "清除失败！可能是因为玩家不在线，导致target为Null！");
                            return true;
                        }
                    }
                }
            }
            if("trade".equals(command.getName())){
                if(args.length == 1){
                    if("accept".equals(args[0])){
                        if(sendTrade.containsKey(p)){
                            Player target = sendTrade.get(p);
                            if(target != null){
                                if(Bukkit.getOnlinePlayers().contains(target)){
                                    Inventory tradeinv = Bukkit.createInventory(null,54,"§2交易面板");
                                    target.sendMessage(ChatColor.RED + p.getName() + ChatColor.GOLD + "同意了您的交易请求！");
                                    p.sendMessage(ChatColor.GOLD + "您同意了" + ChatColor.RED + p.getName() + ChatColor.GOLD + "的交易请求！");
                                    p.closeInventory();
                                    p.openInventory(tradeinv);
                                    target.closeInventory();
                                    target.openInventory(tradeinv);
                                    sendTrade.remove(p);
                                    return true;
                                }
                                else{
                                    p.sendMessage(ChatColor.RED + "目标(target)不在线！已经移除此请求！");
                                    sendTrade.remove(p);
                                    return true;
                                }
                            }
                            else{
                                p.sendMessage(ChatColor.RED + "目标(target)不是玩家或不在线！");
                                return true;
                            }
                        }
                        else{
                            p.sendMessage(ChatColor.RED + "没有找到符合条件的请求！");
                            return true;
                        }
                    }
                    else{
                        if("deny".equals(args[0])){
                            if(sendTrade.containsKey(p)){
                                Player target = sendTrade.get(p);
                                if(target != null){
                                    if(Bukkit.getOnlinePlayers().contains(target)){
                                        sendTrade.remove(p);
                                        p.sendMessage(ChatColor.GOLD + "成功拒绝了" + ChatColor.RED + target.getName() + ChatColor.GOLD + "的交易请求！");
                                        target.sendMessage(ChatColor.RED + "您的交易请求被拒绝！");
                                        return true;
                                    }
                                    else{
                                        sendTrade.remove(p);
                                        p.sendMessage(ChatColor.RED + "目标(target)不在线！已经移除此请求！");
                                        return true;
                                    }
                                }
                                else{
                                    p.sendMessage(ChatColor.RED + "目标(target)不是玩家或不在线！");
                                    return true;
                                }
                            }
                            else{
                                p.sendMessage(ChatColor.RED + "没有找到符合条件的请求！");
                                return true;
                            }
                        }
                        else{
                            p.sendMessage(ChatColor.RED + "参数错误！");
                            return false;
                        }
                    }
                }
                if(args.length == 2){
                    if("send".equals(args[0])){
                        Player target = Bukkit.getPlayerExact(args[1]);
                        if (target != null) {
                            if(Bukkit.getOnlinePlayers().contains(target)){
                                if(sendTrade.containsKey(target)){
                                    p.sendMessage(ChatColor.RED + "对方存在来自" + ChatColor.GOLD + sendTrade.get(target).getName() + ChatColor.RED + "的交易请求，无法发送！");
                                    return true;
                                }
                                else{
                                    sendTrade.put(target,p);
                                    p.sendMessage(ChatColor.GOLD + "您已发送交易请求至" + ChatColor.RED + target.getName());
                                    target.sendMessage(ChatColor.RED + p.getName() + ChatColor.GOLD + "向您发送了交易请求，发送/trade accept以同意，发送/trade deny以拒绝");
                                    return true;
                                }
                            }
                            else{
                                p.sendMessage(ChatColor.RED + "目标(target)不在线！");
                            }
                            return true;
                        }
                        else{
                            p.sendMessage(ChatColor.RED + "发送失败！可能是玩家不在线，导致target为Null！");
                            return true;
                        }
                    }
                    else{
                        p.sendMessage(ChatColor.RED + "参数错误！");
                        return false;
                    }
                }
            }
            if("nobreak".equals(command.getName())){
                if(args.length == 1){
                    if("enable".equals(args[0])){
                        getConfig().set("player." + p.getName() + "." + "nobreak",true);
                        saveConfig();
                        p.sendMessage(ChatColor.GOLD + "已禁用您的挖掘能力！");
                    }
                    else if("disable".equals(args[0])){
                        getConfig().set("player." + p.getName() + "." + "nobreak",false);
                        saveConfig();
                        p.sendMessage(ChatColor.GOLD + "已启用您的挖掘能力！");
                    }
                    else{
                        p.sendMessage(ChatColor.RED + "参数错误！");
                        return false;
                    }
                    return true;
                }
                else if(args.length == 2){
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if("enable".equals(args[0])){
                        if(target != null){
                            getConfig().set("player." + target.getName() + "." + "nobreak",true);
                            saveConfig();
                            target.sendMessage(ChatColor.RED + "您的挖掘能力被" + ChatColor.GOLD + p.getName() + ChatColor.RED + "禁用！");
                            return true;
                        }
                        else{
                            p.sendMessage(ChatColor.RED + "设置失败！可能是因为玩家不在线，导致target为Null！");
                            getLogger().info(ChatColor.RED + "target is Null！");
                            return true;
                        }
                    }
                    if("disable".equals(args[0])){
                        if(target != null){
                            getConfig().set("player." + target.getName() + "." + "nobreak",false);
                            saveConfig();
                            target.sendMessage(ChatColor.GOLD + "您的挖掘能力被" + ChatColor.RED + p.getName() + ChatColor.GOLD + "启用！");
                            return true;
                        }
                        else{
                            p.sendMessage(ChatColor.RED + "设置失败！可能是因为玩家不在线，导致target为Null！");
                            getLogger().info(ChatColor.RED + "target is Null！");
                            return true;
                        }
                    }
                }
                else{
                    p.sendMessage(ChatColor.RED + "参数错误！");
                    return false;
                }
            }
            if("setbuild".equals(command.getName())){
                if(args.length == 1){
                    if("disable".equals(args[0])){
                        getConfig().set("player." + p.getName() + "." + "build",false);
                        saveConfig();
                        p.sendMessage(ChatColor.GOLD + "已禁用您的放置能力！");
                    }
                    else if("enable".equals(args[0])){
                        getConfig().set("player." + p.getName() + "." + "build",true);
                        saveConfig();
                        p.sendMessage(ChatColor.GOLD + "已启用您的放置能力！");
                    }
                    else{
                        p.sendMessage(ChatColor.RED + "参数错误！");
                        return false;
                    }
                    return true;
                }
                else if(args.length == 2){
                    Player target = Bukkit.getPlayerExact(args[1]);
                    if("disable".equals(args[0])){
                        if(target != null){
                            getConfig().set("player." + target.getName() + "." + "build",false);
                            saveConfig();
                            p.sendMessage(ChatColor.GOLD + "已禁用" + ChatColor.RED + target.getName() + ChatColor.GOLD + "的放置能力！");
                            target.sendMessage(ChatColor.RED + "您的放置能力被" + ChatColor.GOLD + p.getName() + ChatColor.RED + "禁用！");
                            return true;
                        }
                        else{
                            p.sendMessage(ChatColor.RED + "设置失败！可能是因为玩家不在线，导致target为Null！");
                            getLogger().info(ChatColor.RED + "target is Null！");
                            return true;
                        }
                    }
                    else if("enable".equals(args[0])){
                        if(target != null){
                            getConfig().set("player." + target.getName() + "." + "build",true);
                            saveConfig();
                            p.sendMessage(ChatColor.GOLD + "已启用" + ChatColor.RED + target.getName() + ChatColor.GOLD + "的放置能力！");
                            target.sendMessage(ChatColor.GOLD + "您的放置能力被" + ChatColor.RED + p.getName() + ChatColor.GOLD + "启用！");
                            return true;
                        }
                        else{
                            p.sendMessage(ChatColor.RED + "设置失败！可能是因为玩家不在线，导致target为Null！");
                            getLogger().info(ChatColor.RED + "target is Null！");
                            return true;
                        }
                    }
                    else{
                        p.sendMessage(ChatColor.RED + "参数错误！");
                        return false;
                    }
                }
                else{
                    p.sendMessage(ChatColor.RED + "参数错误！");
                    return false;
                }
            }
            if("openinv".equals(command.getName())){
                Inventory playerinv = Bukkit.createInventory(null,9,"§4随身背包");
                playerinv.setItem(0,getConfig().getItemStack("inventory." + p.getName() + ".item1"));
                playerinv.setItem(1,getConfig().getItemStack("inventory." + p.getName() + ".item2"));
                playerinv.setItem(2,getConfig().getItemStack("inventory." + p.getName() + ".item3"));
                playerinv.setItem(3,getConfig().getItemStack("inventory." + p.getName() + ".item4"));
                playerinv.setItem(4,getConfig().getItemStack("inventory." + p.getName() + ".item5"));
                playerinv.setItem(5,getConfig().getItemStack("inventory." + p.getName() + ".item6"));
                playerinv.setItem(6,getConfig().getItemStack("inventory." + p.getName() + ".item7"));
                playerinv.setItem(7,getConfig().getItemStack("inventory." + p.getName() + ".item8"));
                playerinv.setItem(8,getConfig().getItemStack("inventory." + p.getName() + ".item9"));
                p.openInventory(playerinv);
                return true;
            }
        }
        else{
            sender.sendMessage(ChatColor.RED + "这个指令只能由玩家执行!");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent e){
        String invtitle = e.getView().getTitle();
        if("§4随身背包".equals(invtitle)){
            getConfig().set("inventory." + e.getPlayer().getName() + ".item1",e.getInventory().getItem(0));
            getConfig().set("inventory." + e.getPlayer().getName() + ".item2",e.getInventory().getItem(1));
            getConfig().set("inventory." + e.getPlayer().getName() + ".item3",e.getInventory().getItem(2));
            getConfig().set("inventory." + e.getPlayer().getName() + ".item4",e.getInventory().getItem(3));
            getConfig().set("inventory." + e.getPlayer().getName() + ".item5",e.getInventory().getItem(4));
            getConfig().set("inventory." + e.getPlayer().getName() + ".item6",e.getInventory().getItem(5));
            getConfig().set("inventory." + e.getPlayer().getName() + ".item7",e.getInventory().getItem(6));
            getConfig().set("inventory." + e.getPlayer().getName() + ".item8",e.getInventory().getItem(7));
            getConfig().set("inventory." + e.getPlayer().getName() + ".item9",e.getInventory().getItem(8));
            saveConfig();
            e.getPlayer().sendMessage(ChatColor.GOLD + "随身背包已经保存！");
        }
    }
}