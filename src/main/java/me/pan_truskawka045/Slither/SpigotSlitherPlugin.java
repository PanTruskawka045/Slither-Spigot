package me.pan_truskawka045.Slither;

import me.pan_truskawka045.Slither.food.FoodModule;
import me.pan_truskawka045.Slither.game.GameModule;
import me.pan_truskawka045.Slither.start.StartMenuModule;
import me.pan_truskawka045.Slither.user.UserModule;
import me.pan_truskawka045.Slither.worm.WormModule;
import me.pan_truskawka045.injector.Bind;
import me.pan_truskawka045.injector.Injector;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Bind(JavaPlugin.class)
public class SpigotSlitherPlugin extends JavaPlugin {

    private final Injector injector = new Injector();

    @Override
    public void onEnable() {
        injector.register(this);
        injector.register(Bukkit.getWorlds().getFirst(), World.class);

        injector.registerModule(new UserModule());
        injector.registerModule(new FoodModule());
        injector.registerModule(new WormModule());
        injector.registerModule(new GameModule());
        injector.registerModule(new StartMenuModule());

        injector.injectAll();
        injector.initAll();
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
