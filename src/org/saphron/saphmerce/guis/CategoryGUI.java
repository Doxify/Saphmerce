package org.saphron.saphmerce.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.Category;
import org.saphron.saphmerce.Shop;

import java.util.Arrays;
import java.util.List;

public class CategoryGUI {

    Shop shop;

    public CategoryGUI(Shop s) {
        shop = s;
    }

    public Inventory getCategoryInterface(Player p) {
        List<Category> shopCategories = shop.getShopCategories();
        Inventory categoryInventory = Bukkit.createInventory(null, 54, "Shop: Categories");
        int categoryCounter = 0;

        // Category items code
        for(int i = 10; i < (shopCategories.size() + 10); i++) {
            Category category = shopCategories.get(categoryCounter);
            ItemStack categoryItem = shop.itemStackCreator.createItemStack(
                category.getDisplayItem().getType(),
                ChatColor.LIGHT_PURPLE + category.getName(),
                Arrays.asList(ChatColor.GRAY + "Items: " + ChatColor.YELLOW + category.getCategoryItems().size())
            );
            categoryInventory.setItem(i, categoryItem);
            categoryCounter++;
        }

        // Bottom bar code
        ItemStack[] bottomBarItems = generateCategoryInterfaceBottomBar();
        int bottomBarCounter = 0;
        for(int i = 45; i < 54; i++) {
            categoryInventory.setItem(i, bottomBarItems[bottomBarCounter]);
            bottomBarCounter++;
        }

        return categoryInventory;

    }

    public ItemStack[] generateCategoryInterfaceBottomBar() {
        ItemStack bottomBar[] = new ItemStack[9];

        for(int i = 0; i < 9; i++) {
            if(i == 4) {
                bottomBar[i] = shop.itemStackCreator.createItemStack(
                    Material.BARRIER,
                    ChatColor.RED + "Exit",
                    Arrays.asList(ChatColor.GRAY + "Click to exit shop.")
                );
            } else {
                bottomBar[i] = shop.itemStackCreator.createPlaceholderItem();
            }
        }

        return bottomBar;
    }

}
