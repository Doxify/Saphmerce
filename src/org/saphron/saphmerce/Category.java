package org.saphron.saphmerce;

import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.guis.ShopItemGUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Category {

    private String name;
    private ItemStack displayItem;
    private boolean enabled;
    private List<ShopItem> categoryItems;
    private ShopItemGUI shopItemGUI;
    private int allowedClasses;


    // Constructor when loading from file
    public Category(String name, ItemStack displayItem, List<ShopItem> categoryItems, int allowedClasses) {
        setName(name);
        setDisplayItem(displayItem);
        setCategoryItems(categoryItems);
        setEnabled(true);
        setAllowedClasses(allowedClasses);
        shopItemGUI = new ShopItemGUI(this);
    }

    // Default constructor
    public Category(String name, ItemStack displayItem) {
        setName(name);
        setDisplayItem(displayItem);
        setEnabled(false);
        setAllowedClasses(0);
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
            if(item.getMaterial() == itemStack.getType()) {
                if(item.getMaterialDurability() == itemStack.getDurability()) {
                    return item;
                }
            }
        }
        return null;
    }

    public ShopItemGUI getShopItemGUI() {
        return shopItemGUI;
    }

    public void setAllowedClasses(int allowedClasses) { this.allowedClasses = allowedClasses; }

    public int getAllowedClasses() { return allowedClasses; }

    public List<RewardItem> getScratcherRewards() {
        List<ShopItem> keys = new ArrayList<>(categoryItems);
        List<RewardItem> rewardList = new ArrayList<>();
        Random r = new Random();
        int itemsToAdd = 9;

        for(int i = 0; i < 9; i++) {
            if(itemsToAdd != 0) {
                ShopItem item = categoryItems.get(r.nextInt(keys.size()));
                rewardList.add(item);
                itemsToAdd--;
            } else {
                rewardList.add(null);
            }
        }

        Collections.shuffle(rewardList);

        return rewardList;
    }

}
