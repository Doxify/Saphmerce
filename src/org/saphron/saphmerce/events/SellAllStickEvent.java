package org.saphron.saphmerce.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.saphron.saphmerce.Profile;
import org.saphron.saphmerce.Saphmerce;
import org.saphron.saphmerce.Shop;

import java.util.List;

public class SellAllStickEvent implements Listener {

    Saphmerce plugin;
    Shop shop;

    public SellAllStickEvent(Saphmerce p) {
        plugin = p;
        shop = plugin.getShop();
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSellAllStickUse(PlayerInteractEvent e) {
        if(!e.isCancelled()) {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Player p = e.getPlayer();
                if(e.getClickedBlock().getState() instanceof Chest) {
                    if(p.getItemInHand().getType() == Material.STICK) {
                        Profile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
                        // First we check if it's an unlimited sell all stick
                        if(validate(p.getItemInHand())) {
                            if(!profile.hasSellCooldown()) {
                                handleSellAllStick(p, e);
                                return;
                            } else {
                                p.sendMessage(ChatColor.RED + "You can only use the /sell command once every " + plugin.getApi().COOLDOWN + " seconds.");
                                return;
                            }
                            // Then we check if its a temporary sell all stick
                        } else {
                            if(p.getItemInHand().hasItemMeta()) {
                                ItemStack tempSellAllStick = plugin.getApi().getTempSellAllStick();
                                if(p.getItemInHand().getItemMeta().hasDisplayName() && p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(tempSellAllStick.getItemMeta().getDisplayName())) {
                                    ItemStack itemInHand = p.getItemInHand();
                                    ItemMeta itemMeta = itemInHand.getItemMeta();

                                    if(itemMeta.hasLore()) {
                                        List<String> lore = itemMeta.getLore();
                                        try {
                                            int uses = Integer.parseInt(ChatColor.stripColor(lore.get(0)).split(":")[1].trim());

                                            if(uses > 0) {
                                                if(!profile.hasSellCooldown()) {
                                                    e.setCancelled(true);
                                                    handleSellAllStick(p, e);
                                                    uses--;
                                                    lore.set(0, ChatColor.GRAY + "Uses: " + ChatColor.GREEN + (uses));
                                                    itemMeta.setLore(lore);
                                                    itemInHand.setItemMeta(itemMeta);
                                                    p.updateInventory();
                                                }  else {
                                                    p.sendMessage(ChatColor.RED + "You can only use the /sell command once every " + plugin.getApi().COOLDOWN + " seconds.");
                                                    return;
                                                }
                                            } else {
                                                e.setCancelled(true);
                                                p.sendMessage(ChatColor.RED + "This sell all stick has ran out of uses!");
                                            }
                                        } catch (NumberFormatException err) {
                                            err.printStackTrace();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean validate(ItemStack sellAllStickItem) {
        ItemStack sellAllStick = plugin.getApi().getSellAllStick();
        if(sellAllStick.isSimilar(sellAllStickItem)) {
            return true;
        }
        return false;
    }

    private void handleSellAllStick(Player p, PlayerInteractEvent e) {
        Profile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
        Chest clickedChest = (Chest) e.getClickedBlock().getState();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if(!plugin.getApi().handleSellAllInventory(p, clickedChest.getInventory())) {
                    p.sendMessage(ChatColor.RED + "Couldn't find any items for sale in this chest.");
                }
                plugin.getApi().handleSellCooldown(profile);
            }
        });
    }

}
