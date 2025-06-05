package main;

import Entity.Entity;
import Entity.Player;
import Entity.Monster;
import questions.Questions;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    // Screen Setting
    final int originalTileSize = 16; // 16 x 16 tile
    final int scale = 4;
    public final int tileSize = originalTileSize * scale; // 64 x 64
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 1024px
    public final int screenHeight = tileSize * maxScreenRow; // 768px

    // World data
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = maxWorldCol * tileSize;
    public final int worldHeight = maxWorldRow * tileSize;

    // FPS
    int FPS = 120;

    public TileManager tileM = new TileManager(this);
    KeyInput keyH = new KeyInput(this);
    Thread gameThread;
    public UI ui = new UI(this);
    public CheckCollision cChecker = new CheckCollision(this);

    public Player player = new Player(this, keyH);
    public Entity Monsters[] = new Entity[10];


    // ── Make sure qLoader is instantiated BEFORE we call loadQuestions:
    public Questions qLoader = new Questions(null, null);

    // Game state
    public int gameState;

    public final int initialState = 0;
    public final int playState = 1;
    public final int pauseState = 2;

    public final int endingState = 3;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Now qLoader is non-null. Load up to 100 questions from "/bio_mcq.txt":
        qLoader.loadQuestions("/listOfQuestions/bio_mcq.txt");

        // Print how many actually loaded (should be 100 if the file had 200 lines):
        System.out.println("Loaded questions: " + qLoader.getQuestionCount() + "/100");

        setUpGame();
    }



    public void setUpGame() {
        setMonster();
        gameState = initialState;
    }

    public void setMonster() {
        Monsters[0] = new Monster(this);
        // Place the monster at tile (21, 21):
        Monsters[0].worldX = tileSize * 21;
        Monsters[0].worldY = tileSize * 21;
    }

    public void resetGame(){
        Monsters[0].worldX = tileSize * 21;
        Monsters[0].worldY = tileSize * 21;
        Monster monster1= (Monster) Monsters[0];
        monster1.disablePathingFor10Seconds();
        player.setDefaultValues();

    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000 / (double) FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1_000_000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            player.update();
            for (int i = 0; i < Monsters.length; i++) {
                if (Monsters[i] != null) {
                    Monsters[i].update();
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1) Draw tile map
        tileM.draw(g2);

        // 2) Draw monsters (their sprites)
        for (int i = 0; i < Monsters.length; i++) {
            if (Monsters[i] != null) {
                Monsters[i].draw(g2);
            }
        }

        // 3) Draw player
        player.draw(g2);

        // 4) Draw UI
        ui.draw(g2);

        g2.dispose();
    }
}