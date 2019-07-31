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
import org.saphron.saphmerce.Category;
import org.saphron.saphmerce.Profile;
import org.saphron.saphmerce.Saphmerce;
import org.saphron.saphmerce.ShopItem;

public class SellCommand implements CommandExecutor {

    Saphmerce plugin;

    public SellCommand(Saphmerce p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            Profile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
            if(plugin.getShop().isEnabled()) {

                if(profile.hasSellCooldown())  {
                    p.sendMessage(ChatColor.RED + "You can only use the /sell command once every " + plugin.getApi().COOLDOWN + " seconds.");
                    return true;
                }

                switch (args.length) {
                    default: {
                        sendHelpMessage(p);
                        break;
                    }
                    case 1: {
                        if (args[0].equalsIgnoreCase("ALL")) {
                            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    plugin.getApi().handleSellCooldown(profile);
                                    if (!plugin.getApi().handleSellAllInventory(p, p.getInventory())) {
                                        p.sendMessage(ChatColor.RED + "Couldn't find any items for sale in your inventory!");
                                    }
                                }
                            });
                        } else if(args[0].equalsIgnoreCase("HAND")) {
                            if(p.getItemInHand() != null) {
                                if(!p.getItemInHand().getType().equals(Material.AIR)) {
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            ItemStack sellItem = p.getItemInHand().clone();

                                            for (Category category : plugin.getShop().getShopCategories()) {
                                                ShopItem shopItem = category.getShopItemByItemStack(sellItem);

                                                if (shopItem != null) {
                                                    if (shopItem.isSellable()) {
                                                        plugin.getApi().handleSellCooldown(profile);
                                                        plugin.getApi().handleSell(p, shopItem, p.getItemInHand().getAmount());
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "This item cannot be sold.");
                                                    }
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    p.sendMessage(ChatColor.RED + "You must be holding the item you want to sell.");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "You must be holding the item you want to sell.");
                            }
                        } else {
                            sendHelpMessage(p);
                        }
                        break;
                    }
                    case 2: {
                        if(args[0].equalsIgnoreCase("all") && args[1].equalsIgnoreCase("hand")) {
                            if(p.getItemInHand() != null) {
                                if(!p.getItemInHand().getType().equals(Material.AIR)) {
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            ItemStack sellItem = p.getItemInHand().clone();

                                            for (Category category : plugin.getShop().getShopCategories()) {
                                                ShopItem shopItem = category.getShopItemByItemStack(sellItem);

                                                if (shopItem != null) {
                                                    if (shopItem.isSellable()) {
                                                        plugin.getApi().handleSellCooldown(profile);
                                                        plugin.getApi().handleSellAll(p, shopItem);
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "This item cannot be sold.");
                                                    }
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    p.sendMessage(ChatColor.RED + "You must be holding the item you want to sell.");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "You must be holding the item you want to sell.");
                            }



                        } else {
                            sendHelpMessage(p);
                        }
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


    private void sendHelpMessage(Player p) {
        p.sendMessage(ChatColor.RED + "Usage:");
        p.sendMessage(ChatColor.RED + "/sell hand");
        p.sendMessage(ChatColor.GRAY + "Sell the item you are holding");
        p.sendMessage(ChatColor.RED + "/sell all");
        p.sendMessage(ChatColor.GRAY + "Sell your entire inventory");
        p.sendMessage(ChatColor.RED + "/sell all hand");
        p.sendMessage(ChatColor.GRAY + "Sell all items from inventory similar to the one you're holding");
    }

}
