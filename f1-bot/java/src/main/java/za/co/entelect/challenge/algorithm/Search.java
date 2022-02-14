package za.co.entelect.challenge.algorithm;


import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.globalentities.Map;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Search {
    public List<Node> candidateActions;
    //public GlobalState bestState;

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
    }

    private Queue<Node> q;

    public Search(GlobalState state, boolean OpponentWise, Map globe) {
        this.candidateActions = new ArrayList<>();
        q = new LinkedList<>();
        q.add(new Node(state));
        List<Node> Candidates = new LinkedList<>();
        while (!q.isEmpty()) {
            Node p = q.remove();
            if(!OpponentWise){
                System.out.print("[");
                for (Command cmd : p.Actions) {
                    Abilities.print(cmd);
                }
                System.out.println("]");
            }
            if (p.Actions.size() == 3) {
                Candidates.add(p);
            }
            if(p.Actions.size()<4) {
                for (Command newCmd : Actions.validAction(p.State)) {
                    p.Actions.add(newCmd);
                    GlobalState newState;
                    if(!OpponentWise){
                        newState = Actions.simulateActions(newCmd, Actions.predictAction(p.State.clone(), globe), p.State.clone(), globe);
                    }
                    else{
                        newState = Actions.simulateActions(newCmd, Abilities.ACCELERATE, p.State.clone(), globe);
                    }
                    q.add(p.clone(newState));
                    p.Actions.remove(p.Actions.size() - 1);
                }
            }
        }
        this.candidateActions.addAll(Candidates);
        //this.bestActions.addAll(Candidates.get(0).Actions);
    }
    public List<Command> findBestAction(GlobalState state, Map globe,  boolean opponentWise){
        List<Command> bestAction = new ArrayList<>();
        boolean finalGame = false;
        for (Node node : this.candidateActions) {
            if (node.State.player.pos_x >= 1500) {
                finalGame = true;
                break;
            }
        }
        if (finalGame) {
            int mx = Integer.MIN_VALUE;
            for (Node node : this.candidateActions) {
                if (mx < node.State.player.speed && node.State.player.pos_x >= 1500) {
                    bestAction = new ArrayList<>();
                    mx = node.State.player.speed;
                    bestAction.addAll(node.Actions);
                }
            }
        }
        else{
            Double mx = Double.MIN_VALUE;
            double currentScore;
            for(Node node: this.candidateActions){
                currentScore = Scoring.score(node, state, globe, opponentWise);
                if(mx < currentScore){
                    bestAction = new ArrayList<>();
                    mx = currentScore;
                    bestAction.addAll(node.Actions);
                }
            }
        }
        return bestAction;
    }
}
