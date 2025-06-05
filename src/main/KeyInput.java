package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyInput implements KeyListener {

    GamePanel gp;

    public boolean upPressed, downPressed, leftPressed, rightPressed;

public KeyInput(GamePanel gp){
    this.gp = gp;
}
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        if(gp.gameState==gp.initialState){
            if (code == KeyEvent.VK_SPACE) {
                gp.gameState=gp.playState;
            }
        }else if (gp.gameState==gp.playState){
            if (code == KeyEvent.VK_W) {
                upPressed = true;
            }
            if (code == KeyEvent.VK_S) {
                downPressed = true;
            }
            if (code == KeyEvent.VK_A) {
                leftPressed = true;
            }
            if (code == KeyEvent.VK_D) {
                rightPressed = true;
            }
            if (code == KeyEvent.VK_SPACE) {
                    gp.gameState=gp.pauseState;
            }
        }else if(gp.gameState==gp.pauseState){
            if (code == KeyEvent.VK_SPACE) {
                gp.gameState=gp.playState;
            }
        }else if(gp.gameState==gp.endingState){
            if (code == KeyEvent.VK_SPACE) {
                gp.resetGame();
                gp.gameState=gp.initialState;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }

    }
}
