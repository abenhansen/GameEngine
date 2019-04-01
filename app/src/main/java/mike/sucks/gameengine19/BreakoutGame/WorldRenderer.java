package mike.sucks.gameengine19.BreakoutGame;

import android.graphics.Bitmap;

import mike.sucks.gameengine19.GameEngine;

public class WorldRenderer {
    GameEngine gameEngine;
    World world;
    Bitmap ballImage;
    Bitmap paddleImage;
    Bitmap blockImage;
    Block block;


    public WorldRenderer(GameEngine gameEngine, World world){
        this.gameEngine = gameEngine;
        this.world = world;
        ballImage = gameEngine.loadBitmap("BreakoutAssets/ball.png");
        paddleImage = gameEngine.loadBitmap("BreakoutAssets/paddle.png");
        blockImage = gameEngine.loadBitmap("BreakoutAssets/blocks.png");

    }
    public void render() {
        gameEngine.drawBitmap(ballImage, (int)world.ball.x, (int)world.ball.y);
        gameEngine.drawBitmap(paddleImage, (int)world.paddle.x, (int)world.paddle.y);
        for(int i = 0; i<world.blocks.size(); i++){
            block = world.blocks.get(i);
            gameEngine.drawBitmap(blockImage, (int)block.x, (int)block.y,
                    0, (int)(block.type* Block.HEIGHT),
                    (int) Block.WIDTH, (int) Block.HEIGHT);
        }
    }
}
