package me.pan_truskawka045.Slither.game;

import me.pan_truskawka045.injector.Module;

public class GameModule extends Module {


    @Override
    public void init() {
        create(GameService.class);
    }
}
