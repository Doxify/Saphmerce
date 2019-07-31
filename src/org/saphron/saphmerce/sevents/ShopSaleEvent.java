package org.saphron.saphmerce.sevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShopSaleEvent extends Event {

    private Player player;
    private double saleAmount;

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

    public ShopSaleEvent(Player player, double saleAmount) {
        this.player = player;
        this.saleAmount = saleAmount;
    }

    public Player getPlayer() { return this.player; }

    public double getSaleAmount() { return this.saleAmount; }


}
