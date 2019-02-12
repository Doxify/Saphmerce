package org.saphron.saphmerce.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.saphron.saphmerce.*;

public class ShopInterfaceEvents implements Listener {

    Saphmerce plugin;
    Shop shop;


    public ShopInterfaceEvents(Saphmerce p) { plugin = p; shop = plugin.getShop(); }

    @EventHandler
    public void onInventoryInteractEvent(InventoryInteractEvent e) {
        String inventoryName = e.getInventory().getName();

        if(inventoryName.toUpperCase().contains("SHOP")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent e) {
        String inventoryName = e.getInventory().getName();

        if(inventoryName.toUpperCase().contains("SHOP")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        String inventoryName = e.getInventory().getName();

        if(inventoryName.toUpperCase().contains("SHOP") && e.getCurrentItem() != null) {
            e.setCancelled(true);
            if(e.getCurrentItem().getType() != Material.AIR) {
                Player p = (Player) e.getWhoClicked();
                Profile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
                String clickedItemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

                if(clickedItemName.equalsIgnoreCase("EXIT")) {
                    p.closeInventory();

                } else {
                    if(!clickedItemName.equalsIgnoreCase(" ")) {
                        if(inventoryName.equalsIgnoreCase("SHOP: CATEGORIES")) {
                            Category clickedCategory = shop.getShopCategoryByName(clickedItemName);

                            if(clickedCategory != null) {
                                p.openInventory(shop.shopItemGUI.generateShopItemGUI(p, clickedCategory));
                                profile.setClickedCategory(clickedCategory);
                            }

                        } else if(inventoryName.equalsIgnoreCase("SHOP: ITEMS")) {

                            // Uesr returns from items view to category view
                            if(clickedItemName.equalsIgnoreCase("RETURN")) {
                                p.openInventory(shop.categoryGUI.getCategoryInterface(p));
                                profile.clearClickedItems();

                            } else {
                                // User clicked on a shop item.
                                ShopItem clickedItem = profile.getClickedCategory().getShopItemByName(clickedItemName);
                                if(clickedItem != null) {
                                    profile.setClickedShopItem(clickedItem);
                                    if(e.getClick().isShiftClick() && p.hasPermission("saphmerce.admin")) {
                                        p.openInventory(shop.adminGUI.getShopItemAdminGUI(clickedItem));
                                    } else {
                                        p.openInventory(shop.transactionGUI.getTransactionGUI(clickedItem, p, 1));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
