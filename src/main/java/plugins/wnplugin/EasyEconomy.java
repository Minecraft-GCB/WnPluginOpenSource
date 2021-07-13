package plugins.wnplugin;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Objects;

public class EasyEconomy{
    JavaPlugin plugin;
    YamlConfiguration file;
    File f = new File(plugin != null ? plugin.getDataFolder() : null,"setting.yml");
    YamlConfiguration data;
    File f2 = new File(plugin != null ? plugin.getDataFolder() : null,"PlayerData.yml");
    private final int VALUE;
    private final String NAME;
    private final int MAX;
    private final String DESCRIPTION;
    private final boolean ALLOW_OUT;
    private final boolean ALLOW_IN;
    private final boolean ALLOW_TRANSFER;
    private final double EXCHANGE_TAX;
    private final double TRANSFER_TAX;


    public EasyEconomy(String id,JavaPlugin plugin) {
        this.plugin = plugin;
        if(!f.exists()){
            plugin.saveResource("setting.yml",true);
        }
        try {
            file.load(f);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if(!f2.exists()){
            plugin.saveResource("PlayerData.yml",true);
        }
        try {
            data.load(f2);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        String coinPath = "coins." + id;
        if (file.get(coinPath) == null) {
            throw new InvalidParameterException("Coin of this id not found.");
        }
        coinPath += ".";
        String name = file.getString(coinPath + "name");
        String description = file.getString(coinPath + "description");
        if (name == null || description == null) {
            throw new InvalidParameterException("Coin data missing!");
        }
        int value = file.getInt(coinPath + "value");
        int max = file.getInt(coinPath + "max");
        if(max < 0)max=0;
        boolean allowIn = file.getBoolean(coinPath + "exchange.in");
        boolean allowOut = file.getBoolean(coinPath + "exchange.out");
        double exchangeTax = file.getDouble(coinPath + "exchange.tax");
        boolean allowTransfer = file.getBoolean(coinPath + "transfer.allow");
        double transferTax = file.getDouble(coinPath + "transfer.tax");
        NAME = name;
        DESCRIPTION = description;
        VALUE = value;
        MAX = max;
        ALLOW_IN = allowIn;
        ALLOW_OUT = allowOut;
        EXCHANGE_TAX = exchangeTax;
        TRANSFER_TAX = transferTax;
        ALLOW_TRANSFER = allowTransfer;
    }
    public void createBank(String bankName,double money,double max,String coins) throws IllegalArgumentException{
        if(max<money){
            throw new IllegalArgumentException("max小于money！");
        }
        else{
            if(data.get("bank." + bankName) != null)throw new IllegalArgumentException("银行存在！");
            data.set("bank." + bankName + ".max",max);
            data.set("bank." + bankName + ".money",money);
        }
    }
    public int getRank(){

        return 0;
    }
    public void addToPlayer(double money, Player player, String bankName){

    }
    public int getVALUE(){return VALUE;}
    public int getMAX(){
        if(Objects.requireNonNull(file.getString("main-coin")).equals(NAME))return -1;
        return MAX;
    }
    public double getEXCHANGE_TAX(){return EXCHANGE_TAX;}
    public double getTRANSFER_TAX(){return TRANSFER_TAX;}
    public String getNAME(){return NAME;}
    public String getDESCRIPTION(){return DESCRIPTION;}
    public Boolean getALLOW_OUT(){return ALLOW_OUT;}
    public Boolean getALLOW_IN(){return ALLOW_IN;}
    public Boolean getALLOW_TRANSFER(){return ALLOW_TRANSFER;}
}
