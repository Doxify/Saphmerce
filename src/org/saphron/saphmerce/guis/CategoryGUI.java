package org.saphron.saphmerce.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.Category;
import org.saphron.saphmerce.Shop;
import org.saphron.saphmerce.utilities.ItemStackCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CategoryGUI {

    private Shop shop;
    private static List<Integer> blackListSlots = new ArrayList<>(Arrays.asList(9, 17, 18, 26, 27, 35, 36, 44));
    private static Inventory categoryInterface;

    public CategoryGUI(Shop s) {
        shop = s;
    }

    public void generateCategoryInterface() {
        List<Category> shopCategories = shop.getShopCategories();
        Inventory categoryInventory = Bukkit.createInventory(null, 54, "Shop: Categories");
        int categoryCounter = 0;

        // Category items code
        for(int i = 10; i < (categoryInventory.getSize() - 10); i++) {
            if(!blackListSlots.contains(i)) {
                Category category = shopCategories.get(categoryCounter);
                ItemStack categoryItem = ItemStackCreator.createItemStack(
                        category.getDisplayItem().getType(),
                        ChatColor.LIGHT_PURPLE + category.getName(),
                        Arrays.asList(ChatColor.GRAY + "Items: " + ChatColor.YELLOW + category.getCategoryItems().size())
                );
                categoryInventory.setItem(i, categoryItem);
                categoryCounter++;

                if(categoryCounter == shopCategories.size()) {
                    break;
                }
            }
        }

        // Bottom bar code
        ItemStack[] bottomBarItems = generateCategoryInterfaceBottomBar();
        int bottomBarCounter = 0;
        for(int i = 45; i < 54; i++) {
            categoryInventory.setItem(i, bottomBarItems[bottomBarCounter]);
            bottomBarCounter++;
        }

        this.categoryInterface = categoryInventory;

    }

    public ItemStack[] generateCategoryInterfaceBottomBar() {
        ItemStack bottomBar[] = new ItemStack[9];

        for(int i = 0; i < 9; i++) {
            if(i == 4) {
                bottomBar[i] = ItemStackCreator.createItemStack(
                    Material.STAINED_GLASS_PANE,
                    ChatColor.RED + "Exit",
                    Arrays.asList(ChatColor.GRAY + "Click to exit shop.")
                );
                bottomBar[i].setDurability((short) 14);
            } else {
                bottomBar[i] = ItemStackCreator.createPlaceholderItem();
            }
        }

        return bottomBar;
    }

    public Inventory getCategoryInterface() {
        return categoryInterface;
    }

}
