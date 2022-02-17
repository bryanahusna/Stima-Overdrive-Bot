package za.co.entelect.challenge.utils;


import za.co.entelect.challenge.command.Command;

public class Supports {
    /* Mengecek apakah dua command adalah command yang setipe */
    public static boolean isCommandEqual(Command a, Command b) {
        return a.render().equals(b.render());
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
            case 5:
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