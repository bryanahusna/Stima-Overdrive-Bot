package za.co.entelect.challenge;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Calculations;
import za.co.entelect.challenge.utils.Supports;

import java.util.List;
import java.util.Random;

public class Bot {
    private final GameState gameState;
    private final Car opponent;
    private final Car myCar;
    private final int currentSpeedLimit;

    public Bot(Random random, GameState gameState) {
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
        this.currentSpeedLimit = Supports.getCurrentSpeedLimit(gameState);
    }

    public Command run() {
        // Calculate obstacle score in list [LEFT, FRONT, RIGHT]
        List<Integer> laneScores = Calculations.calculateLaneScores(this.gameState,
                Supports.getAcceleratedSpeed(this.myCar.speed));
        int leftScore = laneScores.get(0);
        int frontScore = laneScores.get(1);
        int rightScore = laneScores.get(2);

        if (Abilities.isEnemySameLane(this.myCar, this.opponent)) {
            return Abilities.avoidEnemy(leftScore, rightScore);
        }

        if (this.myCar.damage >= 2
                && this.currentSpeedLimit == this.myCar.speed) {
            return Abilities.FIX;
        }

        if (frontScore > 0
                && Supports.hasPowerUp(PowerUps.LIZARD, this.myCar.powerups)) {
            return Abilities.LIZARD;
        }

        if (Supports.hasPowerUp(PowerUps.TWEET, this.myCar.powerups)) {
            return Abilities.TWEET(this.opponent.position.lane,
                    this.opponent.position.block + this.opponent.speed + 1);
        }

        if (Abilities.isEnemyBehind(this.myCar, this.opponent)
                && Abilities.isEnemySameLane(this.myCar, this.opponent)
                && Supports.hasPowerUp(PowerUps.OIL, this.myCar.powerups)) {
            return Abilities.OIL;
        }

        if (Abilities.isEnemyInFront(this.myCar, this.opponent)
                && Abilities.isEnemySameLane(this.myCar, this.opponent)
                && Supports.hasPowerUp(PowerUps.EMP, this.myCar.powerups)
                && Abilities.isAbleToSeeEnemy(this.myCar, this.opponent)) {
            return Abilities.EMP;
        }

        if (Abilities.isEnemyInFront(this.myCar, this.opponent)
                && Supports.hasPowerUp(PowerUps.EMP, this.myCar.powerups)
                && Abilities.isAbleToSeeEnemy(this.myCar, this.opponent)) {
            return Abilities.followEnemy(this.myCar, this.opponent);
        }

        if (frontScore > 0 && this.myCar.speed > 0) {
            if (frontScore <= leftScore && frontScore <= rightScore) {
                if (this.myCar.speed < this.currentSpeedLimit
                        && Supports.hasPowerUp(PowerUps.BOOST, this.myCar.powerups)) {
                    if (this.myCar.damage > 0) {
                        return Abilities.FIX;
                    } else {
                        return Abilities.BOOST;
                    }
                }
                return Abilities.ACCELERATE;
            }

            if (leftScore < frontScore && leftScore <= rightScore) {
                return Abilities.TURN_LEFT;
            } else {
                return Abilities.TURN_RIGHT;
            }
        }

        return Abilities.ACCELERATE;
    }
}
