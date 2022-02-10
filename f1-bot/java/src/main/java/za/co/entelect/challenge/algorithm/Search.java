package za.co.entelect.challenge.algorithm;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.utils.Abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Search {
    public Command[] bestCommands;
    private GameState gameState; // state to simulate
    private Command predictOpp(){
        return Abilities.ACCELERATE;
    }
    private GameState getNextState(GameState curState, Command curCommand, Command curOppCommand){
        return curState;
    }
    private List<Command> validAction(GameState curState){
        List<Command> valid =  new ArrayList<Command>();
        return valid;
    }
    public Search(GameState gameState){
        this.gameState = gameState;

    }


}
