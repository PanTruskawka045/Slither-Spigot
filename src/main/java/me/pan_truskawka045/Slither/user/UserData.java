package me.pan_truskawka045.Slither.user;

import lombok.Data;
import me.pan_truskawka045.Slither.skin.WormSkinType;

@Data
public class UserData {

    private long bestScore = -1;
    private long bestTimeLived = -1;
    private WormSkinType selectedSkin = WormSkinType.CHERRY;

}
