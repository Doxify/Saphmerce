package org.saphron.saphmerce;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.guis.AdminGUI;
import org.saphron.saphmerce.guis.CategoryGUI;
import org.saphron.saphmerce.guis.ShopItemGUI;
import org.saphron.saphmerce.guis.TransactionGUI;
import org.saphron.saphmerce.utilities.ItemStackCreator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shop {

    Saphmerce plugin;
    private boolean enabled;
    private static DecimalFormat df = new DecimalFormat(".##");;
    private List<Category> shopCategories;
    public ItemStackCreator itemStackCreator = new ItemStackCreator();
    public CategoryGUI categoryGUI = new CategoryGUI(this);
    public ShopItemGUI shopItemGUI = new ShopItemGUI(this);
    public TransactionGUI transactionGUI = new TransactionGUI(this);
    public AdminGUI adminGUI = new AdminGUI(this);



    public Shop(Saphmerce p, List<Category> shopCategories) {
        plugin = p;
        if(shopCategories.size() > 0) {
            setShopCategories(shopCategories);
            setEnabled(true);
        } else {
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
        for(Category category : shopCategories) {
            if(category.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // BUY
    public void handleBuy(Player p, ShopItem transactionShopItem, int transactionItemAmount) {
        EconomyResponse econres = plugin.getNsa().getEcon().withdrawPlayer(p, (transactionItemAmount * transactionShopItem.getBuyPrice()));
        if(econres.transactionSuccess()) {
            if(transactionShopItem.isCommandItem()) {
                String command = transactionShopItem.getCommandString().replace("<p>", p.getDisplayName());

                for(int i = 0; i < transactionItemAmount; i++) {
                    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                }

                p.sendMessage(ChatColor.GREEN + "Successful purchase: " + transactionItemAmount + " x " + transactionShopItem.getName() + " for $" + df.format(econres.amount));
                p.updateInventory();

            } else {
                ItemStack buyItem = new ItemStack(transactionShopItem.getDisplayItem());
                buyItem.setAmount(transactionItemAmount);
                HashMap<Integer, ItemStack> leftOverItems = p.getInventory().addItem(buyItem);

                p.sendMessage(ChatColor.GREEN + "Successful purchase: " + transactionItemAmount + " x " + transactionShopItem.getName() + " for $" + df.format(econres.amount));
                p.updateInventory();

                if(leftOverItems.size() > 0) {
                    for(ItemStack item : leftOverItems.values()) {
                        p.getWorld().dropItem(p.getLocation(), item);
                    }
                    p.sendMessage(ChatColor.RED + "You did not have enough room in your inventory, check the ground for your item(s).");
                }
            }

        } else {
            p.sendMessage(ChatColor.RED + "Insufficient funds! You need at least $" + df.format(econres.amount));
        }
    }

    // SELL Functions
    public void handleSell(Player p, ShopItem transactionShopItem, int transactionItemAmount) {
        ItemStack sellItem = new ItemStack(transactionShopItem.getDisplayItem());
        if(hasEnoughItemsInInventory(p, sellItem, transactionItemAmount)) {
            removeItemFromInventory(p, sellItem, transactionItemAmount);

            EconomyResponse econres = plugin.getNsa().getEcon().depositPlayer(p, (transactionShopItem.getSellPrice() * transactionItemAmount));

            if(econres.transactionSuccess()) {
                p.sendMessage(ChatColor.GREEN + "Successful sale: " + transactionItemAmount + " x " + transactionShopItem.getName() + " for $" + df.format(econres.amount));
            } else {
                p.sendMessage(ChatColor.GREEN + "Transaction failed, contact an administrator.");
            }

        } else {
            p.sendMessage(ChatColor.RED + "You don't have " + transactionItemAmount + " x " + transactionShopItem.getName() + " in your inventory.");
        }
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

    // SELL ALL FUNCTIONS
    public void handleSellAll(Player p, ShopItem transactionShopItem) {
        ItemStack sellAllItem = new ItemStack(transactionShopItem.getDisplayItem());
        int itemsInInventory = getItemCountFromInventory(p, sellAllItem);

        if(itemsInInventory != 0) {
            removeItemFromInventory(p, sellAllItem, itemsInInventory);
            EconomyResponse econres = plugin.getNsa().getEcon().depositPlayer(p, (transactionShopItem.getSellPrice() * itemsInInventory));

            if(econres.transactionSuccess()) {
                p.sendMessage(ChatColor.GREEN + "Successful sale: " + itemsInInventory + " x " + transactionShopItem.getName() + " for $" + df.format(econres.amount));
            } else {
                p.sendMessage(ChatColor.GREEN + "Transaction failed, contact an administrator.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have any " + transactionShopItem.getName() + "(s) in your inventory.");
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
}
