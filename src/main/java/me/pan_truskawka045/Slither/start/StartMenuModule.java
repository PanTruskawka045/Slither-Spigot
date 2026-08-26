package me.pan_truskawka045.Slither.start;

import me.pan_truskawka045.injector.Module;
import me.pan_truskawka045.Slither.start.menu.MenuClickListener;

public class StartMenuModule extends Module {

    @Override
    public void init() {
        create(StartMenuService.class);

        create(StartTeleportComponent.class);
        create(MenuClickListener.class);
    }
}
