package me.pan_truskawka045.Slither.food;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum FoodColor {

    RED(Blocks.RED_STAINED_GLASS, Blocks.RED_CONCRETE),
    ORANGE(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_CONCRETE),
    YELLOW(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_CONCRETE),
    LIME(Blocks.LIME_STAINED_GLASS, Blocks.LIME_CONCRETE),
    GREEN(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_CONCRETE),
    LIGHT_BLUE(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_CONCRETE),
    PINK(Blocks.PINK_STAINED_GLASS, Blocks.PINK_CONCRETE),
    PURPLE(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_CONCRETE);

    private final Block stainedGlass;
    private final Block innerBlock;

    FoodColor(Block stainedGlass, Block innerBlock) {
        this.stainedGlass = stainedGlass;
        this.innerBlock = innerBlock;
    }

    public Block getStainedGlass() {
        return this.stainedGlass;
    }

    public Block getInnerBlock() {
        return this.innerBlock;
    }

}
