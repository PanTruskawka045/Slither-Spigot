package me.pan_truskawka045.Slither.listener;

import me.pan_truskawka045.injector.Module;

public class ListenerModule extends Module {

    @Override
    public void init() {
        create(PlayerListener.class);
    }
}
