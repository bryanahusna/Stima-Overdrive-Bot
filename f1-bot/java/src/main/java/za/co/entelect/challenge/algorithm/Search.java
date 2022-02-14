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
    }

    private Queue<Node> q;

    public Search(GlobalState state, boolean OpponentWise) {
        q = new LinkedList<>();
        q.add(new Node(state));
        List<Node> Candidates = new LinkedList<>();
        while (!q.isEmpty()) {
            Node p = q.remove();
            for (Command cmd : p.Actions) {
                if(!OpponentWise){
                    p.State = Actions.simulateActions(cmd, Actions.predictAction(p.State), p.State);
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
                q.add(p);
                p.Actions.remove(p.Actions.size() - 1);
            }
        }
        // TODO:
        // Scoring from hasil state
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
                    this.bestActions = node.Actions;
                    this.bestState = node.State;
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
                    this.bestActions = node.Actions;
                    this.bestState = node.State;
                }
            }
        }
    }

}
