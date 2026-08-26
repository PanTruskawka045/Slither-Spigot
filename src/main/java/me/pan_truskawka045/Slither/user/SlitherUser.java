package me.pan_truskawka045.Slither.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pan_truskawka045.Slither.start.menu.StartMenuView;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
@Setter
public class SlitherUser {

    private final Player player;

    private StartMenuView startMenuView;

}
