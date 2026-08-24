package me.pan_truskawka045.Slither.user;

import org.bukkit.entity.Player;

public class SlitherUserFactory {

    public SlitherUser createUser(Player player) {
        return new SlitherUser(player);
    }

}
