package za.co.entelect.challenge.algorithm;

import za.co.entelect.challenge.utils.Weights;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.globalentities.Map;
import za.co.entelect.challenge.globalentities.Player;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Actions;

/* CARA PAKAI:
    1. Buat object baru dari class Scoring, dengan list commands (terurut) yang diinginkan dan gameState sekarang
    2. Jalankan method calculate, akan menghasilkan skornya
    
    Return INVALID_COMMAND jika ada command yang tidak valid (misal menggunakan boost ketika tidak punya boost) */
public class Scoring {
    public static double scorePlayer(Node n, GlobalState initialState, Map globe, int depthOpp) {
        double score = multiplyWeights(initialState, n.State, false);
        GlobalState immediateNextState = Actions.simulateActions(n.Actions.get(0),
                Actions.predictAction(initialState.clone(), globe, depthOpp),
                initialState,
                globe);
        score += Weights.AFTERSTATE * (multiplyWeights(n.State, immediateNextState, false));

        return score;
    }

    public static double scoreEnemy(Node n, GlobalState initialState, Map globe) {
        double score = multiplyWeights(initialState, n.State, true);
        GlobalState immediateNextState = Actions.simulateActions(Abilities.ACCELERATE,
                n.Actions.get(0),
                initialState,
                globe);
        score += Weights.AFTERSTATE * (multiplyWeights(n.State, immediateNextState, true));

        return score;
    }

    /* Menghitung skor berdasarkan finalState dan initialState */
    public static double multiplyWeights(GlobalState initialState, GlobalState finalState, boolean opponentWise) {
        double score = 0;
        Player initialPlayer;
        Player finalPlayer;
        if (!opponentWise) {
            initialPlayer = initialState.player;
            finalPlayer = finalState.player;
        } else {
            initialPlayer = initialState.enemy;
            finalPlayer = finalState.enemy;
        }

        score += (finalPlayer.pos_x - initialPlayer.pos_x) * Weights.POSITION;
        score += (finalPlayer.speed) * Weights.SPEED;
        score += (finalPlayer.damage - initialPlayer.damage) * Weights.DAMAGE;
        score += (finalPlayer.score - initialPlayer.score) * Weights.SCORE;
        score += (finalPlayer.boost - initialPlayer.boost) * Weights.BOOST;
        score += (finalPlayer.oil - initialPlayer.oil) * Weights.OIL;
        score += (finalPlayer.tweet - initialPlayer.tweet) * Weights.TWEET;
        score += (finalPlayer.lizard - initialPlayer.lizard) * Weights.LIZARD;
        score += (finalPlayer.emp - initialPlayer.emp) * Weights.EMP;

        return score;
    }

}
