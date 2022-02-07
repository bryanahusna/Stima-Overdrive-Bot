package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.Terrain;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class Calculations {
    public static List<Integer> calculateLaneScores(GameState gameState, int speed) {
        List<Terrain> leftBlocks = getBlocksInFront(gameState.player.position.lane - 1,
                gameState.player.position.block,
                gameState, speed);
        List<Terrain> frontBlocks = getBlocksInFront(gameState.player.position.lane,
                gameState.player.position.block,
                gameState, speed);
        List<Terrain> rightBlocks = getBlocksInFront(gameState.player.position.lane + 1,
                gameState.player.position.block,
                gameState, speed);

        List<Integer> laneScores = new ArrayList<>();
        laneScores.add(calculateObstacleScore(leftBlocks));
        laneScores.add(calculateObstacleScore(frontBlocks));
        laneScores.add(calculateObstacleScore(rightBlocks));

        return laneScores;
    }

    public static int calculateObstacleScore(List<Terrain> terrains) {
        int score = 0;
        if (terrains.size() == 0) {
            return 9999;
        }
        for (Terrain terrain : terrains) {
            if (terrain == Terrain.MUD) {
                score += Constants.LOW_OBS_SCORE;
            } else if (terrain == Terrain.ENEMY) {
                score += Constants.LOW_OBS_SCORE;
            } else if (terrain == Terrain.OIL_SPILL) {
                score += Constants.LOW_OBS_SCORE + 1;
            } else if (terrain == Terrain.WALL) {
                score += Constants.MIDDLE_OBS_SCORE;
            } else if (terrain == Terrain.CYBERTRUCK) {
                score += Constants.HIGH_OBS_SCORE;
            } else if (terrain == Terrain.TWEET) {
                score -= Constants.LOW_OBS_SCORE;
            }
        }
        return score;
    }

    private static List<Terrain> getBlocksInFront(int lane, int block,
                                                  GameState gameState, int speed) {
        /*
         * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
         * traversed at max speed.
         */
        List<Lane[]> map = gameState.lanes; // Read lanes
        List<Terrain> blocks = new ArrayList<>(); // To store block
        if (!isLaneValid(lane - 1)) {
            return blocks; // If lane invalid return empty list
        }

        // Pick leftmost col of map (only at start)
        int startBlock = map.get(0)[0].position.block;
        Lane[] laneList = map.get(lane - 1);

        // Only start at 0 in round 1, rest take block - startBlock
        for (int i = max(block - startBlock, 0); i <= block - startBlock + speed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            if (lane - 1 == gameState.opponent.position.lane && i == gameState.opponent.position.block) {
                blocks.add(Terrain.ENEMY);
            } else {
                blocks.add(laneList[i].terrain);
            }
        }
        return blocks;
    }

    private static Boolean isLaneValid(int lane) {
        return (lane >= 0) && (lane < 4);
    }

}
