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
import org.saphron.saphmerce.Saphmerce;
import org.saphron.saphmerce.Shop;

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
                        if(validate(p.getItemInHand())) {
                            e.setCancelled(true);
                            Chest clickedChest = (Chest) e.getClickedBlock().getState();

                            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if(!shop.handleSellAllInventory(p, clickedChest.getInventory(), false)) {
                                        p.sendMessage(ChatColor.RED + "Couldn't find any items for sale in this chest.");
                                    }
                                }
                            });

                        }
                    }
                }
            }
        }
    }

    public boolean validate(ItemStack sellAllStickItem) {
        ItemStack sellAllStick = shop.getSellAllStick();
        if(sellAllStick.isSimilar(sellAllStickItem)) {
            return true;
        }
        return false;
    }

}
