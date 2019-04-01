package mike.sucks.gameengine19.BreakoutGame;

import mike.sucks.gameengine19.GameEngine;
import mike.sucks.gameengine19.Screen;

public class Breakout extends GameEngine {
    @Override
    public Screen createStartScreen() {
        music = this.loadMusic("BreakoutAssets/music.ogg");
        return new MainMenuScreen(this);
    }

    public void onResume(){
        super.onResume();
        music.play();
    }

    public void onPause(){
        super.onPause();
        music.pause();
    }
}
