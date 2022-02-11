package za.co.entelect.challenge.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import za.co.entelect.challenge.Bot;
import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.entities.Lane;

public class Supports {
    /* Mengecek apakah dua command adalah command yang setipe */

    public static boolean isCommandEqual(Command a, Command b){
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
                acceleratedSpeed =  9;
                break;
        }
        return Math.min(acceleratedSpeed, getCurrentSpeedLimit(damage));
    }

    public static int getDeceleratedSpeed(int speed, boolean isMudOil){
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
                if(isMudOil){
                    deceleratedSpeed = 3;
                } else{
                    deceleratedSpeed = 0;
                }
                break;
            default:
                deceleratedSpeed = 0;
        }
        return deceleratedSpeed;
    }

    public static int getBoostedSpeed(int damage){
        return Math.min(15, getCurrentSpeedLimit(damage));
    }

    public static void TrackLog(Queue<LogState> Q){
        LogState curLog = Q.remove();
        //Bot.enemy.update(curLog);
    }
}