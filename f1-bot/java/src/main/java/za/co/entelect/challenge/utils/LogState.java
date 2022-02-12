package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.globalentities.GlobalState;

public class LogState {
    public GlobalState prevState;
    public GlobalState currentState;
    public Command actions;

    public LogState(GlobalState prevState, GlobalState currentState, Command actions) {
        this.prevState = prevState;
        this.currentState = currentState;
        this.actions = actions;
    }

    public Command calcOpponentCommand() {
        Command cmd = this.actions;

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

        int offsetX = afterX - x;
        int offsetY = afterY - y;

        // belom kelar
    }
}
