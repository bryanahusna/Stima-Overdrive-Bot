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
    private Queue<Node> q;

    public Search(GlobalState state, Map globe, int depth, int depthOpp) {
        this.candidateActions = new ArrayList<>();
        q = new LinkedList<>();
        q.add(new Node(state));
        List<Node> Candidates = new LinkedList<>();
        while (!q.isEmpty()) {
            Node p = q.remove();
            if (p.Actions.size() == depth) {
                Candidates.add(p);
            }
            if(p.Actions.size()<depth) {
                for (Command newCmd : Actions.validAction(p.State.player)) {
                    Node curNode = p.clone(p.State.clone());
                    curNode.Actions.add(newCmd);
                    curNode.State = Actions.simulateActions(newCmd, Actions.predictAction(p.State.clone(), globe, depthOpp), p.State.clone(), globe);
                    if(curNode.State.player.pos_x >= globe.nxeff){
                        Candidates.add(curNode);
                    }
                    else {
                        q.add(curNode);
                    }
                }
            }
        }
        for(int i=0; i<Candidates.size(); i++){
            if(Candidates.get(i).Actions.size()!=0){
                this.candidateActions.add(Candidates.get(i));
            }
        }
        //this.bestActions.addAll(Candidates.get(0).Actions);
    }
    public List<Command> bestAction(GlobalState state, Map globe, int depthOpp){
        List<Command> bestAction = new ArrayList<>();
        int idmx = -1;
        boolean finalGame = false;
        for (Node node : this.candidateActions) {
            if (node.State.player.pos_x >= 1500) {
                finalGame = true;
                break;
            }
        }
        if (finalGame) {
            int mx = Integer.MIN_VALUE;
            for (int i=0; i<this.candidateActions.size(); i++) {
                Node node = this.candidateActions.get(i);
                if (mx < node.State.player.speed && node.State.player.pos_x >= 1500) {
                    idmx = i;
                    mx = node.State.player.speed;
                }
            }
        }
        else{
            Double mx = -Double.MAX_VALUE;
            double currentScore;
            for (int i=0; i<this.candidateActions.size(); i++){
                Node node = this.candidateActions.get(i);
                //node.print();
                currentScore = Scoring.scorePlayer(node, state, globe, depthOpp);
                //System.out.println("SCORE : " + currentScore);
                //node.State.player.printAll();
                if(mx < currentScore){
                    idmx = i;
                    mx = currentScore;
                }
            }
        }
        bestAction.addAll(this.candidateActions.get(idmx).Actions);
        return bestAction;
    }
}
