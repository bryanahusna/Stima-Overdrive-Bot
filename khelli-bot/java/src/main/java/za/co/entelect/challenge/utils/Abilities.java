package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.Car;

public class Abilities {
    // NORMAL COMMANDS
    public final static Command ACCELERATE = new AccelerateCommand();
    public final static Command BOOST = new BoostCommand();
    public final static Command DECELERATE = new DecelerateCommand();
    public final static Command DO_NOTHING = new DoNothingCommand();
    public final static Command FIX = new FixCommand();
    // BOOST COMMAND
    public final static Command EMP = new EmpCommand();
    public final static Command LIZARD = new LizardCommand();
    public final static Command OIL = new OilCommand();

    public static Command TWEET(int lane, int block) {
        return new TweetCommand(lane, block);
    }

    // MOVEMENT COMMAND
    public final static Command TURN_LEFT = new ChangeLaneCommand(-1);
    public final static Command TURN_RIGHT = new ChangeLaneCommand(1);

    // ENEMY READING ABILITIES
    // ENEMY STATES
    public static Boolean isEnemyInFront(Car myCar, Car opponent) {
        return (opponent.position.block > myCar.position.block);
    }

    public static Boolean isEnemyBehind(Car myCar, Car opponent) {
        return (opponent.position.block < myCar.position.block);
    }

    public static Boolean isEnemySameLane(Car myCar, Car opponent) {
        return (opponent.position.lane == myCar.position.lane);
    }

    public static Command followEnemy(Car myCar, Car opponent) {
        int delta = opponent.position.lane - myCar.position.lane;
        if (delta < 0) {
            return Abilities.TURN_LEFT;
        } else if (delta > 0) {
            return Abilities.TURN_RIGHT;
        }
        return Abilities.EMP;
    }

    public static Command avoidEnemy(int leftScore, int rightScore) {
        if (rightScore <= leftScore) {
            return Abilities.TURN_RIGHT;
        }
        return Abilities.TURN_LEFT;
    }
}
