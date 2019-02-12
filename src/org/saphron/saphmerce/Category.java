package org.saphron.saphmerce;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String name;
    private ItemStack displayItem;
    private boolean enabled;
    private List<ShopItem> categoryItems;


    public Category(String name, ItemStack displayItem, List<ShopItem> categoryItems) {
        setName(name);
        setDisplayItem(displayItem);
        setCategoryItems(categoryItems);
        setEnabled(true);
    }

    public Category(String name, ItemStack displayItem) {
        setName(name);
        setDisplayItem(displayItem);
        setEnabled(false);
        categoryItems = new ArrayList<>();
    }


    // Methods
    public void setName(String n) { name = n; }

    public void setDisplayItem(ItemStack i) { displayItem = i; }

    public void setEnabled(boolean e) { enabled = e; }

    public void setCategoryItems(List<ShopItem> itemList) { categoryItems = itemList; }


    public boolean addShopItem(ShopItem i) {
        if(categoryItems.size() < 45) {
            categoryItems.add(i);
            return true;
        }
        return false;
    }

    public void deleteShopItem(ShopItem i) { categoryItems.remove(i); }


    public String getName() { return name; }

    public ItemStack getDisplayItem() { return displayItem; }

    public boolean isEnabled() { return enabled; }

    public List<ShopItem> getCategoryItems() { return categoryItems; }

    public ShopItem getShopItemByName(String name) {
        for(ShopItem item : categoryItems) {
            if(item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public ShopItem getShopItemByItemStack(ItemStack itemStack) {
        for(ShopItem item : categoryItems) {
            if(item.getDisplayItem() == itemStack) {
                return item;
            }
        }
        return null;
    }


}