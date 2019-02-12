package org.saphron.saphmerce.events;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitTask;
import org.saphron.saphmerce.*;

public class ShopAdminEvent implements Listener {

    Saphmerce plugin;
    Shop shop;

    public ShopAdminEvent(Saphmerce p) { plugin = p; shop = p.getShop(); }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        String inventoryName = e.getInventory().getName();

        if (inventoryName.toUpperCase().contains("SHOP") && e.getCurrentItem() != null) {
            e.setCancelled(true);

            if (e.getCurrentItem().getType() != Material.AIR) {
                Player p = (Player) e.getWhoClicked();
                Profile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
                String clickedItemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

                if (clickedItemName.equalsIgnoreCase("EXIT")) {
                    p.closeInventory();

                } else if (!clickedItemName.equalsIgnoreCase(" ")) {

                    if (inventoryName.equalsIgnoreCase("SHOP: ADMIN") && p.hasPermission("saphmerce.admin")) {
                        Category clickedCategory = profile.getClickedCategory();
                        ShopItem clickedShopItem = profile.getClickedShopItem();


                        switch (clickedItemName.toUpperCase()) {
                            case "RENAME": {
                                new AnvilGUI(plugin, p, "Name: " + clickedShopItem.getName(), (player, reply) -> {
                                    if(!reply.equalsIgnoreCase(clickedShopItem.getName())) {
                                        clickedShopItem.setName(reply);
                                        player.sendMessage(ChatColor.GREEN + "Shop item successfully renamed.");
                                    }

                                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            player.openInventory(shop.adminGUI.getShopItemAdminGUI(clickedShopItem));
                                        }
                                    }, 1L);

                                    return null;
                                });

                                break;
                            }
                            case "CHANGE BUY PRICE": {
                                new AnvilGUI(plugin, p, "Buy Price: " + clickedShopItem.getBuyPrice(), (player, reply) -> {
                                    try {
                                        double newBuyPrice = Double.parseDouble(reply);
                                        clickedShopItem.setBuyPrice(newBuyPrice);
                                        player.sendMessage(ChatColor.GREEN + "Successfully changed buy price to " + clickedShopItem.getBuyPrice());

                                        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                            @Override
                                            public void run() {
                                                player.openInventory(shop.adminGUI.getShopItemAdminGUI(clickedShopItem));
                                            }
                                        }, 1L);

                                        return null;
                                    } catch (NumberFormatException error) {
                                        player.sendMessage(ChatColor.RED + "Syntax Error: Buy price MUST be a double.");
                                        return "Incorrect";
                                    }
                                });

                                break;
                            }
                            case "CHANGE SELL PRICE": {
                                new AnvilGUI(plugin, p, "Sell Price: " + clickedShopItem.getSellPrice(), (player, reply) -> {
                                    try {
                                        double newSellPrice = Double.parseDouble(reply);
                                        clickedShopItem.setSellPrice(newSellPrice);
                                        player.sendMessage(ChatColor.GREEN + "Successfully changed sell price to " + clickedShopItem.getSellPrice());

                                        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                            @Override
                                            public void run() {
                                                player.openInventory(shop.adminGUI.getShopItemAdminGUI(clickedShopItem));
                                            }
                                        }, 1L);

                                        return null;
                                    } catch (NumberFormatException error) {
                                        player.sendMessage(ChatColor.RED + "Syntax Error: Sell price MUST be a double.");
                                        return "Incorrect";
                                    }
                                });

                                break;
                            }
                            case "COMMAND": {
                                if(clickedShopItem.isCommandItem()) {
                                    clickedShopItem.setCommandItem(false);
                                    clickedShopItem.setItemCommand(null);
                                    p.openInventory(shop.adminGUI.getShopItemAdminGUI(clickedShopItem));
                                    break;
                                } else {
                                    new AnvilGUI(plugin, p, "EX: es give <p> pig", (player, reply) -> {
                                        clickedShopItem.setItemCommand(reply);
                                        clickedShopItem.setCommandItem(true);
                                        player.sendMessage(ChatColor.GREEN + "Set command for " + clickedShopItem.getName() + ": " + clickedShopItem.getCommandString());

                                        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                                            @Override
                                            public void run() {
                                                player.openInventory(shop.adminGUI.getShopItemAdminGUI(clickedShopItem));
                                            }
                                        }, 1L);

                                        return null;
                                    });
                                    break;
                                }
                            }
                            case "DELETE ITEM": {
                                clickedCategory.deleteShopItem(clickedShopItem);
                                p.sendMessage(ChatColor.GREEN + "Successfully deleted item.");
                                p.openInventory(shop.shopItemGUI.generateShopItemGUI(p, clickedCategory));
                                break;
                            }
                            case "RETURN": {
                                p.openInventory(shop.shopItemGUI.generateShopItemGUI(p, clickedCategory));
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}