package ai;

import main.GamePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Pathfinder {
    GamePanel gp;
    Graphics2D g2;
    Nodes[][] node;
    ArrayList<Nodes> openList = new ArrayList<>();
    public ArrayList<Nodes> pathList = new ArrayList<>();
    Nodes startNode, goalNode, currentNode;
    boolean goalReached = false;

    public Pathfinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
    }

    // 1) Create a grid of Nodes once:
    public void instantiateNodes() {
        node = new Nodes[gp.maxWorldCol][gp.maxWorldRow];
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                node[col][row] = new Nodes(col, row);
            }
        }
    }

    // 2) Clear all node state from any previous search:
    public void resetNodes() {
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                Nodes n = node[col][row];
                n.open = false;
                n.checked = false;
                n.solid = false;
                n.gCost = 0;
                n.hCost = 0;
                n.fCost = 0;
                n.parent = null;
            }
        }
        openList.clear();
        pathList.clear();
        goalReached = false;
    }

    // 3) Mark solid tiles, set start/goal, and add start to openList
    public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {
        resetNodes();

        // Mark every tile that has collision = true as solid
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                int tileNum = gp.tileM.mapTileNum[col][row];
                if (gp.tileM.tile[tileNum].collision) {
                    node[col][row].solid = true;
                }
            }
        }

        // Identify start, goal, and begin the openList
        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];
        openList.add(currentNode);
        currentNode.open = true;
    }

    // 4) Run A* until you find the goal or exhaust openList
    public void findPath(int startCol, int startRow, int goalCol, int goalRow) {
        setNodes(startCol, startRow, goalCol, goalRow);

        while (!openList.isEmpty() && !goalReached) {
            // Sort openList by fCost, then hCost
            Collections.sort(openList, new Comparator<Nodes>() {
                @Override
                public int compare(Nodes n1, Nodes n2) {
                    if (n1.fCost == n2.fCost) {
                        return Integer.compare(n1.hCost, n2.hCost);
                    }
                    return Integer.compare(n1.fCost, n2.fCost);
                }
            });

            // Take lowest‐fCost node
            currentNode = openList.remove(0);
            currentNode.checked = true;

            // If we've reached goal, build the path
            if (currentNode == goalNode) {
                goalReached = true;
                buildPath();
                return;
            }

            // Otherwise, discover neighbors
            discoverNeighbors(currentNode);
        }
        // If we exit the loop without reaching goal, pathList remains empty
    }

    // 5) Examine the four neighbors (up/down/left/right)
    private void discoverNeighbors(Nodes nodeToCheck) {
        int col = nodeToCheck.col;
        int row = nodeToCheck.row;

        // Up
        if (row - 1 >= 0) evaluateNode(node[col][row - 1], nodeToCheck);
        // Down
        if (row + 1 < gp.maxWorldRow) evaluateNode(node[col][row + 1], nodeToCheck);
        // Left
        if (col - 1 >= 0) evaluateNode(node[col - 1][row], nodeToCheck);
        // Right
        if (col + 1 < gp.maxWorldCol) evaluateNode(node[col + 1][row], nodeToCheck);
    }

    // Process a neighbor: skip if it's solid or already checked
    private void evaluateNode(Nodes neighbor, Nodes current) {
        if (!neighbor.solid && !neighbor.checked) {
            int gCost = current.gCost + 1;
            int hCost = Math.abs(neighbor.col - goalNode.col)
                    + Math.abs(neighbor.row - goalNode.row);
            int fCost = gCost + hCost;

            if (!neighbor.open) {
                neighbor.gCost = gCost;
                neighbor.hCost = hCost;
                neighbor.fCost = fCost;
                neighbor.parent = current;
                neighbor.open = true;
                openList.add(neighbor);
            } else if (fCost < neighbor.fCost) {
                // Found a cheaper path to this neighbor
                neighbor.gCost = gCost;
                neighbor.hCost = hCost;
                neighbor.fCost = fCost;
                neighbor.parent = current;
            }
        }
    }

    // 6) Backtrack from goalNode to startNode to create the pathList
    private void buildPath() {
        Nodes temp = goalNode;
        ArrayList<Nodes> reversed = new ArrayList<>();

        while (temp != startNode) {
            reversed.add(temp);
            temp = temp.parent;
        }
        reversed.add(startNode);

        // Reverse it so pathList[0] is the start, pathList[last] is the goal
        for (int i = reversed.size() - 1; i >= 0; i--) {
            pathList.add(reversed.get(i));
        }
    }

    public ArrayList<Nodes> getPathList() {
        return pathList;
    }


}
