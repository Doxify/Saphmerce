package org.saphron.saphmerce.utilities;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.saphron.saphmerce.ShopItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemStackCreator {

    private static NumberFormat df = NumberFormat.getCurrencyInstance();


    public static ItemStack createItemStack(Material guiMaterial, String name, List<String> lore) {
        ItemStack item = new ItemStack(guiMaterial, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack createShopItemStack(ShopItem shopItem) {
        ItemStack item = new ItemStack(shopItem.getDisplayItem());
        ItemMeta itemMeta = item.getItemMeta();
        List<String> itemLore = new ArrayList<>();

        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + shopItem.getName());

        if(shopItem.isBuyable()) {
            itemLore.add(ChatColor.GRAY + "Buy: " + ChatColor.GREEN + df.format(shopItem.getBuyPrice()));
        } else {
            itemLore.add(ChatColor.GRAY + "Buy: " + ChatColor.DARK_RED + "Unavailable");
        }

        if(shopItem.isSellable()) {
            itemLore.add(ChatColor.GRAY + "Sell: " + ChatColor.GREEN + df.format(shopItem.getSellPrice()));
        } else {
            itemLore.add(ChatColor.GRAY + "Sell: " + ChatColor.DARK_RED + "Unavailable");
        }

        itemLore.add("");

        if(shopItem.isBuyable()) {
            itemLore.add(ChatColor.GRAY + "Click to buy this item.");
        }

        if(shopItem.isSellable()) {
            itemLore.add(ChatColor.GRAY + "Click to sell this item.");
        }

        itemMeta.setLore(itemLore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack createPlaceholderItem() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 7);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        item.setItemMeta(itemMeta);

        return item;
    }

}
