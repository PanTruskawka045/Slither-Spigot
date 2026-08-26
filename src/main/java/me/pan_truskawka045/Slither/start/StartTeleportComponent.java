package me.pan_truskawka045.Slither.start;

import me.pan_truskawka045.Slither.user.UserService;
import me.pan_truskawka045.injector.Init;
import org.bukkit.Location;
import org.bukkit.World;

public class StartTeleportComponent {

    private final UserService userService;
    private final StartMenuService startMenuService;

    private final Location spawnLocation;

    public StartTeleportComponent(UserService userService, World world, StartMenuService startMenuService) {
        this.userService = userService;
        this.spawnLocation = new Location(world, 128.0, 115, 265.0, -180, 0);
        this.startMenuService = startMenuService;
    }

    @Init
    private void init() {
        userService.join(slitherUser -> {
            slitherUser.getPlayer().teleport(spawnLocation);
            startMenuService.sendInitial(slitherUser);
        });
    }

}
