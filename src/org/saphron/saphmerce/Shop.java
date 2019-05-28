package org.saphron.saphmerce;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.guis.CategoryGUI;
import org.saphron.saphmerce.guis.TransactionGUI;

import java.util.*;

public class Shop {

    Saphmerce plugin;
    private boolean enabled;
    private double multiplier = 1;
    private List<Category> shopCategories;
    private static final List<String> MINE_DROP_LORE = new ArrayList<>(Arrays.asList(ChatColor.LIGHT_PURPLE + "Mine Drop"));
    public CategoryGUI categoryGUI = new CategoryGUI(this);
    public TransactionGUI transactionGUI = new TransactionGUI(this);


    public Shop(Saphmerce p, List<Category> shopCategories) {
        plugin = p;
        multiplier = plugin.getMultiplier();
        if(shopCategories.size() > 0) {
            setShopCategories(shopCategories);
            setEnabled(true);
            categoryGUI.generateCategoryInterface();
        } else {
            setShopCategories(shopCategories);
            setEnabled(false);
        }
    }

    public Shop(Saphmerce p) {
        plugin = p;
        shopCategories = new ArrayList<>();
        setEnabled(false);
    }


    // Methods

    public void setShopCategories(List<Category> categories) {
        shopCategories = categories;
    }

    public void setEnabled(boolean e) { enabled = e; }

    public boolean addCategory(Category category) {
        if(!categoryExists(category.getName())) {
            shopCategories.add(category);
            if(!isEnabled()) {
                setEnabled(true);
            }
            categoryGUI.generateCategoryInterface();
            return true;
        }
        return false;
    }

    public boolean deleteCategory(String name) {
        Category category = getShopCategoryByName(name);
        if(category instanceof Category) {
            if(shopCategories.remove(category)) {
                if(shopCategories.size() == 0) {
                    setEnabled(false);
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public boolean isEnabled() { return enabled; }

    public List<Category> getShopCategories() { return shopCategories; }

    public Category getShopCategoryByName(String name) {
        for(Category category : shopCategories) {
            if(category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return null;
    }

    public boolean categoryExists(String name) {
        if(isEnabled()) {
            for(Category category : shopCategories) {
                if(category.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean hasEnoughItemsInInventory(Player p, ItemStack itemStack, int itemAmount) {
        int itemsInInventory = 0;

        for(ItemStack item : p.getInventory().getContents()) {
            if(item != null) {
                if(item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                    itemsInInventory += item.getAmount();
                }
            }
        }

        if(itemsInInventory >= itemAmount) {
            return true;
        }
        return false;

    }


    public void removeItemFromInventory(Player p, ItemStack itemStack, int itemAmount) {
        HashMap<Integer, ? extends ItemStack> inventoryList = p.getInventory().all(itemStack.getType());
        int itemsToRemove = itemAmount;

        for(Integer itemSlot : inventoryList.keySet()) {
            if(itemsToRemove > 0) {
                ItemStack item = inventoryList.get(itemSlot);
                if(item != null) {
                    if(item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                        // ItemStack amount is greater than itemsToRemove
                        if(item.getAmount() > itemsToRemove) {
                            item.setAmount(item.getAmount() - itemsToRemove);
                            p.updateInventory();
                            break;
                            // ItemStack amount is equal to itemsToRemove
                        } else if(item.getAmount() == itemsToRemove) {
                            p.getInventory().setItem(itemSlot, null);
                            p.updateInventory();
                            break;
                            // ItemStack amount is less than itemsToRemove
                        } else if(item.getAmount() < itemsToRemove) {
                            itemsToRemove -= item.getAmount();
                            p.getInventory().setItem(itemSlot, null);
                            p.updateInventory();
                        }
                    }
                }
            } else {
                break;
            }
        }
    }

    public int getItemCountFromInventory(Player p, ItemStack itemStack) {
        int count = 0;

        for(ItemStack item : p.getInventory().getContents()) {
            if(item != null) {
                if(item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                    count += item.getAmount();
                }
            }
        }

        return count;
    }

    public double getMultiplier() {
        return multiplier;
    }

    // Checking if an item is a mine drop
    public boolean isMineDrop(ItemStack item) {
        if(item.hasItemMeta()) {
            if(item.getItemMeta().hasLore()) {
                if(item.getItemMeta().getLore().equals(MINE_DROP_LORE)) {
                    return true;
                }
            }
        }

        return false;
    }
}
