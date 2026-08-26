package me.pan_truskawka045.Slither.start.menu;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pan_truskawka045.Slither.skin.WormSkinType;
import me.pan_truskawka045.Slither.start.skin.SkinPreview;
import me.pan_truskawka045.Slither.user.SlitherUser;
import me.pan_truskawka045.Slither.util.SpigotUtil;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StartMenuView {

    private final List<MenuButton> displays = new ArrayList<>();

    private final SlitherUser user;

    @Getter
    private WormSkinType skinType = WormSkinType.CHERRY;

    @Setter
    private SkinPreview skinPreview;

    public void removeAll() {
        ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(displays.stream().mapToInt(menu -> menu.getDisplay().getId()).toArray());
        SpigotUtil.sendPacket(user, packet);
        skinPreview.remove();
    }

    public void tick() {
        displays.forEach(MenuButton::tick);
        this.skinPreview.tick();
    }

    public void click() {
        displays.forEach(MenuButton::click);
    }

    public void addButton(MenuButton start) {
        displays.add(start);
        start.spawn();
    }

    public void setSkinType(WormSkinType type) {
        this.skinType = type;
        this.skinPreview.applySkin(type);
    }

}
