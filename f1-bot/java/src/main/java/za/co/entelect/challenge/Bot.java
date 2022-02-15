package za.co.entelect.challenge;

import com.google.gson.Gson;
import za.co.entelect.challenge.algorithm.Search;
import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.command.TweetCommand;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.globalentities.Map;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Actions;
import za.co.entelect.challenge.utils.LogState;
import za.co.entelect.challenge.utils.Supports;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;


public class Bot {
    private static final String ROUNDS_DIRECTORY = "rounds";
    private static final String STATE_FILE_NAME = "state.json";
    public GlobalState globalState;
    public Queue<LogState> log;
    public GameState gameState;
    public Car opponent;
    public Car myCar;
    public int currentSpeedLimit;
    public Map globalMap;


    public Bot() {
        this.globalState = new GlobalState();
        this.log = new LinkedList<>();
        this.globalMap = new Map();
    }

    public void takeRound(GameState gameState, Command command) {
        GlobalState prevState = this.globalState.clone();
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
        this.currentSpeedLimit = Supports.getCurrentSpeedLimit(gameState.player.damage);
        //this.globalState.map.updateNewRound(gameState);
        this.globalMap.updateNewRound(gameState);
        this.globalState.player.update(gameState, 1);
        this.globalState.enemy.update(gameState, 2);
        if (command != null) {
            LogState transition = new LogState(prevState, this.globalState, command);
            this.log.add(transition);
            while (!this.log.isEmpty()) {
                LogState head = this.log.peek();
                if (head.currentState.enemy.pos_x - this.globalMap.nxeff > 0) {
                    break;
                } else {
                    this.globalState.enemy.update(head, globalMap);
                    this.log.remove();
                }
            }
        }
    }

    public List<Command> searchBestAction(int v){
        Search Candidates;
        List<Command> ret;
        switch(v){
            case 0:
            case 3:
                Candidates = new Search(this.globalState, globalMap, 4, 0);
                ret = Candidates.bestAction(this.globalState, globalMap, 0);
                break;
            case 6:
                Candidates = new Search(this.globalState, globalMap, 3, 1);
                ret = Candidates.bestAction(this.globalState, globalMap, 1);
                break;
            default:
                Candidates = new Search(this.globalState, globalMap, 3, 2);
                ret = Candidates.bestAction(this.globalState, globalMap, 2);
        }
        return ret;
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        Gson gson = new Gson();
        Command prevCommand = null;
        while (true) {
            try {
                int roundNumber = sc.nextInt();

                String statePath = String.format("./%s/%d/%s", ROUNDS_DIRECTORY, roundNumber, STATE_FILE_NAME);
                String state = new String(Files.readAllBytes(Paths.get(statePath)));

                GameState gameState = gson.fromJson(state, GameState.class);

//                if(prevCommand != null){
//                    if(prevCommand instanceof TweetCommand){
//
//                    }
//                }

                takeRound(gameState, prevCommand);
                List<Command> commands = searchBestAction(myCar.speed);
                Command command = commands.get(0);
                if (Supports.isCommandEqual(command, Abilities.DO_NOTHING)) {
                    command = Actions.bestAttack(commands, this.globalState, globalMap);
                    if (Supports.isCommandEqual(command, Abilities.OIL)) {
                        this.globalMap.setTile(myCar.position.block, myCar.position.lane, Terrain.OIL_SPILL);
                    }
                }
                System.out.println(String.format("C;%d;%s", roundNumber, command.render()));
                prevCommand = command;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        Command[] commands = new Command[6];
//        commands[0] = Abilities.ACCELERATE;
//        commands[1] = Abilities.BOOST;
//        commands[2] = Abilities.TURN_LEFT;
//        commands[3] = Abilities.TURN_RIGHT;
//        commands[4] = Abilities.LIZARD;
//        commands[5] = Abilities.FIX;
//        Scoring bestCmds = null;
//        float bestScore = Scoring.INVALID_COMMAND;
//
//        for(int i = 0; i < 6; i++){
//            float currentScore;
//            List<Command> cmds = new ArrayList<>();
//            cmds.add(commands[i]);
//            Scoring currScoring = new Scoring(cmds, gameState);
//            currentScore = currScoring.calculate();
//            if(currentScore > bestScore){
//                bestScore = currentScore;
//                bestCmds = currScoring;
//            }
//        }
//
//        for(int i = 0; i < 6; i++){
//            for(int j = 0; j < 6; j++){
//                float currentScore;
//                List<Command> cmds = new ArrayList<>();
//                cmds.add(commands[i]);
//                cmds.add(commands[j]);
//                Scoring currScoring = new Scoring(cmds, gameState);
//                currentScore = currScoring.calculate();
//                if(currentScore > bestScore){
//                    bestScore = currentScore;
//                    bestCmds = currScoring;
//                }
//            }
//        }
//
//        for(int i = 0; i < 6; i++){
//            for(int j = 0; j < 6; j++){
//                for(int k = 0; k < 6; k++){
//                    float currentScore;
//                    List<Command> cmds = new ArrayList<>();
//                    cmds.add(commands[i]);
//                    cmds.add(commands[j]);
//                    cmds.add(commands[k]);
//                    Scoring currScoring = new Scoring(cmds, gameState);
//                    currentScore = currScoring.calculate();
//                    if(currentScore > bestScore){
//                        bestScore = currentScore;
//                        bestCmds = currScoring;
//                    }
//                }
//            }
//        }
//
//        if(bestCmds != null){
//            return bestCmds.commands.get(0);
//        }

        // Calculate obstacle score in list [LEFT, FRONT, RIGHT]
        
        /*List<Integer> laneScores = Calculations.calculateLaneScores(this.gameState,
                Supports.getAcceleratedSpeed(this.myCar.speed, this.myCar.damage));
        int leftScore = laneScores.get(0);
        int frontScore = laneScores.get(1);
        int rightScore = laneScores.get(2);

        if (Abilities.isEnemySameLane(this.myCar, this.opponent)) {
            return Abilities.avoidEnemy(leftScore, rightScore);
        }

        if (this.myCar.damage >= 2
                && this.currentSpeedLimit == this.myCar.speed) {
            return Abilities.FIX;
        }

        if (frontScore > 0
                && Supports.hasPowerUp(PowerUps.LIZARD, this.myCar.powerups)) {
            return Abilities.LIZARD;
        }

        if (Supports.hasPowerUp(PowerUps.TWEET, this.myCar.powerups)) {
            return Abilities.TWEET(this.opponent.position.lane,
                    this.opponent.position.block + this.opponent.speed + 1);
        }

        if (Abilities.isEnemyBehind(this.myCar, this.opponent)
                && Abilities.isEnemySameLane(this.myCar, this.opponent)
                && Supports.hasPowerUp(PowerUps.OIL, this.myCar.powerups)) {
            return Abilities.OIL;
        }

        if (Abilities.isEnemyInFront(this.myCar, this.opponent)
                && Abilities.isEnemySameLane(this.myCar, this.opponent)
                && Supports.hasPowerUp(PowerUps.EMP, this.myCar.powerups)
                && Abilities.isAbleToSeeEnemy(this.myCar, this.opponent)) {
            return Abilities.EMP;
        }

        if (Abilities.isEnemyInFront(this.myCar, this.opponent)
                && Supports.hasPowerUp(PowerUps.EMP, this.myCar.powerups)
                && Abilities.isAbleToSeeEnemy(this.myCar, this.opponent)) {
            return Abilities.followEnemy(this.myCar, this.opponent);
        }

        if (frontScore > 0 && this.myCar.speed > 0) {
            if (frontScore <= leftScore && frontScore <= rightScore) {
                if (this.myCar.speed < this.currentSpeedLimit
                        && Supports.hasPowerUp(PowerUps.BOOST, this.myCar.powerups)) {
                    if (this.myCar.damage > 0) {
                        return Abilities.FIX;
                    } else {
                        return Abilities.BOOST;
                    }
                }
                return Abilities.ACCELERATE;
            }

            if (leftScore < frontScore && leftScore <= rightScore) {
                return Abilities.TURN_LEFT;
            } else {
                return Abilities.TURN_RIGHT;
            }
        }*/

        //return Abilities.ACCELERATE;
    }
}
