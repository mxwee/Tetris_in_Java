package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager {

    // Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_X;
    public static int right_X;
    public static int top_Y;
    public static int bottom_Y;

    // Minoes
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> statricBlocks = new ArrayList<>();

    // others
    public static int dropInterval = 60; // mino drops in every 60 frames or 1 sec

    public PlayManager() {

        // Main Play Area frame
        left_X = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_X = left_X + WIDTH;
        top_Y = 50;
        bottom_Y = top_Y + HEIGHT;

        MINO_START_X = left_X + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_Y + Block.SIZE;

        NEXTMINO_X = right_X + 175;
        NEXTMINO_Y = top_Y + 500;

        // set starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

    }

    private Mino pickMino(){
        // Pick a random Mino
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch (i){
            case 0: mino = new Mino_Bar(); break;
            case 1: mino = new Mino_Square(); break;
            case 2: mino = new Mino_T(); break;
            case 3: mino = new Mino_L1(); break;
            case 4: mino = new Mino_L2(); break;
            case 5: mino = new Mino_Z1(); break;
            case 6: mino = new Mino_Z2(); break;
        }
        return mino;
    }

    public void update(){
        // check if currentMino is active
        if(currentMino.active == false){
            // if mino isnt active put it into staticBlocks
            statricBlocks.add(currentMino.b[0]);
            statricBlocks.add(currentMino.b[1]);
            statricBlocks.add(currentMino.b[2]);
            statricBlocks.add(currentMino.b[3]);

            // replace the currentMino with nextMino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
        }
        else {
            currentMino.update();
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

        // Draw Current Mino
        if(currentMino != null){
            currentMino.draw(g2);
        }
        // Draw the next Mino
        nextMino.draw(g2);

        // Draw staticBlocks
        for(int i = 0; i < statricBlocks.size(); i++){
            statricBlocks.get(i).draw(g2);
        }

        // Draw Pause menu
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if(KeyHandler.pausePressed){
            x = left_X + 70;
            y = top_Y + 320;
            g2.drawString("PAUSED", x, y);
        }
    }
}
