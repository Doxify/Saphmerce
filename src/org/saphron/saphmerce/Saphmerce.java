package org.saphron.saphmerce;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.saphron.saphmerce.commands.SellCommand;
import org.saphron.saphmerce.commands.ShopAdminCommand;
import org.saphron.saphmerce.commands.ShopCommand;
import org.saphron.saphmerce.events.*;


public class Saphmerce extends JavaPlugin {

    private static Saphmerce instance;
    private Shop shop = null;
    private FileManager fileManager = null;
    private ProfileManager profileManager = null;
    private double multiplier = 0;
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private static API api = null;

    @Override
    public void onEnable() {
        instance = this;
        profileManager = new ProfileManager();
        fileManager = new FileManager(this);
        shop = fileManager.loadShopFromFile();
        api = new API();

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

        // Vault hook
        setupChat();
        setupPermissions();
        setupEcon();

    }

    @Override
    public void onDisable() {
        fileManager.saveShopToFile();
        profileManager.handleServerClose();
    }

    public API getApi() { return api; }
    public Shop getShop() { return shop; }
    public ProfileManager getProfileManager() { return profileManager; }
    public Permission getPerms() { return perms; }
    public Chat getChat() { return chat; }
    public Economy getEcon() { return econ; }

    public void setMultiplier(double m) { multiplier = m; }
    public double getMultiplier() { return multiplier; }

    private boolean setupEcon() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if(rsp == null) {
            return false;
        }
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if(rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Saphmerce getPlugin() {
        return instance;
    }
}
