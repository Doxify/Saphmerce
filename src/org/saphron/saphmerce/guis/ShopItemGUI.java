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
import org.saphron.saphmerce.utilities.ItemStackCreator;

import java.util.Arrays;
import java.util.List;

public class ShopItemGUI {

    private Category category;
    private Inventory shopInventory;

    public ShopItemGUI(Category category) {
        this.category = category;
        generateShopItemGUI();
    }

    public void generateShopItemGUI() {
        List<ShopItem> shopItems = category.getCategoryItems();
        Inventory shopInventory = Bukkit.createInventory(null, 54, "Shop: Items");
        ItemStack[] bottomBarItems = generateShopInterfaceBottomBar();
        int bottomBarCounter = 0;

        for(int i = 0; i < shopItems.size(); i++) {
            ShopItem shopItem = shopItems.get(i);
            shopInventory.setItem(i, ItemStackCreator.createShopItemStack(shopItem));
        }

        // Bottom Bar Code

        for(int i = 45; i < 54; i++) {
            // Bottom Bar Code
            shopInventory.setItem(i, bottomBarItems[bottomBarCounter]);
            bottomBarCounter++;
        }

        this.shopInventory = shopInventory;
    }

    public static ItemStack[] generateShopInterfaceBottomBar() {
        ItemStack bottomBar[] = new ItemStack[9];

        for(int i = 0; i < 9; i++) {
            if(i == 1) {
                bottomBar[i] = ItemStackCreator.createItemStack(
                        Material.STAINED_GLASS_PANE,
                        ChatColor.RED + "Return",
                        Arrays.asList(ChatColor.GRAY + "Click to go back")
                );
                bottomBar[i].setDurability((short) 1);
            } else if(i == 7) {
                bottomBar[i] = ItemStackCreator.createItemStack(
                        Material.STAINED_GLASS_PANE,
                        ChatColor.RED + "Exit",
                        Arrays.asList(ChatColor.GRAY + "Click to exit shop")
                );
                bottomBar[i].setDurability((short) 14);
            } else {
                bottomBar[i] = ItemStackCreator.createPlaceholderItem();
            }
        }

        return bottomBar;
    }

    public Inventory getShopInventory() {
        return shopInventory;
    }

}
