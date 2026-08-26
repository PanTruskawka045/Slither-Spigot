package me.pan_truskawka045.Slither.util;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Components {

    public static final Component JOIN_GAME = Component.text("Join Game").decorate(TextDecoration.BOLD);
    public static final Component JOIN_GAME_HOVER = Component.text("Join Game").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED);

    public static final Component SWITCH_SKIN_LEFT = Component.text("<<").decorate(TextDecoration.BOLD);
    public static final Component SWITCH_SKIN_RIGHT = Component.text(">>").decorate(TextDecoration.BOLD);

    public static final Component SWITCH_SKIN_LEFT_HOVER = Component.text("<<").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED);
    public static final Component SWITCH_SKIN_RIGHT_HOVER = Component.text(">>").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED);


}
