package plugins.wnplugin;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import plugins.wnplugin.runnable.KickPlayer;
import plugins.wnplugin.util.PasswordUtil;

import java.io.File;
import java.util.*;

public final class WnPlugin extends JavaPlugin implements Listener{
    HashMap<Player,Player> sendTrade = new HashMap<>();
    public static List<String> Login = new ArrayList<>();
    public static JavaPlugin instance;
    String Prefix = "[WnPlugin]";
    //Inventory anvil = Bukkit.createInventory(null,InventoryType.ANVIL,"§2Anvil");

    private static String getColoredText(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static boolean judgePlayer(Player p){
        return Login.contains(p.getName());
    }

    @Override
    public final void onEnable() {
        File temp = new File(this.getDataFolder(),"mail.yml");
        if(!temp.exists())saveResource("mail.yml",true);
        instance = this;
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        //RecipeCraft();
        File t = new File(getDataFolder(),"config.yml");
        if(!t.exists()){
            saveDefaultConfig();
            getLogger().info("SaveDefaultConfig!");
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    /*
    @EventHandler
    public void OnWorldInit(WorldInitEvent event){
        if ("world".equals(event.getWorld().getName())) {
            event.getWorld().getPopulators().add(new TNTPopulator());
        }
    }
     */

    @Override
    public final void onDisable() {
        // Plugin shutdown logic
        Bukkit.broadcastMessage(Prefix + ChatColor.RED + " 插件关闭，丢失了" + sendTrade.size() + "个交易");
        saveConfig();
    }

    /*
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
    */

    /*
    private static class TNTPopulator extends BlockPopulator {

        @Override
        public void populate(World world, Random random, Chunk chunk) {
            int amount = random.nextInt(30);
            for (int i = 0; i < amount; i++) {
                int x = random.nextInt(16);
                int z = random.nextInt(16);
                for (int y = 255; y >= 0; y--) {
                    if (chunk.getBlock(x, y, z).getType() != Material.AIR) {
                        if (chunk.getBlock(x, y, z).getType() == Material.GRASS_BLOCK
                                && chunk.getBlock(x, y + 1, z).getType() == Material.AIR)
                            chunk.getBlock(x, y + 1, z).setType(Material.TNT);
                        break;
                    }
                }
            }
        }
    }
     */

    @EventHandler
    public final void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        String name = e.getPlayer().getName();
        if(p.hasPlayedBefore()){
           if(p.isOp()){
               e.setJoinMessage(ChatColor.YELLOW + "管理员" + ChatColor.RED + p.getName() + ChatColor.YELLOW + "进入了服务器！");
           }
           else{
               e.setJoinMessage(ChatColor.YELLOW + p.getName() + "进入了服务器！");
           }
        }
        else {
            e.setJoinMessage(ChatColor.YELLOW + "欢迎萌新" + p.getName() + "进入服务器！");
            //Starter Kit
            ItemStack food = new ItemStack(Material.COOKED_PORKCHOP,16);
            ItemStack sword = new ItemStack(Material.WOODEN_SWORD,1);
            p.getInventory().addItem(food,sword);
        }
        if(getConfig().getInt("player." + name + ".try") > 5){
            BukkitTask kick = new KickPlayer(e.getPlayer(),1,this).runTaskLater(this, 10);
            return;
        }
        if(getConfig().get("player." + p.getName() + ".allow") == null){
            getConfig().set("player." + p.getName() + ".allow",false);
            saveConfig();
        }
        if(getConfig().get("player." + p.getName() + ".allow") != null && getConfig().getBoolean("fix." + "enable") && !getConfig().getBoolean("player." + p.getName() + ".allow")){
            BukkitTask kick = new KickPlayer(e.getPlayer(),2,this).runTaskLater(this, 10);
        }
        saveConfig();
        if(getConfig().getString("player." + p.getName() + ".password")==null){
            p.sendMessage(getColoredText(getConfig().getString("message.need-register")));
        }
        else{
            p.sendMessage(getColoredText(getConfig().getString("message.need-login")));
        }
    }

    @EventHandler
    public final void onPlayerQuit(PlayerQuitEvent e){
        Login.remove(e.getPlayer().getName());
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
        if(e.getResult() != null && e.getResult().hasItemMeta() && !Objects.requireNonNull(e.getInventory().getRenameText()).equals("") && e.getInventory().getRenameText() != null){
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
        Player p = e.getPlayer();
        if(!judgePlayer(p)){
            e.setCancelled(true);
            p.sendMessage(getColoredText(getConfig().getString("message.hint")));
            return;
        }
        if(getConfig().get("player." + name + ".nobreak") == null){
            getConfig().set("player." + name + ".nobreak",false);
            saveConfig();
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
    @SuppressWarnings("deprecation")
    public void onBuildBlock(BlockCanBuildEvent e){
        Player p = e.getPlayer();
        String name = Objects.requireNonNull(e.getPlayer()).getName();
        if(getConfig().get("player." + name + ".build") == null){
            getConfig().set("player." + name + ".build",true);
            saveConfig();
        }
        if(!judgePlayer(Objects.requireNonNull(p))){
            e.setBuildable(false);
            p.sendMessage(getColoredText(getConfig().getString("message.hint")));
            return;
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

    @EventHandler
    public void onPlayerOpenInventory(InventoryOpenEvent e){
        Player p = (Player) e.getPlayer();

    }

    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
        if(!judgePlayer((Player) sender)){
            if((!("login".equals(command.getName())))&&(!("reg".equals(command.getName())))&&(!("password".equals(command.getName())))){
                sender.sendMessage(getColoredText(getConfig().getString("message.hint")));
                return true;
            }
        }
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "控制台无法执行插件命令！");
            return true;
        }
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
                            Objects.requireNonNull(Bukkit.getPlayerExact(args[1])).sendMessage(ChatColor.RED + sender.getName() + ChatColor.GOLD + "将您加入了修复人员名单！");
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
                            Objects.requireNonNull(Bukkit.getPlayerExact(args[1])).sendMessage(ChatColor.RED + sender.getName() + ChatColor.GOLD + "将您移出了修复人员名单！");
                            if(getConfig().getBoolean("fix.enable")){
                                Objects.requireNonNull(Bukkit.getPlayerExact(args[1])).kickPlayer(ChatColor.RED + "服务器进入维修模式，您不在修复人员名单内！");
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
                        for (Player player : playerlist) {
                            if (!getConfig().getBoolean("player." + player.getName() + ".allow")) {
                                player.kickPlayer(ChatColor.RED + "服务器进入维修模式，您不在修复人员名单内！");
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
        else if("god".equals(command.getName())){
            if(args.length != 2){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            else{
                Player p = Bukkit.getPlayerExact(args[0]);
                if(p != null){
                    if("enable".equals(args[1])){
                        getConfig().set("player." + p.getName() + ".godmode",true);
                        saveConfig();
                        sender.sendMessage(ChatColor.GOLD + "已开启" + ChatColor.RED + args[0] + ChatColor.GOLD + "的无敌模式！");
                        p.sendMessage(ChatColor.RED + p.getName() + ChatColor.GOLD + "开启了您的无敌模式！");
                    }
                    else if("disable".equals(args[1])){
                        getConfig().set("player." + p.getName() + ".godmode",false);
                        saveConfig();
                        sender.sendMessage(ChatColor.GOLD + "已关闭" + ChatColor.RED + args[0] + ChatColor.GOLD + "的无敌模式！");
                        p.sendMessage(ChatColor.RED + p.getName() + ChatColor.GOLD + "关闭了您的无敌模式！");
                    }
                    else{
                        sender.sendMessage(ChatColor.RED + "参数错误！");
                        return false;
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED + "玩家" + args[0] + "不在线！");
                }
                return true;
            }
        }
        else if("eco".equals(command.getName())){
            sender.sendMessage(ChatColor.RED + "经济系统正在开发中，敬请期待！");
            return true;
        }
        Player p = (Player) sender;
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
        if("discard".equals(command.getName())){
            Inventory discardinv = Bukkit.createInventory(null,54,"§b垃圾桶");
            p.closeInventory();
            p.openInventory(discardinv);
            return true;
        }
        if("boom".equals(command.getName())){
            if(args.length != 2){
                p.sendMessage(ChatColor.RED + "参数错误！");
            }
            else{
                Player target = Bukkit.getPlayerExact(args[0]);
                if(target == null){
                    p.sendMessage(ChatColor.RED + "玩家不在线！");
                }
                else{
                    Location temp = target.getLocation();
                    Location loc = new Location(target.getWorld(),temp.getX(),temp.getY(),temp.getZ());
                    try{
                        float strength=Float.parseFloat(args[1]);
                        p.getWorld().createExplosion(loc,strength);
                    }catch (NumberFormatException e){
                        p.sendMessage(ChatColor.RED + "参数错误，期望float，却得到String");
                    }
                }
                return true;
            }
        }
        if("words".equals(command.getName())){
            if(args.length != 2){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            else{
                if("add".equals(args[0])){
                    List<String> words;
                    getConfig().getStringList("words");
                    words = getConfig().getStringList("words");
                    if(!words.contains(args[1])){
                        words.add(args[1]);
                        sender.sendMessage(ChatColor.GOLD + "添加关键词" + args[1] + "成功！");
                        getConfig().set("words",words);
                        saveConfig();
                    }
                    else{
                        sender.sendMessage(ChatColor.RED + "已经存在此关键词！");
                    }
                    saveConfig();
                    return true;
                }
                else if("del".equals(args[0])){
                    List<String> words;
                    getConfig().getStringList("words");
                    words = getConfig().getStringList("words");
                    if(words.contains(args[1])){
                        words.remove(args[1]);
                        sender.sendMessage(ChatColor.GOLD + "删除关键词" + args[1] + "成功！");
                        getConfig().set("words",words);
                        saveConfig();
                    }
                    else{
                        sender.sendMessage(ChatColor.RED + "不存在此关键词！");
                    }
                    saveConfig();
                    return true;
                }
                else{
                    sender.sendMessage(ChatColor.RED + "参数错误！");
                    return false;
                }
            }
        }
        if("mute".equals(command.getName())){
            if(args.length!=1){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            else{
                if(getConfig().get("player." + args[0])==null){
                    p.sendMessage(ChatColor.RED + "此玩家从未进入游戏！");
                    return true;
                }
                else{
                    boolean mute = getConfig().getBoolean("player." + args[0] + ".mute");
                    getConfig().set("player." + args[0] + ".mute",!mute);
                    saveConfig();
                    Player target = Bukkit.getPlayerExact(args[0]);
                    if(target!=null){
                        if(!mute){
                            target.sendMessage(ChatColor.RED + "管理员" + ChatColor.GOLD + p.getName() + ChatColor.RED + "将你禁言");
                            p.sendMessage(ChatColor.GOLD + "成功禁言" + ChatColor.RED + args[0]);
                        }
                        else{
                            target.sendMessage(ChatColor.RED + "管理员" + ChatColor.GOLD + p.getName() + ChatColor.RED + "将你解除禁言");
                            p.sendMessage(ChatColor.GOLD + "成功解除禁言" + ChatColor.RED + args[0]);
                        }
                    }
                    return true;
                }
            }
        }
        if("login".equals(command.getName())){
            if(args.length!=1){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            if(judgePlayer((Player) sender)){
                sender.sendMessage(getColoredText(getConfig().getString("message.is-login")));
                return true;
            }
            String alg = getConfig().getString("security.algorithm");
            if("MD5".equals(alg)){
                String pass = PasswordUtil.StringToMD5(args[0]);
                String t = getConfig().getString("player." + sender.getName() + ".password");
                if(pass.equals(t)){
                    Login.add(sender.getName());
                    sender.sendMessage(getColoredText(getConfig().getString("message.success-login")));
                }
                else{
                    sender.sendMessage(getColoredText(getConfig().getString("message.wrong-password")));
                }
            }
            else if("SHA1".equals(alg)){
                String pass = PasswordUtil.StringToSHA1(args[0]);
                String t = getConfig().getString("player." + sender.getName() + ".password");
                if(pass.equals(t)){
                    Login.add(sender.getName());
                    sender.sendMessage(getColoredText(getConfig().getString("message.success-login")));
                }
                else{
                    sender.sendMessage(getColoredText(getConfig().getString("message.wrong-password")));
                }
            }
            else{
                String pass = PasswordUtil.StringToSHA256(args[0]);
                String t = getConfig().getString("player." + sender.getName() + ".password");
                if(pass.equals(t)){
                    Login.add(sender.getName());
                    sender.sendMessage(getColoredText(getConfig().getString("message.success-login")));
                }
                else{
                    sender.sendMessage(getColoredText(getConfig().getString("message.wrong-password")));
                }
            }
            return true;
        }
        if("reg".equals(command.getName())){
            if(args.length!=2){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            if(!(args[0].equals(args[1]))){
                sender.sendMessage(getColoredText(getConfig().getString("message.not-same")));
                return true;
            }
            if(getConfig().getString("player." + sender.getName() + ".password") != null){
                sender.sendMessage(getColoredText(getConfig().getString("message.is-register")));
                return true;
            }
            if(judgePlayer((Player) sender)){
                sender.sendMessage(getColoredText(getConfig().getString("message.is-login")));
                return true;
            }
            String alg = getConfig().getString("security.algorithm");
            if("MD5".equals(alg)){
                String pass = PasswordUtil.StringToMD5(args[0]);
                getConfig().set("player." + sender.getName() + ".password",pass);
                saveConfig();
                Login.add(sender.getName());
                sender.sendMessage(getColoredText(getConfig().getString("message.success-register")));
            }
            else if("SHA1".equals(alg)){
                String pass = PasswordUtil.StringToSHA1(args[0]);
                getConfig().set("player." + sender.getName() + ".password",pass);
                saveConfig();
                Login.add(sender.getName());
                sender.sendMessage(getColoredText(getConfig().getString("message.success-register")));
            }
            else{
                String pass = PasswordUtil.StringToSHA256(args[0]);
                getConfig().set("player." + sender.getName() + ".password",pass);
                saveConfig();
                Login.add(sender.getName());
                sender.sendMessage(getColoredText(getConfig().getString("message.success-register")));
            }
            saveConfig();
            return true;
        }
        if("password".equals(command.getName())){
            if(args.length!=3){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            else{
                String alg = getConfig().getString("security.algorithm");
                if("MD5".equals(alg)){
                    String pass = PasswordUtil.StringToMD5(args[0]);
                    String t = getConfig().getString("player." + sender.getName() + ".password");
                    if(pass.equals(t)){
                        if(!args[1].equals(args[2])){
                            sender.sendMessage(getColoredText(getConfig().getString("massage.not-same")));
                        }
                        else{
                            getConfig().set("player." + sender.getName() + ".password", PasswordUtil.StringToMD5(args[1]));
                            sender.sendMessage(getColoredText(getConfig().getString("message.change-password")));
                            saveConfig();
                        }
                    }
                    else{
                        sender.sendMessage(getColoredText(getConfig().getString("message.wrong-old-password")));
                    }
                }
                else if("SHA1".equals(alg)){
                    String pass = PasswordUtil.StringToSHA1(args[0]);
                    String t = getConfig().getString("player." + sender.getName() + ".password");
                    if(pass.equals(t)){
                        if(!args[1].equals(args[2])){
                            sender.sendMessage(getColoredText(getConfig().getString("massage.not-same")));
                        }
                        else{
                            getConfig().set("player." + sender.getName() + ".password", PasswordUtil.StringToSHA1(args[1]));
                            sender.sendMessage(getColoredText(getConfig().getString("message.change-password")));
                            saveConfig();
                        }
                    }
                    else{
                        sender.sendMessage(getColoredText(getConfig().getString("message.wrong-old-password")));
                    }
                }
                else{
                    String pass = PasswordUtil.StringToSHA256(args[0]);
                    String t = getConfig().getString("player." + sender.getName() + ".password");
                    if(pass.equals(t)){
                        if(!args[1].equals(args[2])){
                            sender.sendMessage(getColoredText(getConfig().getString("massage.not-same")));
                        }
                        else{
                            getConfig().set("player." + sender.getName() + ".password", PasswordUtil.StringToSHA256(args[1]));
                            sender.sendMessage(getColoredText(getConfig().getString("message.change-password")));
                            saveConfig();
                        }
                    }
                    else{
                        sender.sendMessage(getColoredText(getConfig().getString("message.wrong-old-password")));
                    }
                }
                return true;
            }
        }
        if("report".equals(command.getName())){
            if(args.length!=2){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            else{
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
                if(!target.hasPlayedBefore()){
                    sender.sendMessage(getColoredText(getConfig().getString("message.never-played")));
                    return true;
                }
                else{
                    Random r = new Random();
                    int rand=r.nextInt(2147483647);
                    while(getConfig().get("report." + PasswordUtil.StringToMD5(Integer.toString(rand)))!=null){
                        rand=r.nextInt(2147483647);
                    }
                    getConfig().set("report." + PasswordUtil.StringToMD5(Integer.toString(rand)) + ".sender",sender.getName());
                    getConfig().set("report." + PasswordUtil.StringToMD5(Integer.toString(rand)) + ".target",args[0]);
                    getConfig().set("report." + PasswordUtil.StringToMD5(Integer.toString(rand)) + ".reason",args[1]);
                    sender.sendMessage(ChatColor.GOLD + "举报成功！");
                    sender.sendMessage(ChatColor.GOLD + "你的ReportID为" + ChatColor.RED + PasswordUtil.StringToMD5(Integer.toString(rand)) + ChatColor.GOLD + "，请将此ID提供给OP以处理举报！");
                    PasswordUtil.setClipboardString(PasswordUtil.StringToMD5(Integer.toString(rand)));
                    sender.sendMessage(ChatColor.GOLD + "ReportID已复制");
                    saveConfig();
                    return true;
                }
            }
        }
        if("inquire".equals(command.getName())){
            if(args.length!=1){
                sender.sendMessage(ChatColor.RED + "参数错误！");
                return false;
            }
            if(getConfig().get("report." + args[0])==null){
                sender.sendMessage(ChatColor.RED + "ReportID" + ChatColor.GOLD + args[0] + ChatColor.RED + "不存在！");
                return true;
            }
            else{
                ItemStack ann = new ItemStack(Material.WRITTEN_BOOK);
                BookMeta annBm = (BookMeta) ann.getItemMeta();
                Objects.requireNonNull(annBm).setPages(Arrays.asList("举报者" + getConfig().getString("report." + args[0] + ".sender"),"举报对象" + getConfig().getString("report." + args[0] + ".target"),getConfig().getString("report." + args[0] + ".reason")));
                annBm.setAuthor(getConfig().getString("report." + args[0] + ".sender"));
                annBm.setTitle("举报对象" + getConfig().getString("report." + args[0] + ".target"));
                ann.setItemMeta(annBm);
                getConfig().set("report." + args[0],null);
                ((Player) sender).openBook(ann);
                return true;
            }
        }
        if("mail".equals(command.getName())){
            BukkitTask task = new Mail().runTaskAsynchronously(this);
        }
        saveConfig();
        return false;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (getConfig().getBoolean("player." + e.getPlayer().getName() + ".mute")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "您已被禁言，请联系管理员解禁！");
            return;
        }
        List<String> w;
        if (getConfig().getList("words") == null) {
            List<String> words = new ArrayList<>();
            words.add("傻逼");
            words.add("SB");
            words.add("MMP");
            words.add("妈卖批");
            words.add("智障");
            words.add("ZZ");
            words.add("我日你妈");
            words.add("RNM");
            words.add("WDNMD");
            words.add("WDNMB");
            getConfig().set("words", words);
            w = words;
            saveConfig();
        } else {
            w = getConfig().getStringList("words");
        }
        for (String s : w) {
            if (e.getMessage().toUpperCase().contains(s.toUpperCase())) {
                e.setMessage(ChatColor.RED + "******");
                e.getPlayer().sendMessage(ChatColor.RED + "请勿口吐芬芳！（触发关键词" + "\"" + s + "\"" + "）！");
                return;
            }
        }
        String colored = ChatColor.translateAlternateColorCodes('&', e.getMessage());
        e.setMessage(colored);
    }

    /*
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e){
        Random random = new Random();
        random.setSeed(280092384);
        if(random.nextInt() % 10 == 0){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "欸，我就是不让你丢掉它");
        }
    }
    */

    /*
    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent e){

    }
    */
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
        if("§b垃圾桶".equals(invtitle)){
            e.getPlayer().sendMessage(ChatColor.GOLD + "成功丢弃了垃圾桶中的物品！");
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e){
        if(e.getEntity().getType() == EntityType.PLAYER){
            Player p = (Player)e.getEntity();
            if(getConfig().getBoolean("player." + e.getEntity().getName() + ".godmode")){
                e.setCancelled(true);
                //e.getEntity().sendMessage(ChatColor.GOLD + "已为您抵挡" + ChatColor.RED + e.getDamage() + ChatColor.GOLD + "点伤害！");
            }
        }
    }

    @EventHandler
    public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            if (getConfig().getBoolean("player." + e.getEntity().getName() + ".godmode")) {
                if (p.getFoodLevel() != 20) {
                    p.setFoodLevel(20);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void OnPlayerSendCommand(PlayerCommandPreprocessEvent e){
        if(!judgePlayer(e.getPlayer())){
            String a=e.getMessage();
            if((!a.startsWith("/login"))&&(!a.startsWith("/reg"))&&(!a.startsWith("/password"))){
                e.setCancelled(true);
                e.getPlayer().sendMessage(getColoredText(getConfig().getString("message.hint")));
            }
        }
    }
}