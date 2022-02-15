package za.co.entelect.challenge.algorithm;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.utils.Abilities;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public List<Command> Actions;
    public GlobalState State;

    public Node(GlobalState state) {
        this.State = state.clone();
        this.Actions = new ArrayList<>();
    }

    public Node(){
        this.Actions = new ArrayList<>();
        this.State = new GlobalState();
    }

    public Node clone(GlobalState newState){
        Node clone = new Node();
        clone.Actions.addAll(this.Actions);
        clone.State = newState.clone();
        return clone;
    }

    public void print(){
        System.out.print("[");
        for (Command cmd : this.Actions) {
            Abilities.print(cmd);
        }
        System.out.println("]");
    }
}
