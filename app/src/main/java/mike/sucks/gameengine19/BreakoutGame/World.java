package mike.sucks.gameengine19.BreakoutGame;

import java.util.ArrayList;
import java.util.List;

public class World {
    public static float MIN_X = 0;
    public static float MAX_X = 319;

    public static float MIN_Y = 36;
    public static float MAX_Y = 479;

    Ball ball = new Ball();
    Paddle paddle = new Paddle();
    List<Block> blocks = new ArrayList<>();

    int lives = 3;
    boolean lostLife = false;
    boolean gameOver = false;
    int level = 1;
    int hits = 0;
    int points = 0;
    CollisionListener collisionListener;

    public World (CollisionListener collisionListener) {
        this.collisionListener = collisionListener;
        generateBlock();
    }

    public void update(float deltaTime, float accelX, boolean isTouch, int touchX){
        ball.x = ball.x + ball.vx * deltaTime;
        ball.y = ball.y + ball.vy * deltaTime;

//        paddle.x = ball.x; // Auto mode to test

        if (ball.x < MIN_X) {
            ball.vx = -ball.vx;
            ball.x = MIN_X;
            collisionListener.collisionWall();
        }
        if (ball.x > MAX_X- Ball.WIDTH){
            ball.vx = -ball.vx;
            ball.x = MAX_X - Ball.WIDTH;
            collisionListener.collisionWall();
        }
        if (ball.y  < MIN_Y){
            ball.vy = -ball.vy;
            ball.y = MIN_Y;
            collisionListener.collisionWall();
        }
        if (ball.y > MAX_Y) {
            lives = lives-1;
            lostLife = true;
            ball.x = paddle.x + paddle.WIDTH/2;
            ball.vy = -Ball.initialSpeed;
            ball.y = paddle.y - Ball.HEIGHT -5;
            if (lives == 0) gameOver = true;
            return;
        }

        paddle.x = paddle.x - accelX * 50 * deltaTime;

        // move paddle based on touch, only for testing in emulator!!
        if (isTouch){
            paddle.x = touchX - paddle.WIDTH/2;
        }

        if (paddle.x < MIN_X){
            paddle.x = MIN_X;
        }
        if (paddle.x + Paddle.WIDTH > MAX_X){
            paddle.x = MAX_X - Paddle.WIDTH;
        }
        collideBallPaddle(deltaTime);
        collideBallBlocks(deltaTime);


        if(blocks.size() == 0){
            generateBlock();
            level++;
            ball.x = 160;
            ball.y = 320-40;
            ball.vy = -Ball.initialSpeed * 1.3f;
            ball.vx = Ball.initialSpeed * 1.3f;
        }


    } // End of the update() method (AKA THE GAME WORLD)

    private void collideBallPaddle(float deltaTime) {
        //if (ball.y > paddle.y + Paddle.HEIGHT) return;
        if ((ball.x + Ball.WIDTH >= paddle.x) && (ball.x < paddle.x + Paddle.WIDTH) && (ball.y + Ball.HEIGHT > paddle.y)) {
            ball.y = ball.y - ball.vy * deltaTime * 1.01f;
            ball.vy = -ball.vy;
            collisionListener.collisionPaddle();
            hits++;
            if (hits == 5) {
                hits = 0;
                if(level ==2) {
                    advanceBlocks();
                }
            }
        }
    }

    private void collideBallBlocks(float deltatime) {
        Block block = null;
        for (int i=0; i< blocks.size(); i++) {
            block = blocks.get(i);
            if (collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT,
                    block.x,block.y, Block.WIDTH, Block.HEIGHT)) {
                blocks.remove(i);
                float oldvx = ball.vx;
                float oldvy = ball.vy;
                reflectBall(ball, block);
                // back out the ball with 1% to avoid multiple interatctions
                ball.x = ball.x - oldvx * deltatime * 1.01f;
                ball.y = ball.y - oldvy *deltatime * 1.01f;
                points = points + 10 - block.type;
                collisionListener.collisionBlocks();
                break; //no need to check collusion with other block when it hits the block
            }
        }
    }

    private void reflectBall(Ball ball, Block block){
        // cheff if ball hits the top left corner of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x,block.y, 1,1)){
            if (ball.vx > 0) ball.vx = -ball.vx;
            if (ball.vy >0) ball.vy = -ball.vy;
            return;
        }
        // check if ball hits the top right corner of the block
        if(collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x+ Block.WIDTH, block.y,1,1)){
            if (ball.vx <0) ball.vx = -ball.vx;
            if( ball.vy >0) ball.vy = -ball.vy;
            return;
        }
        //Check the bottem left corner of the brick
        if (collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT,block.x,
                block.y+ Block.HEIGHT, 1,1)){
            if (ball.vx >0) ball.vx = -ball.vx;
            if (ball.vy <0) ball.vy = -ball.vy;
            return;
        }
        // check the bottom right corner
        if (collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x+ Block.WIDTH, block.y+ Block.HEIGHT, 1,1)){
            if (ball.vx <0) ball.vx = -ball.vx;
            if (ball.vy <0) ball.vy = -ball.vy;
            return;
        }
        //check the top edge of the block
        if(collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x,block.y, Block.WIDTH,1)){
            if (ball.vy >0) ball.vy = -ball.vy;
            return;
        }
        //check the bottom edge of the block
        if(collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT,
              block.x,block.y+ Block.HEIGHT, Block.WIDTH,1  )){
            if(ball.vy < 0) ball.vy = -ball.vy;
            return;
        }
        //Check the left edge of the block
        if(collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT,
                block.x,block.y,1, Block.HEIGHT)){
            if(ball.vx >0) ball.vx = -ball.vx;
            return;
        }
        //Check the right edge of the block
        if(collideRects(ball.x,ball.y, Ball.WIDTH, Ball.HEIGHT,
               block.x+ Block.WIDTH,block.y,1, Block.HEIGHT )){
            if(ball.vx <0) ball.vx = -ball.vx;
            return;
        }

    }

        private boolean collideRects(float x, float y, float width, float height,
        float x2, float y2, float width2, float height2 ) {
            if(x < x2+width2 && x + width > x2 && y < y2+height2 && y+height >y2) {
                return true;
            }
            return false;
        }


    private void generateBlock(){
        blocks.clear();
        for(int y = 60, type = 0; y <60 + 8*(Block.HEIGHT+4); y=y+((int) Block.HEIGHT+4), type++){
            for (int x = 30; x<320 - Block.WIDTH; x=x + (int) Block.WIDTH+4){
                blocks.add(new Block(x,y,type));
            }
        }
//      ball.vy =  ball.vy *1.3f;
    }

    private void advanceBlocks(){
        Block block;
        int stop = blocks.size();
        for(int i = 0; i<stop; i++){
            block = blocks.get(i);
            block.y = block.y +10;
        }
    }

}
