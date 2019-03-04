package org.saphron.saphmerce.commands;

import com.saphron.nsa.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.saphron.saphblock.mine.MineModeManager;
import org.saphron.saphmerce.Category;
import org.saphron.saphmerce.Saphmerce;
import org.saphron.saphmerce.ShopItem;

public class SellCommand implements CommandExecutor {

    Saphmerce plugin;

    public SellCommand(Saphmerce p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(plugin.getShop().isEnabled()) {
                switch (args.length) {
                    case 0: {
                        if(p.getItemInHand().getType() != Material.AIR) {

                            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    ItemStack sellItem = new ItemStack(p.getItemInHand());

                                    for (Category category : plugin.getShop().getShopCategories()) {
                                        ShopItem shopItem = category.getShopItemByItemStack(sellItem);

                                        if (shopItem != null) {
                                            if (shopItem.isSellable()) {
                                                plugin.getShop().handleSell(p, shopItem, p.getItemInHand().getAmount());
                                                break;
                                            } else {
                                                p.sendMessage(ChatColor.RED + "This item cannot be sold.");
                                                break;
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "This item cannot be sold.");
                                            break;
                                        }
                                    }
                                }
                            });

                        } else {
                            p.sendMessage(ChatColor.RED + "You must be holding the item you want to sell.");
                        }
                        break;
                    }
                    case 1: {
                        if (args[0].equalsIgnoreCase("ALL")) {

                            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if(!plugin.getShop().handleSellAllInventory(p, p.getInventory(), false)) {
                                        p.sendMessage(ChatColor.RED + "Couldn't find any items for sale in your inventory!");
                                    }
                                }
                            });

                        } else if(args[0].equalsIgnoreCase("MINE")) {
                            MineModeManager mineModeManager = plugin.getSaphblock().mineModeManager;
                            if(mineModeManager.isInMineMode(p.getUniqueId().toString())) {

                                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!plugin.getShop().handleSellAllInventory(p, p.getInventory(), true)) {
                                            p.sendMessage(ChatColor.RED + "Couldn't find any items for sale in your inventory!");
                                        }
                                    }
                                });

                            } else {
                                p.sendMessage(ChatColor.RED + "You must be in mine mode to use this command.");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Usage: /sell all");
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } else {
                p.sendMessage(ChatColor.RED + "Shop is currently disabled.");
            }
        } else {
            sender.sendMessage(Utilities.NON_PLAYER_SENDER);
        }
        return true;
    }
}
