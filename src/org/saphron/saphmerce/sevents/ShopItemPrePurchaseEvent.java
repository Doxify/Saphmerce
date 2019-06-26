package org.saphron.saphmerce.sevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.saphron.saphmerce.Category;
import org.saphron.saphmerce.ShopItem;

public class ShopItemPrePurchaseEvent extends Event implements Cancellable {

    private Player player;
    private Category category;
    private ShopItem shopItem;
    private boolean isCanceled;

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }


    public boolean isCancelled() {
        return this.isCanceled;
    }

    public void setCancelled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public ShopItemPrePurchaseEvent(Player player, Category category, ShopItem shopItem) {
        this.player = player;
        this.category = category;
        this.shopItem =  shopItem;
        this.isCanceled = false;
    }

    public Player getPlayer() { return this.player; }

    public Category getCategory() { return this.category; }

    public ShopItem getShopItem() { return this.shopItem; }

}
