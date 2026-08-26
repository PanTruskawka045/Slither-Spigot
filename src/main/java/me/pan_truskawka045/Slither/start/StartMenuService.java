package me.pan_truskawka045.Slither.start;

import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;
import me.pan_truskawka045.Slither.game.GameService;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import me.pan_truskawka045.Slither.start.menu.MenuButton;
import me.pan_truskawka045.Slither.start.menu.StartMenuView;
import me.pan_truskawka045.Slither.start.skin.SkinPreview;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.user.UserStorage;
import me.pan_truskawka045.Slither.util.Components;
import me.pan_truskawka045.Slither.util.SpigotUtil;
import me.pan_truskawka045.injector.Init;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

@RequiredArgsConstructor
public class StartMenuService {

    private final SpigotSlitherPlugin spigotSlitherPlugin;
    private final UserStorage userStorage;
    private final World world;
    private final GameService gameService;

    public void sendInitial(SlitherUser user) {
        StartMenuView startMenuView = new StartMenuView(user);

        MenuButton start = new MenuButton(user, new Location(world, 126.0, 115.5, 257.05), () -> {
            Display.TextDisplay display = new Display.TextDisplay(EntityType.TEXT_DISPLAY, SpigotUtil.getServerLevel(world));
            TextDisplay bukkitEntity = (TextDisplay) display.getBukkitEntity();
            bukkitEntity.setAlignment(TextDisplay.TextAlignment.CENTER);
            bukkitEntity.setShadowed(false);
            bukkitEntity.setDefaultBackground(false);
            bukkitEntity.setBillboard(org.bukkit.entity.Display.Billboard.FIXED);
            bukkitEntity.setTransformation(getScaledTransformation(1.75f));

            return display;
        }, 2.6, 0.35);

        start.setText(Components.JOIN_GAME);

        start.onHover(() -> start.setText(Components.JOIN_GAME_HOVER));
        start.onRelease(() -> start.setText(Components.JOIN_GAME));
        start.onClick(() -> gameService.joinPlayer(user));

        startMenuView.addButton(start);

        user.setStartMenuView(startMenuView);
        setupSkinPrevie(startMenuView, user);

    }

    private void setupSkinPrevie(StartMenuView startMenuView, SlitherUser user) {
        SkinPreview skinPreview = new SkinPreview(user);
        startMenuView.setSkinPreview(skinPreview);
        skinPreview.spawn();

        startMenuView.setSkinType(WormSkinType.CHERRY); //TODO get from database

    }

    @Init
    private void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(spigotSlitherPlugin, () -> {
            for (SlitherUser onlineUser : userStorage.getOnlineUsers()) {
                StartMenuView startMenuView = onlineUser.getStartMenuView();
                if (startMenuView != null) {
                    startMenuView.tick();
                }
            }
        }, 1, 1);
    }

    private Transformation getScaledTransformation(float scale) {
        return new Transformation(
                new Vector3f(0, 0, 0),
                new AxisAngle4f(0, 0, 0, 0),
                new Vector3f(scale, scale, scale),
                new AxisAngle4f(0, 0, 0, 1)
        );
    }

}
