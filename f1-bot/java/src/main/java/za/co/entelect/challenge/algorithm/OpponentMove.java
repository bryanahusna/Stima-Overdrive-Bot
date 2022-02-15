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

public class OpponentMove {
    List<Node> CandidateMoves;
    private Queue<Node> q;
    public OpponentMove(GlobalState state, Map globe, int depth){
        //System.out.println("DEPTH: " + depth);
        this.CandidateMoves = new ArrayList<>();
        q = new LinkedList<>();
        q.add(new Node(state));
        List<Node> Candidates = new LinkedList<>();
        while (!q.isEmpty()) {
            Node p = q.remove();
            if (p.Actions.size() == depth) {
                Candidates.add(p);
            }
            if(p.Actions.size()<depth) {
                //System.out.println("VALID MOVE: " + Actions.validAction(p.State.enemy).size());
                for (Command newCmd : Actions.validAction(p.State.enemy)) {
                    Node curNode = p.clone(p.State.clone());
                    curNode.Actions.add(newCmd);
                    curNode.State = Actions.simulateActions(Abilities.ACCELERATE, newCmd, p.State.clone(), globe);
                    if(curNode.State.enemy.pos_x >= globe.nxeff){
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
                this.CandidateMoves.add(Candidates.get(i));
            }
        }
        if(this.CandidateMoves.size()==0){

        }
        //System.out.println("SIZE: "+this.CandidateMoves.size());
    }

    public List<Command> bestMove(GlobalState state, Map globe){
        List<Command> bestAction = new ArrayList<>();
        int idmx = -1;
        boolean finalGame = false;
        for (Node node : this.CandidateMoves) {
            if (node.State.enemy.pos_x >= 1500) {
                finalGame = true;
                break;
            }
        }
        if (finalGame) {
            int mx = Integer.MIN_VALUE;
            for (int i=0; i<this.CandidateMoves.size(); i++) {
                Node node = this.CandidateMoves.get(i);
                if (mx < node.State.enemy.speed && node.State.enemy.pos_x >= 1500) {
                    idmx = i;
                    mx = node.State.enemy.speed;
                }
            }
        }
        else{
            Double mx = -Double.MAX_VALUE;
            double currentScore;
            for (int i=0; i<this.CandidateMoves.size(); i++){
                Node node = this.CandidateMoves.get(i);
                currentScore = Scoring.scoreEnemy(node, state, globe);
                if(mx < currentScore){
                    idmx = i;
                    mx = currentScore;
                }
            }
        }
        bestAction.addAll(this.CandidateMoves.get(idmx).Actions);
        return bestAction;
    }
}
