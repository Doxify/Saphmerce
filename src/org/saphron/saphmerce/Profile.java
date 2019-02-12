package org.saphron.saphmerce;

public class Profile {

    Category clickedCategory = null;
    ShopItem clickedShopItem = null;

    public void setClickedCategory(Category category) {
        clickedCategory = category;
    }

    public void setClickedShopItem(ShopItem shopItem) {
        clickedShopItem = shopItem;
    }

    public void clearClickedItems() {
        clickedCategory = null;
        clickedShopItem = null;
    }

    public Category getClickedCategory() { return clickedCategory; }

    public ShopItem getClickedShopItem() { return clickedShopItem; }

}
