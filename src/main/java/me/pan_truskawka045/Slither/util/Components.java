package me.pan_truskawka045.Slither.util;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Components {

    public static final Component JOIN_GAME = Component.text("Join Game").decorate(TextDecoration.BOLD);
    public static final Component JOIN_GAME_HOVER = Component.text("Join Game").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED);

}
