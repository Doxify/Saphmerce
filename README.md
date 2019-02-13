![alt text](https://i.imgur.com/qpFv2Ab.jpg "Saphron Logo")

# Saphmerce
Saphron's virtual shop plugin

## Tutorial

### Creating or deleting a category
/shopAdmin createCategory "name"

/shopAdmin deleteCategory "name"

### Adding items to a category
/shopAdmin addItem "category" "buy price" "sell price" "name"

`buy and sell price can be set to "-1" to disable buying or selling`

`"name" can be as long or as short as you want, no limitations.`

### Sell All Stick
Sell all sticks allow players to right click a chest and sell all the items that are for sale in that chest.

`/shopAdmin givesellallstick "player"`

### Admin GUI
The admin gui can be enabled by SHIFT clicking any item. All ops and players who have "saphmerce.admin" will be able to use this feature.

#### Turning an item into a command item
Command items are shop items that run a command when purchased instead of giving players the item displayed.
```sh
Format: es give <p> pig 1
```
`notice how there is no "/" and "<p>" will be replaced by the player's name`