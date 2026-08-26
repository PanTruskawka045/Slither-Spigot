package me.pan_truskawka045.Slither.skin;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public enum WormSkinType {

    CHERRY(() -> new StaticWormSkin(SkinPart.CHERRY)),
    PUNCH(() -> new StaticWormSkin(SkinPart.PUNCH)),
    WATERMELON(() -> new StaticWormSkin(SkinPart.WATERMELON)),
    ORANGE(() -> new StaticWormSkin(SkinPart.ORANGE)),
    LEMON(() -> new StaticWormSkin(SkinPart.LEMON)),
    LIME(() -> new StaticWormSkin(SkinPart.LIME)),
    APPLE(() -> new StaticWormSkin(SkinPart.APPLE)),
    BUBBLEGUM(() -> new StaticWormSkin(SkinPart.BUBBLEGUM)),
    BLUEBERRY(() -> new StaticWormSkin(SkinPart.BLUEBERRY)),
    GRAPE(() -> new StaticWormSkin(SkinPart.GRAPE)),
    MULBERRY(() -> new StaticWormSkin(SkinPart.MULBERRY)),
    PLUM(() -> new StaticWormSkin(SkinPart.PLUM)),
    LICORICE(() -> new StaticWormSkin(SkinPart.LICORICE)),
    CHOCOLATE(() -> new StaticWormSkin(SkinPart.CHOCOLATE)),
    MILK(() -> new StaticWormSkin(SkinPart.MILK));


    private final Supplier<AbstractWormSkin> factory;

    public AbstractWormSkin create() {
        return factory.get();
    }

}
