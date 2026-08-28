package me.pan_truskawka045.Slither.leaderboard;

import me.pan_truskawka045.injector.Module;

public class LeaderboardModule extends Module {

    @Override
    public void init() {
        create(LeaderboardService.class);
    }
}
