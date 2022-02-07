package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.max;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Command> directionList = new ArrayList<>();

    private final Random random;

    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot() {
        this.random = new SecureRandom();
        directionList.add(TURN_LEFT);
        directionList.add(TURN_RIGHT);
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;
        int currentSpeedLimit = getCurrentSpeedLimit(gameState);

        if (hasPowerUp(PowerUps.EMP, myCar.powerups)) {
            if (Math.abs(myCar.position.lane - opponent.position.lane) <= 1 && opponent.position.block > myCar.position.block) {
                return EMP;
            }
        }

        // Avoidance logic
        List<Terrain> frontBlocks = getBlocksFront(myCar, gameState, 1);
        List<Terrain> leftBlocks = getBlocksLeft(myCar, gameState, 1);
        List<Terrain> rightBlocks = getBlocksRight(myCar, gameState, 1);

        int frontObstacleScore = calculateObstacleScore(frontBlocks, 2, 4);
        int leftObstacleScore = calculateObstacleScore(leftBlocks, 2, 4);
        int rightObstacleScore = calculateObstacleScore(rightBlocks, 2, 4);


        if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
            frontObstacleScore = 0;
            return LIZARD;
        }
        if (frontObstacleScore > 0 && myCar.speed > 0) {
            if (frontObstacleScore <= leftObstacleScore && frontObstacleScore <= rightObstacleScore) {
                return ACCELERATE;
            } else if (leftObstacleScore < frontObstacleScore && leftObstacleScore <= rightObstacleScore) {
                return TURN_LEFT;
            } else {
                return TURN_RIGHT;
            }
        }

        if (myCar.speed < currentSpeedLimit) {
            if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
                if (myCar.damage > 0) {
                    return FIX;
                } else {
                    return BOOST;
                }
            } else {
                return ACCELERATE;
            }
        }

        if (myCar.damage > 0) {
            return FIX;
        }
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            return BOOST;
        }
        if (hasPowerUp(PowerUps.OIL, myCar.powerups) && opponent.position.block < myCar.position.block) {
            return OIL;
        }

        return ACCELERATE;
    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    private int calculateObstacleScore(List<Terrain> terrains, int mudScore, int wallScore) {
        int score = 0;
        if (terrains.size() == 0) {
            return 9999;
        }
        for (Terrain terrain : terrains) {
            if (terrain == Terrain.MUD || terrain == Terrain.OIL_SPILL) {
                score += mudScore;
            } else if (terrain == Terrain.WALL) {
                score += wallScore;
            } else if (terrain == Terrain.CYBERTRUCK) {
                score += 100;
            }
        }
        return score;
    }

    /* Mengambil daftar terrain block-block di depan, kanan, dan kiri mobil
     *  Mengambil sebanyak speed * speedMultiplier block
     *  Untuk getBlocksFront, block diambil mulai DARI DEPAN MOBIL (block mobil + 1).
     *  getBlocksLeft dan Right dimulai SEJAJAR DENGAN MOBIL (berguna mengecek pertimbangan turn left/right)
     *  Return List kosong jika arah tidak valid (misal cek block left jika mobil di lane 1) */
    private List<Terrain> getBlocksFront(Car myCar, GameState gameState, int speedMultiplier) {
        return getBlocks(myCar.position.lane, myCar.position.block + 1, speedMultiplier * myCar.speed, gameState);
    }

    private List<Terrain> getBlocksLeft(Car myCar, GameState gameState, int speedMultiplier) {
        if (myCar.position.lane == 1) {
            return new ArrayList<>();
        } else {
            return getBlocks(myCar.position.lane - 1, myCar.position.block, speedMultiplier * myCar.speed, gameState);
        }
    }

    private List<Terrain> getBlocksRight(Car myCar, GameState gameState, int speedMultiplier) {
        if (myCar.position.lane == 4) {
            return new ArrayList<>();
        } else {
            return getBlocks(myCar.position.lane + 1, myCar.position.block, speedMultiplier * myCar.speed, gameState);
        }
    }

    // lane dan block adalah posisi absolut, bukan indeks array
    private List<Terrain> getBlocks(int lane, int startBlock, int length, GameState gameState) {
        Lane[] laneArr = gameState.lanes.get(lane - 1);
        List<Terrain> blocks = new ArrayList<>();

        int startIdx = startBlock - laneArr[0].position.block;
        int endIdx = startIdx + length - 1;
        for (int i = startIdx; i <= endIdx; i++) {
            if (laneArr[i] == null || laneArr[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneArr[i].terrain);
        }
        return blocks;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns
     * the amount of blocks that can be traversed at max speed.
     */

    private List<Terrain> getBlocksInFront(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Terrain> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            if (laneList[i].occupiedByCyberTruck) {
                blocks.add(Terrain.CYBERTRUCK);
            } else {
                blocks.add(laneList[i].terrain);
            }

        }
        return blocks;
    }

    private int getCurrentSpeedLimit(GameState gameState) {
        int currentMaxSpeed;
        switch (gameState.player.damage) {
            case 0:
                currentMaxSpeed = 15;
                break;
            case 1:
                currentMaxSpeed = 9;
                break;
            case 2:
                currentMaxSpeed = 8;
                break;
            case 3:
                currentMaxSpeed = 6;
                break;
            case 4:
                currentMaxSpeed = 3;
                break;
            case 5:
                currentMaxSpeed = 0;
                break;
            default:
                currentMaxSpeed = -1;
        }
        return currentMaxSpeed;
    }

}
