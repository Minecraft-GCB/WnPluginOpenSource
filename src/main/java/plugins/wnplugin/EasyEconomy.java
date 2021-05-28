package plugins.wnplugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.security.InvalidParameterException;

public class EasyEconomy extends JavaPlugin {
    YamlConfiguration file;
    File f = new File(this.getDataFolder(),"setting.yml");
    private final int VALUE;
    private final String NAME;
    private final int MAX;
    private final String DESCRIPTION;
    private final boolean ALLOW_OUT;
    private final boolean ALLOW_IN;
    private final boolean ALLOW_TRANSFER;
    private final double EXCHANGE_TAX;
    private final double TRANSFER_TAX;


    public EasyEconomy(String id) {
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
    public void createBank(String bankName,double money,double max) throws IllegalArgumentException{
        if(max<money){
            throw new IllegalArgumentException("max小于money！");
        }

    }
    public int getRank(){

        return 0;
    }
    public void addToPlayer(){

    }
    public int getVALUE(){return VALUE;}
    public int getMAX(){return MAX;}
    public double getEXCHANGE_TAX(){return EXCHANGE_TAX;}
    public double getTRANSFER_TAX(){return TRANSFER_TAX;}
    public String getNAME(){return NAME;}
    public String getDESCRIPTION(){return DESCRIPTION;}
    public Boolean getALLOW_OUT(){return ALLOW_OUT;}
    public Boolean getALLOW_IN(){return ALLOW_IN;}
    public Boolean getALLOW_TRANSFER(){return ALLOW_TRANSFER;}
}
