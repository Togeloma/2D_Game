package Entity;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    GamePanel gp;
    public int worldX, worldY;
    public int speed;
    public BufferedImage up1, down1, left1, right1, up2, down2, left2, right2;
    public String direction;
    public int spriteCounter =0;
    public int spriteNum = 1;
    public Rectangle solidArea;
    public boolean collisionOn = false;

    public Entity(GamePanel gp){
        this.gp=gp;
    }
}
