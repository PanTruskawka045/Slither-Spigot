package me.pan_truskawka045.Slither.skin;

import org.bukkit.inventory.ItemStack;

public abstract class AbstractWormSkin {

    public abstract ItemStack getHeadItem();

    public abstract ItemStack getBodyItem(int index);

    public abstract int getHeadColor();

    public abstract int getBodyColor(int index);

    public void tick() {

    }

}
