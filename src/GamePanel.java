package main;

import Entity.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    //Screen Setting
    final int originalTileSize = 16; //16 x 16 tile
    final int scale = 4;
    final public int tileSize = originalTileSize * scale; // 64 x 64
    final public int maxScreenCol = 16;
    final public int maxScreenRow = 12;
    final public int screenWidth = tileSize * maxScreenCol; // 1024px
    final public int screenHeight = tileSize * maxScreenRow; // 768px

    //World data
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = maxWorldCol * tileSize;
    public final int worldHeight = maxWorldRow * tileSize;


    //FPS
    int FPS = 120;

    TileManager tileM = new TileManager(this);
    KeyInput keyH= new KeyInput(this);
    Thread gameThread;
    public UI ui = new UI(this);
    public CheckCollision cChecker = new CheckCollision(this);
    public Player player = new Player(this,keyH);

    //Game state
    public int gameState;
    public final int playState =1;
    public final int pauseState =2;


    //Constructor
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setUpGame(){
        gameState=playState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run(){

        double drawInterval = 1000000000 / (double) FPS;
        double nextDrawTime = System.nanoTime()+drawInterval;

        while (gameThread !=null){

            //Update the character's position
            update();
            //Draw the screen with the updated information
            repaint();

            try{
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime/1000000;
                if (remainingTime < 0){
                    remainingTime = 0;
                }
                Thread.sleep((long)remainingTime);
                nextDrawTime += drawInterval;
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    public void update() {
        if(gameState == playState){
            player.update();
        }
        if(gameState == pauseState){

        }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tileM.draw(g2);
        player.draw(g2);
        ui.draw(g2);
        g2.dispose();
    }

}
