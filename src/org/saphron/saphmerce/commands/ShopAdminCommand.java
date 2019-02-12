package org.saphron.saphmerce.commands;

import com.saphron.nsa.Utilities;
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
                if(args.length == 2) {
                    String categoryName = args[1];

                    switch (args[0].toUpperCase()) {
                        case "CREATECATEGORY": {
                            ItemStack displayItem = p.getItemInHand();

                            if(displayItem.getType() != Material.AIR) {
                                Category category = new Category(categoryName, displayItem);
                                if(plugin.getShop().addCategory(category)) {
                                    p.sendMessage(ChatColor.GREEN + "Successfully created new shop category: " + category.getName());
                                } else {
                                    p.sendMessage(ChatColor.RED + "A category with that name already exists.");
                                }
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
                        default: {
                            p.sendMessage(ChatColor.RED + "Usage: /shopAdmin createCategory 'categoryName'");
                            p.sendMessage(ChatColor.RED + "Usage: /shopAdmin deleteCategory 'categoryName'");
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
                        return true;
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "/shopAdmin");
                }

            } else {
                sender.sendMessage(Utilities.NO_PERMISSION);
            }
        }
        return true;
    }

}


