package org.saphron.saphmerce.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.Category;
import org.saphron.saphmerce.Shop;
import org.saphron.saphmerce.ShopItem;

import java.util.Arrays;
import java.util.List;

public class ShopItemGUI {

    Shop shop;

    public ShopItemGUI(Shop s) { shop = s; }

    public Inventory generateShopItemGUI(Player p, Category category) {
        List<ShopItem> shopItems = category.getCategoryItems();
        Inventory shopInventory = Bukkit.createInventory(null, 54, "Shop: Items");
        ItemStack[] bottomBarItems = generateShopInterfaceBottomBar();
        int bottomBarCounter = 0;

        for(int i = 0; i < shopItems.size(); i++) {
            ShopItem shopItem = shopItems.get(i);
            shopInventory.setItem(i, shop.itemStackCreator.createShopItemStack(shopItem));
        }

        // Bottom Bar Code

        for(int i = 45; i < 54; i++) {
            // Bottom Bar Code
            shopInventory.setItem(i, bottomBarItems[bottomBarCounter]);
            bottomBarCounter++;
        }

        return shopInventory;
    }

    public ItemStack[] generateShopInterfaceBottomBar() {
        ItemStack bottomBar[] = new ItemStack[9];

        for(int i = 0; i < 9; i++) {
            if(i == 1) {
                bottomBar[i] = shop.itemStackCreator.createItemStack(
                        Material.IRON_DOOR,
                        ChatColor.RED + "Return",
                        Arrays.asList(ChatColor.GRAY + "Click to go back")
                );
            } else if(i == 7) {
                bottomBar[i] = shop.itemStackCreator.createItemStack(
                        Material.BARRIER,
                        ChatColor.RED + "Exit",
                        Arrays.asList(ChatColor.GRAY + "Click to exit shop")
                );
            } else {
                bottomBar[i] = shop.itemStackCreator.createPlaceholderItem();
            }
        }

        return bottomBar;
    }

}
