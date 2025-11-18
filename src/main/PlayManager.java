package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PlayManager {

    // Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_X;
    public static int right_X;
    public static int top_Y;
    public static int bottom_Y;

    // Minos
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXT_MINO_X;
    final int NEXT_MINO_Y;
    private Random rng = new Random();
    private ArrayList<Integer> bag = new ArrayList<>();
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // others
    public static int dropInterval = 60; // mino drops in every 60 frames or 1 sec
    boolean GameOver;

    // Effects
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Score
    int level = 1;
    int lines;
    int score;

    public PlayManager() {

        // Main Play Area frame
        left_X = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_X = left_X + WIDTH;
        top_Y = 50;
        bottom_Y = top_Y + HEIGHT;

        MINO_START_X = left_X + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_Y + Block.SIZE;

        NEXT_MINO_X = right_X + 175;
        NEXT_MINO_Y = top_Y + 500;

        // set starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);

    }

    // randomness better
    private void refillBag() {
        bag.clear();
        for (int i = 0; i < 7; i++) bag.add(i);
        Collections.shuffle(bag, rng);
    }

    private Mino pickMino() {

        if (bag.isEmpty()) {
            refillBag();
        }

        int piece = bag.remove(0);

        switch (piece) {
            case 0: return new Mino_Bar();
            case 1: return new Mino_Square();
            case 2: return new Mino_T();
            case 3: return new Mino_L1();
            case 4: return new Mino_L2();
            case 5: return new Mino_Z1();
            case 6: return new Mino_Z2();
        }
        return null;
    }

    public void update(){
        // check if currentMino is active
        if(currentMino.active == false){
            // if mino isn't active put it into staticBlocks
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            // check if the game is over
            if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y){
                // meaning currentMino immediately collided with current Mino and couldn't move
                // and it's XY's are same as nextMino's
                GameOver = true;
            }

            currentMino.deactivating = false;

            // replace the currentMino with nextMino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXT_MINO_X, NEXT_MINO_Y);

            //when mino becomes inactive check if lines can be deleted
            checkDelete();
        }
        else {
            currentMino.update();
        }
    }

    private void checkDelete(){
        int x = left_X;
        int y = top_Y;
        int blockCount = 0;
        int lineCount = 0;

        while(x < right_X && y < bottom_Y){
            for(int i = 0; i < staticBlocks.size(); i++){
                if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y){
                    // increase count if there's static block
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if(x == right_X){
                // if blockCount hits 12, then current y line is filled with blocks
                // so it can be deleted
                if(blockCount == 12){
                    effectCounterOn = true;
                    effectY.add(y);

                    for(int i = staticBlocks.size()-1; i > -1; i--){
                        // remove all blocks in current y line
                        if(staticBlocks.get(i).y == y){
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;
                    // Drop Speed
                    // if line score hits certain level increase drop speed
                    // 1 is the fastest
                    if(lines % 10 == 10  && dropInterval > 1){
                        level++;

                        if(dropInterval > 10){
                            dropInterval -= 1;
                        }
                    }

                    //if line is needed blocks over it need to be pulled down
                    for(int i = 0; i < staticBlocks.size(); i++){
                        // move them down by block size
                        if(staticBlocks.get(i).y < y){
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }

                blockCount = 0;
                x = left_X;
                y += Block.SIZE;
            }
        }

        // add score
        if(lineCount > 0){
            int singelLineScore = 10 * level;
            score += singelLineScore + lineCount;
        }
    }

    public void draw(Graphics2D g2){
        // Draw play area frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_X-4, top_Y-4, WIDTH+8, HEIGHT+8);

        // Draw other Mino frame
        int x = right_X + 100;
        int y = bottom_Y - 200;
        g2.drawRect(x, y, 200 ,200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

        // draw Score frame
        g2.drawRect(x, top_Y, 200, 300);
        x += 20;
        y = top_Y + 90;
        g2.drawString("LVL: " + level, x, y); y += 70;
        g2.drawString("LNS: " + lines, x, y); y += 70;
        g2.drawString("SCR: " + score, x, y);

        // Draw Current Mino
        if(currentMino != null){
            currentMino.draw(g2);
        }
        // Draw the next Mino
        nextMino.draw(g2);

        // Draw staticBlocks
        for(int i = 0; i < staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2);
        }

        // Draw Effects
        if(effectCounterOn){
            effectCounter++;

            g2.setColor(Color.white); // effect color
            for(int i = 0; i < effectY.size(); i++){
                g2.fillRect(left_X, effectY.get(i), WIDTH, Block.SIZE);
            }

            if(effectCounter == 15){
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        // Draw Pause or Game Over menu
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(GameOver){
            x = left_X + 25;
            y = top_Y + 320;
            g2.drawString("GAME OVER", x, y);
        }
        else if(KeyHandler.pausePressed){
            x = left_X + 70;
            y = top_Y + 320;
            g2.drawString("PAUSED", x, y);
        }

        // Draw game title
        x = 35;
        y = top_Y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));
        g2.drawString("Simple Tetris", x+20, y);
        g2.drawString("Game", x+20, y+60);
    }
}
