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
import org.saphron.saphmerce.guis.AdminGUI;
import org.saphron.saphmerce.utilities.InventoryGuard;

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
        if(InventoryGuard.passedInventoryChecks(e, "Shop")) {
            Player p = (Player) e.getWhoClicked();
            Profile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
            String clickedItemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

            if(clickedItemName.equalsIgnoreCase("EXIT")) {
                p.closeInventory();
                return;
            } else {
                if(!clickedItemName.equalsIgnoreCase(" ")) {
                    String inventoryName = e.getClickedInventory().getName();
                    if(inventoryName.equalsIgnoreCase("SHOP: CATEGORIES")) {
                        // Handling clicks in the category menu
                        Category clickedCategory = shop.getShopCategoryByName(clickedItemName);
                        if(clickedCategory != null) {
                            p.openInventory(clickedCategory.getShopItemGUI().getShopInventory());
                            profile.setClickedCategory(clickedCategory);
                            return;
                        }
                    } else if(inventoryName.equalsIgnoreCase("SHOP: ITEMS")) {
                        // Uesr returns from items view to category view
                        if(clickedItemName.equalsIgnoreCase("RETURN")) {
                            p.openInventory(shop.categoryGUI.getCategoryInterface());
                            profile.clearClickedItems();
                            return;
                        } else {
                            // User clicked on a shop item.
                            ShopItem clickedItem = profile.getClickedCategory().getShopItemByName(clickedItemName);
                            if(clickedItem != null) {
                                profile.setClickedShopItem(clickedItem);
                                if(e.getClick().isRightClick() && p.hasPermission("saphmerce.admin")) {
                                    p.openInventory(AdminGUI.getShopItemAdminGUI(clickedItem));
                                    return;
                                } else {
                                    p.openInventory(shop.transactionGUI.getTransactionGUI(clickedItem, p, 1));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
