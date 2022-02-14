package za.co.entelect.challenge.algorithm;


import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Search {
    public List<Command> bestActions;
    public GlobalState bestState;

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

        public Node clone(){
            Node clone = new Node();
            for(Command command: this.Actions){
                clone.Actions.add(command);
            }
            clone.State = this.State.clone();
            return clone;
        }
    }

    private Queue<Node> q;

    public Search(GlobalState state, boolean OpponentWise) {
        this.bestActions = new ArrayList<>();
        this.bestState = new GlobalState();
        q = new LinkedList<>();
        q.add(new Node(state));
        List<Node> Candidates = new LinkedList<>();
        while (!q.isEmpty()) {
            Node p = q.remove();
            for (Command cmd : p.Actions) {
                if(!OpponentWise){
                    p.State = Actions.simulateActions(cmd, Actions.predictAction(p.State.clone()), p.State);
                }
                else{
                    p.State = Actions.simulateActions(cmd, Abilities.ACCELERATE, p.State);
                }
            }
            if (p.State.map.nxeff < p.State.player.pos_x || p.Actions.size() == 4) {
                Candidates.add(p);
                continue;
            }
            for (Command newCmd : Actions.validAction(p.State)) {
                p.Actions.add(newCmd);
                q.add(p.clone());
                p.Actions.remove(p.Actions.size() - 1);
            }
        }
        boolean finalGame = false;
        for (Node node : Candidates) {
            if (node.State.player.pos_x >= 1500) {
                finalGame = true;
                break;
            }
        }
        if (finalGame) {
            int mx = Integer.MIN_VALUE;
            for (Node node : Candidates) {
                if (mx < node.State.player.speed && node.State.player.pos_x >= 1500) {
                    mx = node.State.player.speed;
                    for(Command command: node.Actions){
                        this.bestActions.add(command);
                    }
                    this.bestState = node.State.clone();
                }
            }
        }
        else{
            Double mx = Double.MIN_VALUE;
            double currentScore;
            for(Node node: Candidates){
                currentScore = Scoring.score(node, state);
                if(mx < currentScore){
                    mx = currentScore;
                    for(Command command: node.Actions){
                        this.bestActions.add(command);
                    }
                    this.bestState = node.State.clone();
                }
            }
        }
    }

}
