package org.saphron.saphmerce.serialization;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.saphron.saphmerce.Category;
import org.saphron.saphmerce.ShopItem;

import java.util.ArrayList;
import java.util.List;

public class ShopSerializer {

    public JSONObject categoryToJson(Category category) {
        JSONObject categoryObject = new JSONObject();
        JSONArray categoryItems = new JSONArray();
        categoryObject.put("name", category.getName());
        categoryObject.put("displayItem", category.getDisplayItem().getType().name());
        categoryObject.put("displayItemDurability", String.valueOf(category.getDisplayItem().getDurability()));
        categoryObject.put("allowedClasses", String.valueOf(category.getAllowedClasses()));

        for(int i = 0; i < category.getCategoryItems().size(); i++) {
            ShopItem item = category.getCategoryItems().get(i);
            JSONObject itemObject = new JSONObject();
            itemObject.put("name", item.getName());
            itemObject.put("displayItem", item.getDisplayItem().getType().name());
            itemObject.put("displayItemDurability", String.valueOf(item.getDisplayItem().getDurability()));
            itemObject.put("buyPrice", String.valueOf(item.getBuyPrice()));
            itemObject.put("sellPrice", String.valueOf(item.getSellPrice()));
            itemObject.put("isCommandItem", String.valueOf(item.isCommandItem()));
            itemObject.put("commandString", String.valueOf(item.getCommandString()));
            categoryItems.add(itemObject);
        }

        categoryObject.put("shopItems", categoryItems);

        return categoryObject;
    }

    public Category jsonToCategory(JSONObject categoryObject) {
        try {
            String name = (String) categoryObject.get("name");
            ItemStack displayItem = new ItemStack(Material.getMaterial((String) categoryObject.get("displayItem")));
            displayItem.setDurability(Short.parseShort((String) categoryObject.get("displayItemDurability")));
            int allowedClasses = Integer.valueOf((String) categoryObject.get("allowedClasses"));
            JSONArray shopItemsJson = (JSONArray) categoryObject.get("shopItems");
            List<ShopItem> shopItemsList = new ArrayList<>();

            for(Object shopItem : shopItemsJson) {
                JSONObject shopItemObject = (JSONObject) shopItem;
                String shopItemName = (String) shopItemObject.get("name");
                ItemStack shopDisplayItem = new ItemStack(Material.getMaterial((String) shopItemObject.get("displayItem")));
                shopDisplayItem.setDurability(Short.parseShort((String) shopItemObject.get("displayItemDurability")));
                double shopItemBuyPrice = Double.parseDouble((String) shopItemObject.get("buyPrice"));
                double shopItemSellPrice = Double.parseDouble((String) shopItemObject.get("sellPrice"));
                boolean shopItemIsCommandItem = Boolean.parseBoolean((String) shopItemObject.get("isCommandItem"));
                String shopItemCommandString = (String) shopItemObject.get("commandString");

                if(!shopItemIsCommandItem) {
                    shopItemsList.add(new ShopItem(shopItemName, shopDisplayItem, shopItemBuyPrice, shopItemSellPrice));
                } else {
                    shopItemsList.add(new ShopItem(shopItemName, shopDisplayItem, shopItemBuyPrice, shopItemSellPrice, shopItemIsCommandItem, shopItemCommandString));
                }

            }

            return new Category(name, displayItem, shopItemsList, allowedClasses);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

    }

}
