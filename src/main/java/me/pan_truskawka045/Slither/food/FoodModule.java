package me.pan_truskawka045.Slither.food;

import me.pan_truskawka045.injector.Module;

public class FoodModule extends Module {

    @Override
    public void init() {
        create(FoodStorage.class);
        create(FoodFactory.class);
    }

}
