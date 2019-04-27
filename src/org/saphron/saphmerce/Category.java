package org.saphron.saphmerce;

import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.guis.ShopItemGUI;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private String name;
    private ItemStack displayItem;
    private boolean enabled;
    private List<ShopItem> categoryItems;
    private ShopItemGUI shopItemGUI;


    public Category(String name, ItemStack displayItem, List<ShopItem> categoryItems) {
        setName(name);
        setDisplayItem(displayItem);
        setCategoryItems(categoryItems);
        setEnabled(true);
        shopItemGUI = new ShopItemGUI(this);
    }

    public Category(String name, ItemStack displayItem) {
        setName(name);
        setDisplayItem(displayItem);
        setEnabled(false);
        categoryItems = new ArrayList<>();
        shopItemGUI = new ShopItemGUI(this);
    }


    // Methods
    public void setName(String n) { name = n; }

    public void setDisplayItem(ItemStack i) { displayItem = i; }

    public void setEnabled(boolean e) { enabled = e; }

    public void setCategoryItems(List<ShopItem> itemList) { categoryItems = itemList; }


    public boolean addShopItem(ShopItem i) {
        if(categoryItems.size() < 45) {
            categoryItems.add(i);
            shopItemGUI.generateShopItemGUI();
            return true;
        }
        return false;
    }

    public void deleteShopItem(ShopItem i) {
        categoryItems.remove(i);
        shopItemGUI.generateShopItemGUI();
    }


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
            if(item.getDisplayItem().getType() == itemStack.getType()) {
                if(item.getDisplayItem().getDurability() == itemStack.getDurability()) {
                    return item;
                }
            }
        }
        return null;
    }

    public ShopItemGUI getShopItemGUI() {
        return shopItemGUI;
    }

}
