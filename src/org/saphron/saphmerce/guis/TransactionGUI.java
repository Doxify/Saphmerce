package org.saphron.saphmerce.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.Shop;
import org.saphron.saphmerce.ShopItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionGUI {

    Shop shop;

    public TransactionGUI(Shop s) { shop = s; }

    public Inventory getTransactionGUI(ShopItem shopItem, Player p, int amount) {
        Inventory transactionInventory = Bukkit.createInventory(null, 54, "Shop: Transaction");
        ItemStack transactionItem = new ItemStack(shopItem.getDisplayItem());
        int transactionItemsInInventoryCount = shop.getItemCountFromInventory(p, transactionItem);
        transactionItem.setAmount(amount);

        // Sell Option ItemStack
        ItemStack sellItem = shop.itemStackCreator.createItemStack(
            Material.MINECART,
            ChatColor.RED + ChatColor.BOLD.toString() +  "Sell",
            Arrays.asList(
                ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + amount,
                ChatColor.GRAY + "Price: " + ChatColor.GREEN + "$" + shopItem.getSellPrice() * amount,
                "",
                ChatColor.GRAY + "Click to confirm sale."
            )
        );

        // Sell All Option ItemStack / Lore
        List<String> sellAllItemLore = new ArrayList<>();
        if(transactionItemsInInventoryCount > 0) {
            sellAllItemLore.add(ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + transactionItemsInInventoryCount);
            sellAllItemLore.add(ChatColor.GRAY + "Price: " + ChatColor.GREEN + "$" + shopItem.getSellPrice() * transactionItemsInInventoryCount);
            sellAllItemLore.add("");
            sellAllItemLore.add(ChatColor.GRAY + "Click to confirm sale.");
        } else {
            sellAllItemLore.add(ChatColor.DARK_RED + "Couldn't find any " + shopItem.getName() + "(s).");
        }
        ItemStack sellAllItem = shop.itemStackCreator.createItemStack(
            Material.EXPLOSIVE_MINECART,
            ChatColor.RED + ChatColor.BOLD.toString() + "Sell All",
            sellAllItemLore
        );

        // Buy All Option ItemStack
        ItemStack buyItem = shop.itemStackCreator.createItemStack(
            Material.STORAGE_MINECART,
            ChatColor.GREEN + ChatColor.BOLD.toString() +  "Buy",
            Arrays.asList(
                ChatColor.GRAY + "Amount: " + ChatColor.YELLOW + amount,
                ChatColor.GRAY + "Price: " + ChatColor.GREEN + "$" + shopItem.getBuyPrice() * amount,
                "",
                ChatColor.GRAY + "Click to confirm purchase."
            )
        );

        // Add 1 ItemStack
        ItemStack add1Item = shop.itemStackCreator.createItemStack(
                Material.INK_SACK,
                ChatColor.GREEN + "+ 1",
                null
        );
        add1Item.setDurability((byte) 10);

        // Add 5 ItemStack
        ItemStack add5Item = shop.itemStackCreator.createItemStack(
                Material.INK_SACK,
                ChatColor.GREEN + "+ 5",
                null
        );
        add5Item.setDurability((byte) 10);
        add5Item.setAmount(5);

        // Add 10 ItemStack
        ItemStack add10Item = shop.itemStackCreator.createItemStack(
                Material.INK_SACK,
                ChatColor.GREEN + "+ 10",
                Arrays.asList(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "SHIFT+CLICK = 64")
        );
        add10Item.setDurability((byte) 10);
        add10Item.setAmount(10);

        // Subtract 1 ItemStack
        ItemStack subtract1Item = shop.itemStackCreator.createItemStack(
                Material.INK_SACK,
                ChatColor.RED + "- 1",
                null
        );
        subtract1Item.setDurability((byte) 8);

        // Subtract 5 ItemStack
        ItemStack subtract5Item = shop.itemStackCreator.createItemStack(
                Material.INK_SACK,
                ChatColor.RED + "- 5",
                null
        );
        subtract5Item.setDurability((byte) 8);
        subtract5Item.setAmount(5);

        // Subtract 10 ItemStack
        ItemStack subtract10Item = shop.itemStackCreator.createItemStack(
                Material.INK_SACK,
                ChatColor.RED + "- 10",
                Arrays.asList(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "SHIFT+CLICK = 1")
        );
        subtract10Item.setDurability((byte) 8);
        subtract10Item.setAmount(10);


        transactionInventory.setItem(13, transactionItem);

        if(shopItem.isSellable()) {
            transactionInventory.setItem(30, sellItem);
            transactionInventory.setItem(31, sellAllItem);
        }

        if(shopItem.isBuyable()) {
            transactionInventory.setItem(32, buyItem);
        }

        transactionInventory.setItem(12, subtract1Item);
        transactionInventory.setItem(11, subtract5Item);
        transactionInventory.setItem(10, subtract10Item);
        transactionInventory.setItem(14, add1Item);
        transactionInventory.setItem(15, add5Item);
        transactionInventory.setItem(16, add10Item);

        // Bottom bar code
        ItemStack[] bottomBarItems = shop.shopItemGUI.generateShopInterfaceBottomBar();
        int bottomBarCounter = 0;
        for(int i = 45; i < 54; i++) {
            transactionInventory.setItem(i, bottomBarItems[bottomBarCounter]);
            bottomBarCounter++;
        }


        return transactionInventory;


    }
}