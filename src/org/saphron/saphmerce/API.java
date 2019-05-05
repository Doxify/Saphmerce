package org.saphron.saphmerce;

import com.saphron.nsa.Utilities;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.saphron.saphmerce.utilities.ItemStackCreator;

import java.util.*;

/**
 * @author Andrei Georgescu
 * @version 0.0.1
 */
public class API {

    private static Saphmerce plugin;
    public static final int COOLDOWN = 10;
    public static final String SPOINTS_PREFIX = ChatColor.RED + "s" + ChatColor.BOLD.toString() + "POINTS " + ChatColor.GOLD + ChatColor.BOLD.toString() + "◗ ";
    public static final String MINE_PREFIX = ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "MINE " + ChatColor.GOLD + ChatColor.BOLD.toString() + "◗ ";
    public static final String GENERATOR_PREFIX = ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "GENERATOR " + ChatColor.GOLD + ChatColor.BOLD.toString() + "◗ ";


    /**
     * @param plugin - Saphmerce instance
     */
    public API(Saphmerce plugin) {
        this.plugin = plugin;
    }

    public static Profile getProfile(UUID uuid) {
        return plugin.getProfileManager().getProfile(uuid);
    }

    /**
     * Handles the purchase of a ShopItem
     *
     * @param p - Bukkit Player Object
     * @param transactionShopItem - ShopItem
     * @param transactionItemAmount - integer, quantity of purchase
     * @return Whether or not the function ran smoothly. If Saphmerce isn't loaded it returns false.
     */
    public static boolean handleBuy(Player p, ShopItem transactionShopItem, int transactionItemAmount) {
        if(plugin instanceof Saphmerce) {
            EconomyResponse econres = plugin.getEcon().withdrawPlayer(p, (transactionItemAmount * transactionShopItem.getBuyPrice()));
            if(econres.transactionSuccess()) {
                if(transactionShopItem.isCommandItem()) {
                    String command = transactionShopItem.getCommandString().replace("<p>", p.getDisplayName());

                    for(int i = 0; i < transactionItemAmount; i++) {
                        plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    }

                    p.sendMessage(ChatColor.GREEN + "Successful purchase: " + transactionItemAmount + " x " + transactionShopItem.getName() + " for " + Utilities.moneyFormat.format(econres.amount));
                    p.updateInventory();

                } else {
                    ItemStack buyItem = new ItemStack(transactionShopItem.getDisplayItem());
                    buyItem.setAmount(transactionItemAmount);
                    HashMap<Integer, ItemStack> leftOverItems = p.getInventory().addItem(buyItem);

                    p.sendMessage(ChatColor.GREEN + "Successful purchase: " + transactionItemAmount + " x " + transactionShopItem.getName() + " for " + Utilities.moneyFormat.format(econres.amount));
                    p.updateInventory();

                    if(leftOverItems.size() > 0) {
                        for(ItemStack item : leftOverItems.values()) {
                            p.getWorld().dropItem(p.getLocation(), item);
                        }
                        p.sendMessage(ChatColor.RED + "You did not have enough room in your inventory, check the ground for your item(s).");
                    }
                }
            } else {
                p.sendMessage(ChatColor.RED + "Insufficient funds! You need at least " + Utilities.moneyFormat.format(econres.amount));
            }
            return true;
        } else {
            return false;
        }
    }

    public static ItemStack getSellAllStick() {
        // DO NOT CHANGE NAME OR LORE, IT WILL BREAK ALL PREVIOUS SELL ALL STICKS SPAWNED.
        ItemStack sellAllStick = ItemStackCreator.createItemStack(
                Material.STICK,
                ChatColor.LIGHT_PURPLE + "Sell All Stick",
                Arrays.asList(ChatColor.YELLOW + "Right click a chest to use.")
        );

        return sellAllStick;
    }

    public void handleSell(Player p, ShopItem transactionShopItem, int transactionItemAmount) {
        ItemStack sellItem = new ItemStack(transactionShopItem.getDisplayItem());
        if(plugin.getShop().hasEnoughItemsInInventory(p, sellItem, transactionItemAmount)) {
            plugin.getShop().removeItemFromInventory(p, sellItem, transactionItemAmount);

            EconomyResponse econres = plugin.getEcon().depositPlayer(p, (transactionShopItem.getSellPrice() * transactionItemAmount));

            if(econres.transactionSuccess()) {
                p.sendMessage(ChatColor.GREEN + "Successful sale: " + transactionItemAmount + " x " + transactionShopItem.getName() + " for " + Utilities.moneyFormat.format(econres.amount));
            } else {
                p.sendMessage(ChatColor.GREEN + "Transaction failed, contact an administrator.");
            }

        } else {
            p.sendMessage(ChatColor.RED + "You don't have " + transactionItemAmount + " x " + transactionShopItem.getName() + " in your inventory.");
        }
    }

    public void handleSellAll(Player p, ShopItem transactionShopItem) {
        ItemStack sellAllItem = new ItemStack(transactionShopItem.getDisplayItem());
        int itemsInInventory = plugin.getShop().getItemCountFromInventory(p, sellAllItem);

        if(itemsInInventory != 0) {
            plugin.getShop().removeItemFromInventory(p, sellAllItem, itemsInInventory);
            EconomyResponse econres = plugin.getEcon().depositPlayer(p, (transactionShopItem.getSellPrice() * itemsInInventory));

            if(econres.transactionSuccess()) {
                p.sendMessage(ChatColor.GREEN + "Successful sale: " + itemsInInventory + " x " + transactionShopItem.getName() + " for " + Utilities.moneyFormat.format(econres.amount));
            } else {
                p.sendMessage(ChatColor.GREEN + "Transaction failed, contact an administrator.");
            }
        } else {
            p.sendMessage(ChatColor.RED + "You don't have any " + transactionShopItem.getName() + "(s) in your inventory.");
        }

    }

    public static double handleSellAllInventoryMineMode(Player p, Inventory inventory) {
        boolean soldItems = false;
        int soldItemsAmount = 0;
        double soldItemsPrice = 0;

        for (ItemStack invItem : inventory.getContents()) {
            if (invItem != null) {
                // Making sure that the item being sold is in fact a mine drop
                if (!plugin.getShop().isMineDrop(invItem)) {
                    continue;
                }

                for (Category category : plugin.getShop().getShopCategories()) {
                    ShopItem shopItem = category.getShopItemByItemStack(invItem);
                    if (shopItem != null) {
                        if (shopItem.isSellable()) {
                            soldItemsPrice += (shopItem.getSellPrice() * invItem.getAmount());
                            soldItemsAmount += invItem.getAmount();
                            inventory.removeItem(invItem);
                            soldItems = true;
                        }
                    }
                }
            }
        }

        if (soldItems) {
            EconomyResponse econres;
            if (plugin.getShop().getMineModeMultiplier() != 0) {
                econres = plugin.getEcon().depositPlayer(p, soldItemsPrice * plugin.getShop().getMineModeMultiplier());
                p.sendMessage(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "MINE " + ChatColor.GREEN + "Successfully sold " + soldItemsAmount + (soldItemsAmount > 1 ? " items " : " item ") + "for " + Utilities.moneyFormat.format(econres.amount));
            }
            return soldItemsPrice * plugin.getShop().getMineModeMultiplier();
        } else {
            return 0;
        }
    }

    public static boolean handleSellAllInventory(Player p, Inventory inventory) {
        boolean soldItems = false;
        int soldItemsAmount = 0;
        double soldItemsPrice = 0;

        for (ItemStack invItem : inventory.getContents()) {
            if(invItem != null) {
                for(Category category : plugin.getShop().getShopCategories()) {
                    ShopItem shopItem = category.getShopItemByItemStack(invItem);
                    if(shopItem != null) {
                        if(shopItem.isSellable()) {
                            soldItemsPrice += (shopItem.getSellPrice() * invItem.getAmount());
                            soldItemsAmount += invItem.getAmount();
                            inventory.removeItem(invItem);
                            soldItems = true;
                        }
                    }
                }
            }
        }

        if(soldItems) {
            EconomyResponse econres;
            econres = plugin.getEcon().depositPlayer(p, soldItemsPrice);
            p.sendMessage(ChatColor.GREEN + "Successfully sold " + soldItemsAmount  + (soldItemsAmount > 1 ? " items " : " item ") + "for " + Utilities.moneyFormat.format(econres.amount));
        }

        return soldItems;
    }

    public static double handleSellAllInventory(OfflinePlayer p, Inventory inventory) {
        boolean soldItems = false;
        double soldItemsPrice = 0;

        for (ItemStack invItem : inventory.getContents()) {
            if(invItem != null) {
                for(Category category : plugin.getShop().getShopCategories()) {
                    ShopItem shopItem = category.getShopItemByItemStack(invItem);
                    if(shopItem != null) {
                        if(shopItem.isSellable()) {
                            soldItemsPrice += (shopItem.getSellPrice() * invItem.getAmount());
                            inventory.removeItem(invItem);
                            soldItems = true;
                        }
                    }
                }
            }
        }

        if(soldItems) {
            plugin.getEcon().depositPlayer(p, soldItemsPrice);
        }

        return soldItemsPrice;
    }

    public static boolean handleGeneratorAutoSell(OfflinePlayer p, Collection<Inventory> inventories) {
        boolean soldItems = false;
        int soldItemsAmount = 0;
        double soldItemsPrice = 0;

        for(Inventory storage : inventories) {
            for (ItemStack invItem : storage.getContents()) {
                if(invItem != null) {
                    for(Category category : plugin.getShop().getShopCategories()) {
                        ShopItem shopItem = category.getShopItemByItemStack(invItem);
                        if(shopItem != null) {
                            if(shopItem.isSellable()) {
                                soldItemsPrice += (shopItem.getSellPrice() * invItem.getAmount());
                                soldItemsAmount += invItem.getAmount();
                                storage.removeItem(invItem);
                                soldItems = true;
                            }
                        }
                    }
                }
            }
        }

        if(soldItems) {
            EconomyResponse econres;
            econres = plugin.getEcon().depositPlayer(p, soldItemsPrice);
            if(p.isOnline()) {
                Player player = p.getPlayer();
                player.sendMessage(GENERATOR_PREFIX + ChatColor.GREEN + "Successfully sold " + soldItemsAmount  + (soldItemsAmount > 1 ? " items " : " item ") + "for " + Utilities.moneyFormat.format(econres.amount));
            }
        }

        return soldItems;
    }

    public static void handleSellCooldown(Profile profile) {
        profile.setSellColldown(true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                profile.setSellColldown(false);
            }
        }, COOLDOWN * 20);
    }




}
