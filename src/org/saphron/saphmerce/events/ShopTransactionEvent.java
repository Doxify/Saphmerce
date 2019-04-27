package org.saphron.saphmerce.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.saphron.saphmerce.*;
import org.saphron.saphmerce.utilities.InventoryGuard;

public class ShopTransactionEvent implements Listener {

    public static Saphmerce plugin;
    public Shop shop;

    public ShopTransactionEvent(Saphmerce p) {
        plugin = p;
        shop = p.getShop();
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if(InventoryGuard.passedInventoryChecks(e, "Shop: Transaction")) {
            Player p = (Player) e.getWhoClicked();
            Profile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
            String clickedItemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

            if(clickedItemName.equalsIgnoreCase("EXIT")) {
                p.closeInventory();
                return;
            } else if(!clickedItemName.equalsIgnoreCase(" ")) {
                int transactionItemAmount = e.getInventory().getItem(13).getAmount();
                ShopItem transactionShopItem = profile.getClickedShopItem();

                switch (clickedItemName.toUpperCase()) {
                    case "+ 1": {
                        if (transactionItemAmount < 64) {
                            transactionItemAmount++;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        }
                    }
                    case "+ 5": {
                        if (transactionItemAmount < 60) {
                            transactionItemAmount += 5;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        } else {
                            transactionItemAmount = 64;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        }
                    }
                    case "+ 10": {
                        if (transactionItemAmount < 55) {
                            transactionItemAmount += 10;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        } else {
                            transactionItemAmount = 64;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        }
                    }
                    case "- 1": {
                        if(transactionItemAmount > 1) {
                            transactionItemAmount--;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        }

                    }
                    case "- 5": {
                        if(transactionItemAmount > 5) {
                            transactionItemAmount -= 5;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        } else {
                            transactionItemAmount = 1;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        }
                    }
                    case "- 10": {
                        if(transactionItemAmount > 10) {
                            transactionItemAmount -= 10;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        } else {
                            transactionItemAmount = 1;
                            p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                            break;
                        }
                    }
                    case "SELL": {
                        plugin.getApi().handleSell(p, transactionShopItem, transactionItemAmount);
                        p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                        break;
                    }
                    case "SELL ALL": {
                        plugin.getApi().handleSellAll(p, transactionShopItem);
                        p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                        break;
                    }
                    case "BUY": {
                        plugin.getApi().handleBuy(p, transactionShopItem, transactionItemAmount);
                        p.openInventory(shop.transactionGUI.getTransactionGUI(transactionShopItem, p, transactionItemAmount));
                        break;
                    }
                    case "RETURN": {
                        p.openInventory(profile.getClickedCategory().getShopItemGUI().getShopInventory());
                        break;
                    }
                    case "EXIT": {
                        p.closeInventory();
                        profile.clearClickedItems();
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
