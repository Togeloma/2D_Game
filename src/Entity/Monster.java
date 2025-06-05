package Entity;

import ai.Nodes;
import ai.Pathfinder;
import main.GamePanel;
import questions.Questions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Monster extends Entity {

    private Pathfinder pathfinder;

    // Cooldown fields
    private boolean pathCooldownActive = false;
    private long pathCooldownStartTime = 0L;
    private static final long PATH_COOLDOWN_DURATION = 10_000L; // 10 seconds

    // Tracks whether we've already triggered cooldown upon reaching the player tile
    private boolean reachedTarget = false;

    // Initial spawn delay: monster waits this long before moving
    private boolean initialDelayActive = true;
    private long spawnTime;
    int questionIndex =0;

    public Monster(GamePanel gp) {
        super(gp);
        pathfinder = new Pathfinder(gp);

        direction = "down";
        speed = 5;

        // Shrink + center the solidArea to 32×32 inside a 64×64 tile
        solidArea.x = gp.tileSize / 2 - 16;   // 16
        solidArea.y = gp.tileSize / 2 - 16;   // 16
        solidArea.width  = 32;
        solidArea.height = 32;

        getImage();

        spawnTime = System.currentTimeMillis();
    }

    public void getImage() {
        try {
            up1    = ImageIO.read(getClass().getResourceAsStream("/monster/orc_up_1.png"));
            up2    = ImageIO.read(getClass().getResourceAsStream("/monster/orc_up_2.png"));
            down1  = ImageIO.read(getClass().getResourceAsStream("/monster/orc_down_1.png"));
            down2  = ImageIO.read(getClass().getResourceAsStream("/monster/orc_down_2.png"));
            left1  = ImageIO.read(getClass().getResourceAsStream("/monster/orc_left_1.png"));
            left2  = ImageIO.read(getClass().getResourceAsStream("/monster/orc_left_2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/monster/orc_right_1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/monster/orc_right_2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disable pathfinding for 10 seconds, starting now.
     */
    public void disablePathingFor10Seconds() {
        pathCooldownActive = true;
        pathCooldownStartTime = System.currentTimeMillis();
        onPath = false;
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();

        // 1) Handle the initial 10-second delay after spawn
        if (initialDelayActive) {
            if (now - spawnTime >= 10_000L) {
                initialDelayActive = false;
                onPath = true; // start chasing after delay
            } else {
                // Still in initial delay: only animate sprite, skip movement/collision
                spriteCounter++;
                if (spriteCounter > 12) {
                    spriteNum = (spriteNum == 1 ? 2 : 1);
                    spriteCounter = 0;
                }
                return;
            }
        }

        // 2) Handle cooldown timing (when frozen on player tile)
        if (pathCooldownActive) {
            long elapsed = now - pathCooldownStartTime;
            if (elapsed >= PATH_COOLDOWN_DURATION) {
                // Cooldown finished: re-enable pathing
                pathCooldownActive = false;
                onPath = true;
            } else {
                // Still in cooldown: keep monster frozen in place
                onPath = false;

                // Animate sprite while frozen
                spriteCounter++;
                if (spriteCounter > 12) {
                    spriteNum = (spriteNum == 1 ? 2 : 1);
                    spriteCounter = 0;
                }
                return; // Skip further logic this frame
            }
        }

        // 3) Decide direction via pathfinding or random
        setAction();

        // 4) Attempt to move and handle collision/sliding
        boolean moved = false;
        String originalDirection = direction;

        // a) Check collision for intended direction
        collisionOn = false;
        gp.cChecker.checkTile(this);

        if (!collisionOn) {
            // No collision: move normally
            moveInDirection(direction);
            moved = true;
        } else {
            // b) Collision detected: attempt to slide along the wall
            if (originalDirection.equals("up") || originalDirection.equals("down")) {
                // Try sliding left
                direction = "left";
                collisionOn = false;
                gp.cChecker.checkTile(this);
                if (!collisionOn) {
                    moveInDirection("left");
                    moved = true;
                } else {
                    // Try sliding right
                    direction = "right";
                    collisionOn = false;
                    gp.cChecker.checkTile(this);
                    if (!collisionOn) {
                        moveInDirection("right");
                        moved = true;
                    }
                }
            }
            else { // originalDirection is "left" or "right"
                // Try sliding up
                direction = "up";
                collisionOn = false;
                gp.cChecker.checkTile(this);
                if (!collisionOn) {
                    moveInDirection("up");
                    moved = true;
                } else {
                    // Try sliding down
                    direction = "down";
                    collisionOn = false;
                    gp.cChecker.checkTile(this);
                    if (!collisionOn) {
                        moveInDirection("down");
                        moved = true;
                    }
                }
            }


            if (!moved) {
                direction = originalDirection;
                setAction(); // Recompute a new path/direction
                return;
            }
        }


        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1 ? 2 : 1);
            spriteCounter = 0;
        }

        // 6) After moving (or sliding), check if on the same tile as the player
        int monsterCol = (worldX + solidArea.x) / gp.tileSize;
        int monsterRow = (worldY + solidArea.y) / gp.tileSize;
        int playerCol  = (gp.player.worldX + gp.player.solidArea.x) / gp.tileSize;
        int playerRow  = (gp.player.worldY + gp.player.solidArea.y) / gp.tileSize;

        if (monsterCol == playerCol
                && monsterRow == playerRow
                && !pathCooldownActive
                && !reachedTarget) {

            // Monster caught player → pause game and ask question
            reachedTarget = true;
            gp.gameState = gp.pauseState;

            Questions[] allQuestions = gp.qLoader.getListOfQuestions();
            int total = gp.qLoader.getQuestionCount();

            // If no questions were loaded, simply resume play
            if (total == 0) {
                JOptionPane.showMessageDialog(null, "No questions loaded. You escape!");
                gp.gameState = gp.playState;
                disablePathingFor10Seconds();
                return;
            }

            // Wrap questionIndex if it has reached the end
            if (questionIndex >= total) {
                questionIndex = 0;
            }

            Questions q = allQuestions[questionIndex++];
            String userAnswer = JOptionPane.showInputDialog(
                    "Answer this question to survive:\n\n" + q.getPrompt()
            );

            if (userAnswer != null
                    && userAnswer.trim().equalsIgnoreCase(q.getAnswer().trim())) {
                JOptionPane.showMessageDialog(null, "Correct! You may continue.");
                gp.gameState = gp.playState;
                disablePathingFor10Seconds(); // monster waits again
            } else {
                JOptionPane.showMessageDialog(null, "Wrong! Game Over.");
                gp.gameState = gp.endingState;
            }
        } else if (monsterCol != playerCol || monsterRow != playerRow) {
            // Reset flag if monster leaves the player's tile
            reachedTarget = false;
        }
    }

    /**
     * Move the monster’s worldX/worldY a single step in the given direction.
     * This assumes collisionOn is already checked for that direction.
     */
    private void moveInDirection(String dir) {
        switch (dir) {
            case "up":    worldY -= speed; break;
            case "down":  worldY += speed; break;
            case "left":  worldX -= speed; break;
            case "right": worldX += speed; break;
        }
    }

    @Override
    public void setAction() {
        if (onPath) {
            // Monster's current tile
            int startCol = (worldX + solidArea.x) / gp.tileSize;
            int startRow = (worldY + solidArea.y) / gp.tileSize;

            // Player's current tile
            int goalCol = (gp.player.worldX + gp.player.solidArea.x) / gp.tileSize;
            int goalRow = (gp.player.worldY + gp.player.solidArea.y) / gp.tileSize;

            // Run A* pathfinding
            pathfinder.findPath(startCol, startRow, goalCol, goalRow);
            ArrayList<Nodes> newPath = pathfinder.getPathList();

            // If a path was found, step toward the next node
            if (newPath.size() > 1) {
                Nodes nextNode = newPath.get(1);
                int nextCol = nextNode.col;
                int nextRow = nextNode.row;

                if (nextCol < startCol)       direction = "left";
                else if (nextCol > startCol)  direction = "right";
                else if (nextRow < startRow)  direction = "up";
                else if (nextRow > startRow)  direction = "down";
            }
        } else {
            // Random movement when not chasing the player
            actionLockCounter++;
            if (actionLockCounter > 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;

                if (i <= 25)        direction = "up";
                else if (i <= 50)   direction = "down";
                else if (i <= 75)   direction = "left";
                else                direction = "right";

                actionLockCounter = 0;
            }
        }
    }

}