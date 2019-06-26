package org.saphron.saphmerce;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface RewardItem {

    String getName();
    Material getMaterial();
    int getMaterialDurability();
    String getCommand(Player p);

}
