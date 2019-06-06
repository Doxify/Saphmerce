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


    public API() {
        plugin = Saphmerce.getPlugin();
    }

    /**
     *
     * @param uuid
     * @return Saphmerce#Profile
     */
    public static Profile getProfile(UUID uuid) {
        return plugin.getProfileManager().getProfile(uuid);
    }

    /**
     * Handles the purchase of a ShopItem
     *
     * @param p - Bukkit Player Object
     * @param transactionShopItem - ShopItem
     * @param transactionItemAmount - integer, quantity of purchase
     */
    public static void handleBuy(Player p, ShopItem transactionShopItem, int transactionItemAmount) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    /**
     *
     * @return Sell All ItemStack
     */
    public static ItemStack getSellAllStick() {
        // DO NOT CHANGE NAME OR LORE, IT WILL BREAK ALL PREVIOUS SELL ALL STICKS SPAWNED.
        ItemStack sellAllStick = ItemStackCreator.createItemStack(
                Material.STICK,
                ChatColor.LIGHT_PURPLE + "Sell All Stick " + ChatColor.GREEN + ChatColor.BOLD.toString() + "UNLIMITED",
                Arrays.asList(ChatColor.YELLOW + "Right click a chest to use.")
        );

        return sellAllStick;
    }

    /**
     *
     * @return Temporary Sell All ItemStack
     */
    public static ItemStack getTempSellAllStick() {
        // DO NOT CHANGE NAME OR LORE, IT WILL BREAK ALL PREVIOUS SELL ALL STICKS SPAWNED.
        ItemStack tempSellAllStick = ItemStackCreator.createItemStack(
                Material.STICK,
                ChatColor.LIGHT_PURPLE + "Sell All Stick " + ChatColor.RED + ChatColor.BOLD.toString() + "LIMITED",
                Arrays.asList(ChatColor.GRAY + "Uses: " + ChatColor.GREEN + 150, "", ChatColor.YELLOW + "Right click a chest to use.")
        );

        return tempSellAllStick;
    }

    /**
     *
     * @param p player object
     * @param transactionShopItem ShopItem
     * @param transactionItemAmount amount to sell
     */
    public void handleSell(Player p, ShopItem transactionShopItem, int transactionItemAmount) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                boolean hasMultiplier = p.hasPermission("saphmerce.multiplier");
                ItemStack sellItem = new ItemStack(transactionShopItem.getDisplayItem());
                if(plugin.getShop().hasEnoughItemsInInventory(p, sellItem, transactionItemAmount)) {
                    EconomyResponse econres;
                    plugin.getShop().removeItemFromInventory(p, sellItem, transactionItemAmount);

                    if(hasMultiplier) {
                        econres = plugin.getEcon().depositPlayer(p, (transactionShopItem.getSellPrice() * transactionItemAmount * plugin.getShop().getMultiplier()));
                    } else {
                        econres = plugin.getEcon().depositPlayer(p, (transactionShopItem.getSellPrice() * transactionItemAmount));
                    }

                    if(econres.transactionSuccess()) {
                        p.sendMessage(ChatColor.GREEN + "Successful sale: " + transactionItemAmount + " x " + transactionShopItem.getName() + " for " + Utilities.moneyFormat.format(econres.amount) + (hasMultiplier ? ChatColor.AQUA + " [Multiplier]" : ""));
                    } else {
                        p.sendMessage(ChatColor.GREEN + "Transaction failed, contact an administrator.");
                    }

                } else {
                    p.sendMessage(ChatColor.RED + "You don't have " + transactionItemAmount + " x " + transactionShopItem.getName() + " in your inventory.");
                }
            }
        });
    }

    /**
     *
     * @param p player object
     * @param transactionShopItem ShopItem
     */
    public void handleSellAll(Player p, ShopItem transactionShopItem) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                boolean hasMultiplier = p.hasPermission("saphmerce.multiplier");
                ItemStack sellAllItem = new ItemStack(transactionShopItem.getDisplayItem());
                int itemsInInventory = plugin.getShop().getItemCountFromInventory(p, sellAllItem);

                if(itemsInInventory != 0) {
                    EconomyResponse econres;
                    plugin.getShop().removeItemFromInventory(p, sellAllItem, itemsInInventory);


                    // Handling multiplier permission
                    if(hasMultiplier) {
                        econres = plugin.getEcon().depositPlayer(p, (transactionShopItem.getSellPrice() * itemsInInventory * plugin.getShop().getMultiplier()));
                    } else {
                        econres = plugin.getEcon().depositPlayer(p, (transactionShopItem.getSellPrice() * itemsInInventory));
                    }

                    if(econres.transactionSuccess()) {
                        p.sendMessage(ChatColor.GREEN + "Successful sale: " + itemsInInventory + " x " + transactionShopItem.getName() + " for " + Utilities.moneyFormat.format(econres.amount) + (hasMultiplier ? ChatColor.AQUA + " [Multiplier]" : ""));
                    } else {
                        p.sendMessage(ChatColor.GREEN + "Transaction failed, contact an administrator.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "You don't have any " + transactionShopItem.getName() + "(s) in your inventory.");
                }
            }
        });
    }

    /**
     *
     * @param p player object
     * @param inventory player's inventory
     * @return the amount sold as a double
     */
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
            if (plugin.getShop().getMultiplier() > 0) {
                econres = plugin.getEcon().depositPlayer(p, soldItemsPrice * plugin.getShop().getMultiplier());
                p.sendMessage(ChatColor.GREEN + "Successfully sold " + soldItemsAmount + (soldItemsAmount > 1 ? " items " : " item ") + "for " + Utilities.moneyFormat.format(econres.amount) + ChatColor.AQUA + " [Multiplier]");
            }
            return soldItemsPrice * plugin.getShop().getMultiplier();
        } else {
            return 0;
        }
    }

    /**
     *
     * @param p player object
     * @param inventory player's inventory
     * @return whether or not anything was sold
     */
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
            // Handling multiplier permission
            if(p.hasPermission("saphmerce.multiplier")) {
                econres = plugin.getEcon().depositPlayer(p, soldItemsPrice * plugin.getShop().getMultiplier());
                p.sendMessage(ChatColor.GREEN + "Successfully sold " + soldItemsAmount + (soldItemsAmount > 1 ? " items " : " item ") + "for " + Utilities.moneyFormat.format(econres.amount) + ChatColor.AQUA + " [Multiplier]");
                return soldItems;
            }

            econres = plugin.getEcon().depositPlayer(p, soldItemsPrice);
            p.sendMessage(ChatColor.GREEN + "Successfully sold " + soldItemsAmount  + (soldItemsAmount > 1 ? " items " : " item ") + "for " + Utilities.moneyFormat.format(econres.amount));
        }

        return soldItems;
    }

    /**
     *
     * @param p offlineplayer object
     * @param inventory inventory to sell items from
     * @return the price of the sale
     */
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
            // Handling multiplier permission
            if(plugin.getPerms().playerHas(null, p, "saphmerce.multiplier")) {
                plugin.getEcon().depositPlayer(p, soldItemsPrice * plugin.getShop().getMultiplier());
                return soldItemsPrice;
            }

            plugin.getEcon().depositPlayer(p, soldItemsPrice);
        }

        return soldItemsPrice;
    }

    /**
     *
     * @param p offlineplayer object
     * @param inventories inventories to sell items from
     */
    public static void handleGeneratorAutoSell(OfflinePlayer p, Collection<Inventory> inventories) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                boolean soldItems = false;
                int soldItemsAmount = 0;
                double soldItemsPrice = 0;
                boolean hasMultiplier = plugin.getPerms().playerHas(null, p, "saphmerce.multiplier");

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
                    if(hasMultiplier) {
                        econres = plugin.getEcon().depositPlayer(p, soldItemsPrice * plugin.getShop().getMultiplier());
                    } else {
                        econres = plugin.getEcon().depositPlayer(p, soldItemsPrice);
                    }

                    if(p.isOnline()) {
                        Player player = p.getPlayer();
                        player.sendMessage(GENERATOR_PREFIX + ChatColor.GREEN + "Successfully sold " + soldItemsAmount  + (soldItemsAmount > 1 ? " items " : " item ") + "for " + Utilities.moneyFormat.format(econres.amount) + (hasMultiplier ? ChatColor.AQUA + " [Multiplier]" : ""));
                    }
                }
            }
        });
    }

    /**
     *
     * @param profile saphmerce profile
     */
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
