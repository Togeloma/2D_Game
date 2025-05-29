package main;

import java.awt.*;
import java.text.DecimalFormat;

public class UI {

    GamePanel gp;
    Graphics2D g2;
    public boolean gameCompleted = false;
    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.000");

    public UI(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2){

        this.g2 = g2;

        if(gameCompleted==true){
            g2.setColor(Color.white);
            g2.setFont(new Font("Arial", Font.PLAIN, 30));

            String text;
            int textLength;
            int x;
            int y;

            text = "You have escaped the maze and became a better biology student!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 - (gp.tileSize*3);
            g2.drawString(text, x, y);

            text = "Your Time is: " + dFormat.format(playTime) + "seconds!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 + (gp.tileSize*4);
            g2.drawString(text, x, y);

            g2.setColor(Color.yellow);
            g2.setFont(new Font("Arial", Font.BOLD, 80));
            text = "Congratulations!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 + (gp.tileSize*2);
            g2.drawString(text, x, y);

            gp.gameThread= null;
        }
        else{
            g2.setColor(Color.white);
            g2.setFont(new Font("Arial", Font.PLAIN, 40));
            if(gp.gameState== gp.playState){
                playTime += (double) 1/60;
                g2.drawString("Time= "+ dFormat.format(playTime),gp.tileSize*11, 65 );
            }
            if (gp.gameState == gp.pauseState){
                drawPauseScreen();
            }

        }

    }
    public void drawPauseScreen(){
        String text= "PAUSED";
        int x;
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        x= gp.screenWidth/2 - length/2;
        int y = gp.screenHeight/2;
        g2.drawString(text, x, y);
    }
}
