package org.saphron.saphmerce;

import com.saphron.nsa.NSA;
import org.bukkit.plugin.java.JavaPlugin;
import org.saphron.saphmerce.commands.ShopAdminCommand;
import org.saphron.saphmerce.commands.ShopCommand;
import org.saphron.saphmerce.events.*;

public class Saphmerce extends JavaPlugin {

    private NSA nsa = null;
    private Shop shop = null;
    private FileManager fileManager = null;
    private ProfileManager profileManager = null;

    @Override
    public void onEnable() {
        nsa = (NSA) this.getServer().getPluginManager().getPlugin("NSA");
        profileManager = new ProfileManager();
        fileManager = new FileManager(this);
        shop = fileManager.loadShopFromFile();

        // Commands
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("shopAdmin").setExecutor(new ShopAdminCommand(this));

        // Events
        this.getServer().getPluginManager().registerEvents(new GeneralEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new ShopInterfaceEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new ShopTransactionEvent(this), this);
        this.getServer().getPluginManager().registerEvents(new ShopAdminEvent(this), this);
    }

    @Override
    public void onDisable() {
        fileManager.saveShopToFile();
        profileManager.handleServerClose();
    }

    public Shop getShop() { return shop; }
    public NSA getNsa() { return nsa; }
    public ProfileManager getProfileManager() { return profileManager; }
}
