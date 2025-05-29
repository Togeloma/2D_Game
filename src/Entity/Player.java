package Entity;

import main.GamePanel;
import main.KeyInput;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends Entity {


    KeyInput keyH;

    public final int screenX;
    public final int screenY;

    public Player(GamePanel gp, KeyInput keyH) {
        super(gp);
        this.keyH = keyH;
        screenX = gp.screenWidth/2;
        screenY = gp.screenHeight/2;

        solidArea = new Rectangle(8, 16, 32, 32);

        setDefaultValues();
        getPlayerImage();
    }
    public void setDefaultValues(){
        worldX= gp.tileSize*23-(gp.tileSize-2);
        worldY= gp.tileSize*21-(gp.tileSize-2);
        speed = 4;
        direction = "down";
}
    public void getPlayerImage(){
        try{
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_up_2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_down_2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/boy_right_2.png"));

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void update() {

        if ((screenX > gp.worldWidth) || (screenY > gp.worldHeight) || (screenX < 0) || (screenY < 0)) {
            gp.ui.gameCompleted = true;
            return;
        }

            if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
                if (keyH.upPressed) {
                    direction = "up";
                } else if (keyH.downPressed) {
                    direction = "down";
                }
                if (keyH.leftPressed) {
                    direction = "left";
                } else if (keyH.rightPressed) {
                    direction = "right";
                }

                collisionOn = false;
                gp.cChecker.checkTile(this);

                if (collisionOn == false) {
                    switch (direction) {
                        case "up":
                            worldY -= speed;
                            break;
                        case "down":
                            worldY += speed;
                            break;
                        case "left":
                            worldX -= speed;
                            break;
                        case "right":
                            worldX += speed;
                            break;
                    }

                }

                spriteCounter++;
                if (spriteCounter > 12) {
                    if (spriteNum == 1) {
                        spriteNum = 2;
                    } else if (spriteNum == 2) {
                        spriteNum = 1;
                    }
                    spriteCounter = 0;
                }
            }

        }

        public void pickUpObject(int i) {

            if(i!=999){

            }

        }

    public void draw(Graphics2D g2){

        BufferedImage image = null;

        switch (direction){
            case "up":
                if(spriteNum==1){
                    image = up1;
                }
                if (spriteNum==2){
                    image = up2;
                }
                break;
                case "down":
                    if(spriteNum==1){
                        image = down1;
                    }
                    if (spriteNum==2){
                        image = down2;
                    }
                    break;
                    case "left":
                        if(spriteNum==1){
                            image = left1;
                        }
                        if (spriteNum==2){
                            image = left2;
                        }
                        break;
                        case "right":
                            if(spriteNum==1){
                                image = right1;
                            }
                            if (spriteNum==2) {
                                image = right2;
                            }
                            break;
        }
        g2.drawImage(image, screenX, screenY,gp.tileSize, gp.tileSize, null);
    }

}
