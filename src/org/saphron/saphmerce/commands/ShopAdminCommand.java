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
import org.saphron.saphmerce.Saphmerce;
import org.saphron.saphmerce.ShopItem;

public class ShopAdminCommand implements CommandExecutor {

    Saphmerce plugin;

    public ShopAdminCommand(Saphmerce p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("saphmerce.admin")) {
                Player p = (Player) sender;


                // shopadmin createCategory "NAME"
                // shopAdmin deleteCategory "NAME"
                // shopadmin giveSellAllStick "player"
                if(args.length == 2) {
                    String categoryName = args[1];

                    switch (args[0].toUpperCase()) {
                        case "CREATECATEGORY": {
                            ItemStack displayItem = p.getItemInHand();

                            if(displayItem.getType() != Material.AIR) {
                                Category category = new Category(categoryName, displayItem);
                                plugin.getShop().addCategory(category);
                                p.sendMessage(ChatColor.GREEN + "Successfully created new shop category: " + category.getName());
                            } else {
                                p.sendMessage(ChatColor.RED + "You must be holding the item you want to represent the category in your hand.");
                            }

                            break;
                        }
                        case "DELETECATEGORY": {
                            if(plugin.getShop().deleteCategory(categoryName)) {
                                p.sendMessage(ChatColor.GREEN + "Successfully deleted category: " + categoryName);
                            } else {
                                p.sendMessage(ChatColor.RED + "Could not find a category with that name.");
                            }

                            break;
                        }
                        case "GIVESELLALLSTICK": {
                            Player target = Bukkit.getPlayer(args[1]);
                            if(target != null) {
                                if(target.getInventory().firstEmpty() != -1) {
                                    target.getInventory().addItem(plugin.getShop().getSellAllStick());
                                    target.sendMessage(ChatColor.GREEN + "You've been given a sell all stick, keep it safe!");
                                } else {
                                    target.getWorld().dropItem(target.getLocation(), plugin.getShop().getSellAllStick());
                                    target.sendMessage(ChatColor.GREEN + "You've been given a sell all stick, keep it safe!");
                                    target.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "There was no room in your inventory so your sell all stick has been dropped on the ground!");
                                }
                                p.sendMessage(ChatColor.GREEN + "Successfully gave " + target.getName() + " a sell all stick.");
                                break;
                            } else {
                                p.sendMessage(ChatColor.RED + args[1] + " is not online.");
                                break;
                            }
                        }
                        default: {
                            displayShopAdminCommands(p);
                            break;
                        }
                    }

                // shopadmin addItem "CATEGORY" "BUY PRICE" "SELL PRICE" "ITEM NAME"
                } else if(args.length == 5 || args.length > 5) {
                    if(args[0].equalsIgnoreCase("ADDITEM")) {
                        ItemStack displayItem = p.getItemInHand();
                        String categoryName = args[1];
                        double buyPrice = 0;
                        double sellPrice = 0;
                        Category category = plugin.getShop().getShopCategoryByName(categoryName);

                        if(category instanceof Category) {
                            if(displayItem.getType() != Material.AIR) {
                                try {
                                    buyPrice = Double.parseDouble(args[2]);
                                    sellPrice = Double.parseDouble(args[3]);
                                } catch (ClassCastException e) {
                                    p.sendMessage(ChatColor.RED + "Invalid syntax.");
                                    return true;
                                }

                                StringBuilder itemName = new StringBuilder();
                                for(int i = 4; i < args.length; i++) {
                                    if(i == 4) {
                                        itemName.append(args[i]);
                                    } else {
                                        itemName.append(" " + args[i]);
                                    }
                                }

                                if(category.addShopItem(new ShopItem(itemName.toString(), displayItem, buyPrice, sellPrice))) {
                                    p.sendMessage(ChatColor.GREEN + "Successfully created a new shop item: " + itemName);
                                    return true;
                                } else {
                                    p.sendMessage(ChatColor.RED + "This category has reached the maximum of 45 items.");
                                    return true;
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "You must be holding the item you want add to the shop in your hand.");
                                return true;
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Could not a find a category with the name " + categoryName);
                            return true;
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Invalid syntax.");
                        displayShopAdminCommands(p);
                        return true;
                    }
                } else {
                    displayShopAdminCommands(p);
                }

            } else {
                sender.sendMessage(Utilities.NO_PERMISSION);
            }
        }
        return true;
    }

    public void displayShopAdminCommands(Player p) {
        p.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Usage:");
        p.sendMessage(ChatColor.RED + "/shopAdmin createCategory 'categoryName'");
        p.sendMessage(ChatColor.RED + "/shopAdmin deleteCategory 'categoryName'");
        p.sendMessage(ChatColor.RED + "/shopAdmin addItem 'categoryName' 'buy price' 'sell price' 'item name'");
        p.sendMessage(ChatColor.RED + "/shopAdmin giveSellAllStick 'player name'");
    }

}


