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
    private static final int COOLDOWN = 10;

    public SellCommand(Saphmerce p) { plugin = p; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            Profile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
            if(plugin.getShop().isEnabled()) {
                switch (args.length) {
                    case 0: {
                        if(!profile.hasSellCooldown()) {
                            if(p.getItemInHand().getType() != Material.AIR) {

                                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        ItemStack sellItem = new ItemStack(p.getItemInHand());

                                        for (Category category : plugin.getShop().getShopCategories()) {
                                            ShopItem shopItem = category.getShopItemByItemStack(sellItem);

                                            if (shopItem != null) {
                                                if (shopItem.isSellable()) {
                                                    handleSellCooldown(profile);
                                                    plugin.getApi().handleSell(p, shopItem, p.getItemInHand().getAmount());
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
                        } else {
                            p.sendMessage(ChatColor.RED + "You can only use the /sell command once every " + COOLDOWN + " seconds.");
                        }


                        break;
                    }
                    case 1: {
                        if(!profile.hasSellCooldown()) {
                            if (args[0].equalsIgnoreCase("ALL")) {
                                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        handleSellCooldown(profile);
                                        if(!plugin.getApi().handleSellAllInventory(p, p.getInventory())) {
                                            p.sendMessage(ChatColor.RED + "Couldn't find any items for sale in your inventory!");
                                        }
                                    }
                                });
                            } else {
                                p.sendMessage(ChatColor.RED + "Usage: /sell all");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "You can only use the /sell command once every " + COOLDOWN + " seconds.");
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

    private void handleSellCooldown(Profile profile) {
        profile.setSellColldown(true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                profile.setSellColldown(false);
            }
        }, COOLDOWN * 20);
    }

}
