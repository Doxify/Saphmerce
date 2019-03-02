package org.saphron.saphmerce;

import com.saphron.nsa.NSA;
import org.bukkit.plugin.java.JavaPlugin;
import org.saphron.saphblock.Saphblock;
import org.saphron.saphmerce.commands.SellCommand;
import org.saphron.saphmerce.commands.ShopAdminCommand;
import org.saphron.saphmerce.commands.ShopCommand;
import org.saphron.saphmerce.events.*;

public class Saphmerce extends JavaPlugin {

    private NSA nsa = null;
    private Saphblock saphblock = null;
    private Shop shop = null;
    private FileManager fileManager = null;
    private ProfileManager profileManager = null;
    private double mineModeMultiplier = 0;

    @Override
    public void onEnable() {
        nsa = (NSA) this.getServer().getPluginManager().getPlugin("NSA");
        saphblock = (Saphblock) this.getServer().getPluginManager().getPlugin("saphblock");
        profileManager = new ProfileManager();
        fileManager = new FileManager(this);
        shop = fileManager.loadShopFromFile();

        // Commands
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("sell").setExecutor(new SellCommand(this));
        getCommand("shopAdmin").setExecutor(new ShopAdminCommand(this));

        // Events
        this.getServer().getPluginManager().registerEvents(new GeneralEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new ShopInterfaceEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new ShopTransactionEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new ShopAdminEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new SellAllStickEvent(this), this);
    }

    @Override
    public void onDisable() {
        fileManager.saveShopToFile();
        profileManager.handleServerClose();
    }

    public Shop getShop() { return shop; }
    public NSA getNsa() { return nsa; }
    public Saphblock getSaphblock() { return saphblock; }
    public ProfileManager getProfileManager() { return profileManager; }

    public void setMineModeMultiplier(double m) { mineModeMultiplier = m; };
    public double getMineModeMultiplier() { return mineModeMultiplier; }
}
