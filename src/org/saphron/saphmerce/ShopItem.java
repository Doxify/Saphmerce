package org.saphron.saphmerce;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopItem implements RewardItem {

    private String name;
    private ItemStack displayItem;
    private double buyPrice;
    private double sellPrice;
    private boolean buyable;
    private boolean sellable;
    private boolean commandItem;
    private String commandString;

    public ShopItem(String name, ItemStack displayItem, double buyPrice, double sellPrice) {
        setName(name);
        setDisplayItem(displayItem);
        setBuyPrice(buyPrice);
        setSellPrice(sellPrice);
        commandItem = false;
        commandString = null;
    }

    public ShopItem(String name, ItemStack displayItem, double buyPrice, double sellPrice, boolean commandItem, String commandString) {
        setName(name);
        setDisplayItem(displayItem);
        setBuyPrice(buyPrice);
        setSellPrice(sellPrice);
        setCommandItem(commandItem);
        setItemCommand(commandString);
    }

    // Methods
    public void setName(String n) { name = n; }

    public void setDisplayItem(ItemStack i) { displayItem = i; displayItem.setAmount(1); }

    public void setBuyable(boolean b) { buyable = b; }

    public void setSellable(boolean s) { sellable = s; }

    public void setBuyPrice(double p) {
        buyPrice = p;
        if(p != -1) {
            setBuyable(true);
        } else {
            setBuyable(false);
        }
    }

    public void setSellPrice(double s) {
        sellPrice = s;
        if(s != -1) {
            setSellable(true);
        } else {
            setSellable(false);
        }
    }

    public void setCommandItem(boolean c) { commandItem = c; }

    public void setItemCommand(String c) { commandString = c; }


    @Override
    public String getName() { return name; }

    @Override
    public Material getMaterial() { return displayItem.getType(); }

    @Override
    public int getMaterialDurability() { return 0; }

    public ItemStack getDisplayItem() { return displayItem; }

    public double getBuyPrice() { return buyPrice; }

    public double getSellPrice() { return sellPrice; }

    public boolean isBuyable() { return buyable; }

    public boolean isSellable() { return sellable; }

    public boolean isCommandItem() { return commandItem; }

    public String getCommandString() { return commandString; }

    @Override
    public String getCommand(Player p) {
        String command = commandString;
        command.replace("<p>", p.getName());
        return command;
    }
}
