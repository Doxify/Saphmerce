package org.saphron.saphmerce.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.saphron.saphmerce.Saphmerce;

public class ShopCommand implements CommandExecutor {

    Saphmerce plugin;

    public ShopCommand(Saphmerce p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if(args.length == 0) {
                Player p = (Player) sender;
                if(plugin.getShop().isEnabled()) {
                    p.openInventory(plugin.getShop().categoryGUI.getCategoryInterface(p));
                } else {
                    p.sendMessage(ChatColor.RED + "The shop is currently disabled.");
                }
            }
        }
        return true;
    }
}
