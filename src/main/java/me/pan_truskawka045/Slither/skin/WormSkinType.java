package me.pan_truskawka045.Slither.skin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public enum WormSkinType {

    CHERRY(Component.text("Cherry").color(TextColor.color(0x8F0F10)), () -> new StaticWormSkin(SkinPart.CHERRY)),
    ORANGE(Component.text("Orange").color(TextColor.color(0xE86B00)), () -> new StaticWormSkin(SkinPart.ORANGE)),
    LEMON(Component.text("Lemon").color(TextColor.color(0xE09500)), () -> new StaticWormSkin(SkinPart.LEMON)),
    LIME(Component.text("Lime").color(TextColor.color(0x24C95E)), () -> new StaticWormSkin(SkinPart.LIME)),
    APPLE(Component.text("Apple").color(TextColor.color(0x148931)), () -> new StaticWormSkin(SkinPart.APPLE)),
    BUBBLEGUM(Component.text("Bubblegum").color(TextColor.color(0x0CBAE0)), () -> new StaticWormSkin(SkinPart.BUBBLEGUM)),
    PUNCH(Component.text("Punch").color(TextColor.color(0x0E80CC)), () -> new StaticWormSkin(SkinPart.PUNCH)),
    BLUEBERRY(Component.text("Blueberry").color(TextColor.color(0x144689)), () -> new StaticWormSkin(SkinPart.BLUEBERRY)),
    WATERMELON(Component.text("Watermelon").color(TextColor.color(0xCA109C)), () -> new StaticWormSkin(SkinPart.WATERMELON)),
    GRAPE(Component.text("Grape").color(TextColor.color(0x630D8F)), () -> new StaticWormSkin(SkinPart.GRAPE)),
    MULBERRY(Component.text("Mulberry").color(TextColor.color(0x9302C0)), () -> new StaticWormSkin(SkinPart.MULBERRY)),
    PLUM(Component.text("Plum").color(TextColor.color(0x55001E)), () -> new StaticWormSkin(SkinPart.PLUM)),

    LICORICE(Component.text("Licorice").color(TextColor.color(0x1A1F33)), () -> new StaticWormSkin(SkinPart.LICORICE)),
    CHOCOLATE(Component.text("Chocolate").color(TextColor.color(0x552619)), () -> new StaticWormSkin(SkinPart.CHOCOLATE)),
    MILK(Component.text("Milk").color(TextColor.color(0xD0CAB7)), () -> new StaticWormSkin(SkinPart.MILK));


    @Getter
    private final Component name;
    private final Supplier<AbstractWormSkin> factory;

    public AbstractWormSkin create() {
        return factory.get();
    }

}
