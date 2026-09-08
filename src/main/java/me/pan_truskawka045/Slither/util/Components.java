package me.pan_truskawka045.Slither.util;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Locale;

public class Components {

    public static final Component LEFT_SIDE_TEXT = Component.empty();

    public static final Component JOIN_GAME = Component.text("Join Game").decorate(TextDecoration.BOLD);
    public static final Component JOIN_GAME_HOVER = Component.text("Join Game").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED);

    public static final Component SWITCH_SKIN_LEFT = Component.text("<<").decorate(TextDecoration.BOLD);
    public static final Component SWITCH_SKIN_RIGHT = Component.text(">>").decorate(TextDecoration.BOLD);

    public static final Component SWITCH_SKIN_LEFT_HOVER = Component.text("<<").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED);
    public static final Component SWITCH_SKIN_RIGHT_HOVER = Component.text(">>").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED);

    public static final Component SKIN_NOT_SELECTED = Component.text(">").color(NamedTextColor.GRAY);
    public static final Component SKIN_SELECTED = Component.text(">").color(NamedTextColor.GREEN);

    public static Component elimination(String victim, String killer) {
        return Component.text("%arg0 was eliminated by %arg1").color(NamedTextColor.GRAY)
                .replaceText(builder -> builder.matchLiteral("%arg0").replacement(Component.text(victim).color(NamedTextColor.RED)))
                .replaceText(builder -> builder.matchLiteral("%arg1").replacement(Component.text(killer).color(NamedTextColor.RED)));
    }

    public static Component wallElimination(String victim) {
        return Component.text("%arg0 crashed into a wall").color(NamedTextColor.GRAY)
                .replaceText(builder -> builder.matchLiteral("%arg0").replacement(Component.text(victim).color(NamedTextColor.RED)));
    }

    public static Component wormPoints(int points) {
        String formattedPoints = String.format(Locale.ROOT, "%,d", points);
        return Component.text("Points: ").color(NamedTextColor.GRAY)
                .append(Component.text(formattedPoints).color(NamedTextColor.GREEN));
    }

    public static Component newBestScore(int points) {
        String formattedPoints = String.format(Locale.ROOT, "%,d", points);
        return Component.text("New points record: ").color(NamedTextColor.GOLD)
                .append(Component.text(formattedPoints).color(NamedTextColor.GREEN));
    }

    public static Component newBestTimeLived(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        return Component.text("New survival-time record: ").color(NamedTextColor.GOLD)
                .append(Component.text(String.format(Locale.ROOT, "%d:%02d", minutes, seconds % 60)).color(NamedTextColor.GREEN));
    }

}
