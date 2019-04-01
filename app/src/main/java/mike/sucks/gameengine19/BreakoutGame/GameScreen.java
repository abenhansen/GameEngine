package mike.sucks.gameengine19.BreakoutGame;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import java.util.List;

import mike.sucks.gameengine19.GameEngine;
import mike.sucks.gameengine19.Screen;
import mike.sucks.gameengine19.Sound;
import mike.sucks.gameengine19.TouchEvent;

public class GameScreen extends Screen {

    enum State {
        Paused,
        Running,
        GameOver
    }
    State state = State.Running;
    Bitmap background;
    Bitmap resume;
    Bitmap gameOver;
    Typeface font;
    Sound bounceSound;
    Sound blockSound;
    String showText ="dummy";
    World world;
    WorldRenderer renderer;

    public GameScreen(GameEngine gameEngine){
        super(gameEngine);
        background = gameEngine.loadBitmap("BreakoutAssets/background.png");
        resume = gameEngine.loadBitmap("BreakoutAssets/resume.png");
        gameOver = gameEngine.loadBitmap("BreakoutAssets/gameover.png");
        font = gameEngine.loadFont("BreakoutAssets/font.ttf");
        bounceSound = gameEngine.loadSound("BreakoutAssets/bounce.wav");
        blockSound = gameEngine.loadSound("BreakoutAssets/blocksplosion.wav");


        world = new World(new CollisionListener() {
            @Override
            public void collisionWall() {
                bounceSound.play(1);
            }

            @Override
            public void collisionPaddle() {
                bounceSound.play(1);
            }

            @Override
            public void collisionBlocks() {
                blockSound.play(1);
            }
        });
        renderer = new WorldRenderer(gameEngine, world);
    }


    @Override
    public void update(float deltaTime) {
        if (world.lostLife) {
            state = State.Paused;
            world.lostLife = false;
        }
        if (world.gameOver){
            state = State.GameOver;
        }
        if (state == State.Paused && gameEngine.isTouchDown(0)){
            state = State.Running;
            resume();
        }
        if (state == State.GameOver)  {  //&gameEngine.isTouchDown(0))
            List<TouchEvent> events = gameEngine.getTouchEvents();
            for (int i = 0; i<events.size(); i++) {
                if (events.get(i).type == TouchEvent.TouchEventType.Up){
                    gameEngine.setScreen(new MainMenuScreen(gameEngine));
                    return;
                }
            }

        }
        if (state == State.Running && gameEngine.getTouchY(0)< 30 && gameEngine.getTouchX(0)> 320-33) {
            state = State.Paused;
            pause();
            return;
        }
        gameEngine.drawBitmap(background,0,0);

        if (state == state.Running) {
            world.update(deltaTime, gameEngine.getAccelerometer()[0], gameEngine.isTouchDown(0), gameEngine.getTouchX(0));


//            if (world.ball.y > world.MAX_Y) {
//                state = State.GameOver;
//                return;
//            }
        }
        renderer.render();
        gameEngine.drawText(font,"Points: " + world.points +"         Lives: "+ world.lives,25,22, Color.GREEN,9);

        if (state == State.Paused){
            pause();
            gameEngine.drawBitmap(resume, 160- resume.getWidth()/2, 240- resume.getHeight()/2);
        }
        if (state == State.GameOver){
            pause();
            gameEngine.drawBitmap(gameOver, 160-gameOver.getWidth()/2, 240-gameOver.getHeight()/2);
        }


    }

    @Override
    public void pause() {
        gameEngine.music.pause();
        if (state == State.Running) state = State.Paused;


    }

    @Override
    public void resume() {
        gameEngine.music.play();
    }

    @Override
    public void dispose() {
//        gameEngine.music.pause();
//        gameEngine.music.stop();
        gameEngine.music.dispose();
    }
}
