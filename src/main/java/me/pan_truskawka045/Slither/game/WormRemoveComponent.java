package me.pan_truskawka045.Slither.game;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.user.UserService;
import me.pan_truskawka045.injector.Init;
import org.bukkit.entity.Entity;

@RequiredArgsConstructor
public class WormRemoveComponent {

    private final UserService userService;

    @Init
    private void init() {
        userService.quit(user -> {
            Entity vehicle = user.getPlayer().getVehicle();
            if (vehicle != null) {
                vehicle.remove();
            }
        });
    }

}
