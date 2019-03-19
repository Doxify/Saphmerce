package org.saphron.saphmerce.guis;

import com.saphron.nsa.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.saphron.saphmerce.Category;
import org.saphron.saphmerce.Shop;
import org.saphron.saphmerce.ShopItem;

import java.util.Arrays;

public class AdminGUI {

    Shop shop;

    public AdminGUI(Shop s) {
        shop = s;
    }

    public Inventory getShopItemAdminGUI(ShopItem shopItem) {
        Inventory shopAdminInventory = Bukkit.createInventory(null, 36, "Shop: Admin");
        ItemStack[] bottomBar = shop.shopItemGUI.generateShopInterfaceBottomBar();
        int bottomBarCounter = 0;
        ItemStack shopItemStack = new ItemStack(shopItem.getDisplayItem());
        ItemMeta shopItemStackMeta = shopItemStack.getItemMeta();
        shopItemStackMeta.setLore(
            Arrays.asList(
                    ChatColor.GRAY + "Name: " + ChatColor.LIGHT_PURPLE + shopItem.getName(),
                    ChatColor.GRAY + "Buy Price: " + ChatColor.GREEN + Utilities.moneyFormat.format(shopItem.getBuyPrice()),
                    ChatColor.GRAY + "Sell Price: " + ChatColor.RED + Utilities.moneyFormat.format(shopItem.getSellPrice()),
                    "",
                    ChatColor.GRAY + "Command Item: " + ChatColor.YELLOW + shopItem.isCommandItem(),
                    (shopItem.isCommandItem() ? ChatColor.YELLOW + "/" + shopItem.getCommandString() : "")
                )
        );
        shopItemStack.setItemMeta(shopItemStackMeta);


        ItemStack renameItem = shop.itemStackCreator.createItemStack(
                Material.NAME_TAG,
                ChatColor.LIGHT_PURPLE + "Rename",
                Arrays.asList(ChatColor.GRAY + "Click to rename.")
        );

        ItemStack editBuyPriceItem = shop.itemStackCreator.createItemStack(
                Material.STORAGE_MINECART,
                ChatColor.LIGHT_PURPLE + "Change Buy Price",
                Arrays.asList(ChatColor.GRAY + "Click to edit buy price")
        );

        ItemStack editSellPriceItem = shop.itemStackCreator.createItemStack(
                Material.EXPLOSIVE_MINECART,
                ChatColor.LIGHT_PURPLE + "Change Sell Price",
                Arrays.asList(ChatColor.GRAY + "Click to edit sale price")
        );

        ItemStack commandItem = shop.itemStackCreator.createItemStack(
                Material.PAPER,
                ChatColor.LIGHT_PURPLE + "Command",
                Arrays.asList(ChatColor.GRAY + (shopItem.isCommandItem() ? ChatColor.YELLOW + "/" + shopItem.getCommandString() : ChatColor.GREEN + "Click to set item command"))
        );

        ItemStack deleteItem = shop.itemStackCreator.createItemStack(
                Material.REDSTONE,
                ChatColor.RED + "Delete Item",
                Arrays.asList(ChatColor.GRAY + "Click to delete item")
        );

        shopAdminInventory.setItem(10, shopItemStack);
        shopAdminInventory.setItem(12, renameItem);
        shopAdminInventory.setItem(13, editBuyPriceItem);
        shopAdminInventory.setItem(14, editSellPriceItem);
        shopAdminInventory.setItem(15, commandItem);
        shopAdminInventory.setItem(16, deleteItem);

        for(int i = 27; i < 36; i++) {
            shopAdminInventory.setItem(i, bottomBar[bottomBarCounter]);
            bottomBarCounter++;
        }

        return shopAdminInventory;
    }
}
