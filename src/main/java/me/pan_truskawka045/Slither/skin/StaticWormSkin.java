package me.pan_truskawka045.Slither.skin;

import com.destroystokyo.paper.profile.PlayerProfile;
import lombok.RequiredArgsConstructor;
import me.pan_truskawka045.Slither.util.SpigotUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@RequiredArgsConstructor
public class StaticWormSkin extends AbstractWormSkin {

    private final SkinPart skinPart;

    @Override
    public ItemStack getHeadItem() {
        PlayerProfile playerProfile = SpigotUtil.getPlayerProfile(skinPart.getHead(), "");
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        itemStack.editMeta(SkullMeta.class, skullMeta -> {
            skullMeta.setPlayerProfile(playerProfile);
        });
        return itemStack;
    }

    @Override
    public ItemStack getBodyItem(int index) {
        PlayerProfile playerProfile = SpigotUtil.getPlayerProfile(skinPart.getBody(), "");
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        itemStack.editMeta(SkullMeta.class, skullMeta -> {
            skullMeta.setPlayerProfile(playerProfile);
        });
        return itemStack;
    }
}
