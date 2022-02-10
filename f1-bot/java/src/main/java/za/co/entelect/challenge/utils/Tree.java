package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.GameState;

import java.util.ArrayList;
import java.util.List;
// Masih dalam progress
public class Tree {
    public class Node{
        public List<Command> actions;
        public GameState curState;
        public Node(GameState gameState){
            this.actions = new ArrayList<Command>();
            this.curState = gameState;
        }
        public Node(List<Command> actions, GameState gameState){
            this.actions = actions;
            this.curState = gameState;
        }
    }
    public Node curNode;
    public Tree(GameState gamestate){
        curNode = new Node(gamestate);
    }
    private Command predictOpp(GameState gameState){
        return Abilities.ACCELERATE;
    }
    private List<Command> validAct(GameState gameState){
        List<Command> ret = new ArrayList<Command>();
        ret.add(Abilities.ACCELERATE);
        return ret;
    }
    public GameState getNextState(GameState initState, Command Cmd, Command oppCmd){
        return new GameState();
    }
    public List<Node> Adj(){
        List<Command> actions = this.curNode.actions;
        GameState afterState = this.curNode.curState;
        // update afterstate dengan state selanjutnya
        for(Command Cmd:actions){
            Command oppCmd = predictOpp(afterState);
            afterState = getNextState(afterState, Cmd, oppCmd);
        }
        List<Node> ret = new ArrayList<Node>();
        for(Command command: validAct(afterState)){
            actions.add(command);
            Node adjacentNode = new Node(actions, afterState);
            ret.add(adjacentNode);
            actions.remove(actions.size()-1);
        }
        return ret;
    }

}
