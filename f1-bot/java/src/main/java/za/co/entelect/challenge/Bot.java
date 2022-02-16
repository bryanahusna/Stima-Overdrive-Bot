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
    public GlobalState globalState, curState;
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
        this.curState = this.globalState.clone();

        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;
        this.currentSpeedLimit = Supports.getCurrentSpeedLimit(gameState.player.damage);
        this.globalMap.updateNewRound(gameState);
        this.curState.player.update(gameState, 1);
        this.curState.enemy.update(gameState, 2);



        if(command != null){
            if(command instanceof TweetCommand){
                if(globalState.isSomethingDeleted(1)){
                    globalMap.map[globalState.pref_x1-1][globalState.pref_y1-1].deleteCybertruck();
                }
                else if(globalState.isSomethingDeleted(2)){
                    globalMap.map[globalState.pref_x2-1][globalState.pref_y2-1].deleteCybertruck();
                }
                globalMap.map[((TweetCommand) command).block-1][((TweetCommand) command).lane-1].setCybertruck();
                curState.setCyberTruck(((TweetCommand) command).block, ((TweetCommand) command).lane);
            }
        }
        if (command != null) {
            LogState transition = new LogState(globalState, this.curState, command);
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
                Candidates = new Search(this.curState, globalMap, 4, 0);
                ret = Candidates.bestAction(this.curState, globalMap, 0);
                break;
            case 6:
                Candidates = new Search(this.curState, globalMap, 3, 1);
                ret = Candidates.bestAction(this.curState, globalMap, 1);
                break;
            default:
                Candidates = new Search(this.curState, globalMap, 3, 2);
                ret = Candidates.bestAction(this.curState, globalMap, 2);
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

                takeRound(gameState, prevCommand);
                List<Command> commands = searchBestAction(myCar.speed);
                Command command = commands.get(0);
                if (Supports.isCommandEqual(command, Abilities.DO_NOTHING)) {
                    command = Actions.bestAttack(commands, this.curState, globalMap);
                    if (Supports.isCommandEqual(command, Abilities.OIL)) {
                        this.globalMap.setTile(myCar.position.block, myCar.position.lane, Terrain.OIL_SPILL);
                    }
                }
                System.out.println(String.format("C;%d;%s", roundNumber, command.render()));
                prevCommand = command;
                this.globalState = this.curState;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
