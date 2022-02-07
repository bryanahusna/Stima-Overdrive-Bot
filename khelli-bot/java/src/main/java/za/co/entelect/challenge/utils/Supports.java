package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.PowerUps;

public class Supports {
    public static int getCurrentSpeedLimit(GameState gameState) {
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

    public static Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp : available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static int getAcceleratedSpeed(int speed) {
        switch (speed) {
            case 0:
                return 3;
            case 3:
                return 5;
            case 5:
                return 6;
            case 6:
                return 8;
            default:
                return 9;
        }
    }
}