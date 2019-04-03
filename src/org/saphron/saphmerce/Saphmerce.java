package org.saphron.saphmerce;

import com.saphron.nsa.NSA;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.saphron.saphblock.Saphblock;
import org.saphron.saphmerce.commands.SellCommand;
import org.saphron.saphmerce.commands.ShopAdminCommand;
import org.saphron.saphmerce.commands.ShopCommand;
import org.saphron.saphmerce.events.*;

import java.security.Permission;

public class Saphmerce extends JavaPlugin {

    private Saphblock saphblock = null;
    private Shop shop = null;
    private FileManager fileManager = null;
    private ProfileManager profileManager = null;
    private double mineModeMultiplier = 0;
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    @Override
    public void onEnable() {
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

    public Shop getShop() { return shop; }
    public Saphblock getSaphblock() { return saphblock; }
    public ProfileManager getProfileManager() { return profileManager; }
    public Permission getPerms() { return perms; }
    public Chat getChat() { return chat; }
    public Economy getEcon() { return econ; }

    public void setMineModeMultiplier(double m) { mineModeMultiplier = m; }
    public double getMineModeMultiplier() { return mineModeMultiplier; }

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
}
