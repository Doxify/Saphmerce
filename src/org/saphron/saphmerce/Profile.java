package org.saphron.saphmerce;

public class Profile {

    private Category clickedCategory = null;
    private ShopItem clickedShopItem = null;
    private boolean sellColldown = false;

    public boolean hasSellCooldown() {
        return sellColldown;
    }

    public void setSellColldown(boolean sellColldown) {
        this.sellColldown = sellColldown;
    }

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
