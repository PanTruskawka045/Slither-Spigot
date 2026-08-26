package me.pan_truskawka045.Slither.worm;

import me.pan_truskawka045.injector.Module;

public class WormModule extends Module {
    @Override
    public void init() {
        create(WormFactory.class);
    }
}
