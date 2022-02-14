package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.globalentities.Tile;

import java.util.ArrayList;
import java.util.List;

public class Supports {

    /* Mengecek apakah dua command adalah command yang setipe */
    public static boolean isCommandEqual(Command a, Command b) {
        return a.render().equals(b.render());
    }

    public static List<Terrain> getBlocks(int lane, int startBlock, int length, List<Lane[]> lanes) {
        Lane[] laneArr = lanes.get(lane - 1);
        List<Terrain> blocks = new ArrayList<>();

        int startIdx = startBlock - laneArr[0].position.block;
        int endIdx = startIdx + length - 1;
        for (int i = startIdx; i <= endIdx && i < laneArr.length; i++) {
            if (laneArr[i] == null || laneArr[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneArr[i].terrain);
        }
        return blocks;
    }

    public static boolean isTileEqual(Tile a, Tile b) {
        return (a.x == b.x && a.y == b.y);
    }

    public static boolean sameCoordinate(Tile a, int x, int y) {
        return (a.x == x && a.y == y);
    }

    public static List<Tile> getPath(int x, int y, int v, int damage, Command cmd, GlobalState state) {
        List<Tile> ret = new ArrayList<Tile>();
        ret.add(state.map.getTile(x, y));
        int dx = 0;
        int dy = 0;
        if (isCommandEqual(cmd, Abilities.TURN_LEFT)) {
            dy -= 1;
            dx -= 1;
        } else if (isCommandEqual(cmd, Abilities.TURN_RIGHT)) {
            dy += 1;
            dx -= 1;
        } else if (isCommandEqual(cmd, Abilities.ACCELERATE)) {
            v = getAcceleratedSpeed(v, damage);
        } else if (isCommandEqual(cmd, Abilities.DECELERATE)) {
            v = getDeceleratedSpeed(v, false, damage);
        } else if (isCommandEqual(cmd, Abilities.BOOST)) {
            v = getBoostedSpeed(damage);
        }
        if (isCommandEqual(cmd, Abilities.FIX)) {
            return ret;
        } else if (isCommandEqual(cmd, Abilities.LIZARD)) {
            ret.add(state.map.getTile(x + v, y));
            return ret;
        } else {
            if (dy != 0) {
                ret.add(state.map.getTile(x, y+dy));
            }
            for (int i = x + 1; i <= x + dx + v; i++) {
                ret.add(state.map.getTile(i, y+dy));
                if (state.map.getTile(i, y+dy).layer == Terrain.CYBERTRUCK) {
                    break;
                }
            }
            return ret;
        }
    }

    public static int getCurrentSpeedLimit(int damage) {
        int currentMaxSpeed;
        switch (damage) {
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

    public static Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static int getAcceleratedSpeed(int speed, int damage) {
        int acceleratedSpeed;
        switch (speed) {
            case 0:
                acceleratedSpeed = 3;
                break;
            case 3:
                acceleratedSpeed = 5;
                break;
            case 5:
                acceleratedSpeed = 6;
                break;
            case 6:
                acceleratedSpeed = 8;
                break;
            default:
                acceleratedSpeed = 9;
                break;
        }
        return Math.min(acceleratedSpeed, getCurrentSpeedLimit(damage));
    }

    public static int getDeceleratedSpeed(int speed, boolean isMudOil, int damage) {
        int deceleratedSpeed;
        switch (speed) {
            case 15:
                deceleratedSpeed = 9;
                break;
            case 9:
                deceleratedSpeed = 8;
                break;
            case 8:
                deceleratedSpeed = 6;
                break;
            case 6:
                deceleratedSpeed = 3;
                break;
            case 3:
                if (isMudOil) {
                    deceleratedSpeed = 3;
                } else {
                    deceleratedSpeed = 0;
                }
                break;
            default:
                deceleratedSpeed = 0;
        }
        return Math.min(deceleratedSpeed, getCurrentSpeedLimit(damage));
    }

    public static int getBoostedSpeed(int damage) {
        return Math.min(15, getCurrentSpeedLimit(damage));
    }

}