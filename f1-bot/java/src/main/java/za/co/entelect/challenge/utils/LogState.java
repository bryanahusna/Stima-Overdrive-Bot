package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.globalentities.Map;
import za.co.entelect.challenge.globalentities.Tile;

import java.util.List;

public class LogState {
    public GlobalState prevState;
    public GlobalState currentState;
    public Command action;

    public LogState(GlobalState prevState, GlobalState currentState, Command action) {
        this.prevState = prevState;
        this.currentState = currentState;
        this.action = action;
    }

    public Command calcOpponentCommand(Map globe) {
        Command cmd = this.action;

        if (cmd == Abilities.EMP) {
            return null;
        }
        cmd = Abilities.convertOffensive(cmd);
        int x = this.prevState.enemy.pos_x;
        int y = this.prevState.enemy.pos_y;

        if (this.prevState.enemy.nBoost == 1) {
            this.prevState.enemy.speed = Supports.getCurrentSpeedLimit(
                    this.prevState.enemy.damage
            );
        }
        int speed = this.prevState.enemy.speed;

        int afterX = this.currentState.enemy.pos_x;
        int afterY = this.currentState.enemy.pos_y;
        int afterSpeed = this.currentState.enemy.speed;

        int offsetX = afterX - x;
        int offsetY = afterY - y;

        List<Command> validActions = Actions.validAction(this.currentState.switch_());
        for (Command oppCmd : validActions) {
            GlobalState sim = Actions.simulateActions(cmd, oppCmd, this.prevState, globe);
            if (sim.enemy.pos_x == afterX
                    && sim.enemy.pos_y == afterY
                    && sim.enemy.speed == afterSpeed) {
                return oppCmd;
            }
        }

        if (offsetY != 0) {
            return offsetY < 0 ? Abilities.TURN_LEFT : Abilities.TURN_RIGHT;
        }

        if (offsetX == 0 && speed == afterSpeed
            // && offsetY == 0
        ) {
            return Abilities.FIX;
        }

        if (offsetX > speed) {
            if (offsetX <= Supports.getAcceleratedSpeed(speed, 0)) {
                return Abilities.ACCELERATE;
            } else {
                return Abilities.BOOST;
            }
        }

        if (offsetX == Supports.getDeceleratedSpeed(speed, false, 0)) {
            return Abilities.DECELERATE;
        }

        if (offsetX == speed) {
            int _y = afterY;
            int _speed = speed;
            int startX = offsetY != 0 ? x : x + 1;

            for (int _x = startX; _x < afterX + 1; _x++) {
                Tile block = globe.getTile(_x, _y);
                if (block.tile == Terrain.MUD) {
                    _speed = Supports.getDeceleratedSpeed(_speed, true, 0);
                } else if (block.tile == Terrain.OIL_SPILL) {
                    _speed = Supports.getDeceleratedSpeed(_speed, true, 0);
                } else if (block.tile == Terrain.WALL) {
                    _speed = 3;
                }
            }

            _speed = Math.max(3, _speed);
            return _speed < afterSpeed ? Abilities.LIZARD : Abilities.DO_NOTHING;
        }

        return null;
    }
}
