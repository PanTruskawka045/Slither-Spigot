package me.pan_truskawka045.Slither.user;

import me.pan_truskawka045.injector.Module;

public class UserModule extends Module {
    @Override
    public void init() {
        create(UserStorage.class);
        create(SlitherUserFactory.class);

        create(UserService.class);

        create(UserListener.class);

    }
}
