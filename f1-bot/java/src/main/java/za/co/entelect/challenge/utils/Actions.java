package za.co.entelect.challenge.utils;


import jdk.javadoc.internal.doclets.toolkit.util.Utils;
import za.co.entelect.challenge.algorithm.OpponentMove;
import za.co.entelect.challenge.algorithm.Search;
import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.globalentities.*;

import java.util.ArrayList;
import java.util.List;

public class Actions {
    public static GlobalState simulateActions(Command PlayerAction, Command EnemyAction, GlobalState InitState, Map globe) {
        GlobalState ret = InitState.clone();
        Player player = ret.player;
        Player enemy = ret.enemy;
        if (player.nBoost > 0) {
            player.nBoost--;
            if (player.nBoost == 0) {
                player.speed = Supports.getCurrentSpeedLimit(player.damage);
            }
            else if(Supports.isCommandEqual(PlayerAction, Abilities.DECELERATE)){
                player.nBoost = 0;
            }
        }

        if (enemy.nBoost > 0) {
            enemy.nBoost--;
            if (enemy.nBoost == 0) {
                enemy.speed = Supports.getCurrentSpeedLimit(enemy.damage);
            }
            else if(Supports.isCommandEqual(EnemyAction, Abilities.DECELERATE)){
                player.nBoost = 0;
            }
        }

        // for command that causes preliminary effect
        player.getFromAction(PlayerAction);
        enemy.getFromAction(EnemyAction);

        // calculate path
        Path PlayerPath = new Path();
        PlayerPath.updatePath(PlayerAction, player);

        Path EnemyPath = new Path();
        EnemyPath.updatePath(EnemyAction, enemy);

        // cybertruck
        Resource PlayerCyber = new Resource(globe, PlayerPath, PlayerAction, true);
        Resource EnemyCyber = new Resource(globe, EnemyPath, EnemyAction, true);
        for(Utils.Pair<Integer, Integer> P: PlayerCyber.cyberPos){
            ret.deleteCyberTruck(P.first, P.second);
        }
        for(Utils.Pair<Integer, Integer> P: EnemyCyber.cyberPos){
            ret.deleteCyberTruck(P.first, P.second);
        }
        // collision resolve
        PlayerPath.resolveCollision(EnemyPath, PlayerAction, EnemyAction);

        // collect resource after collision
        Resource PlayerResource = new Resource(globe, PlayerPath, PlayerAction, false);
        Resource EnemyResource = new Resource(globe, EnemyPath, EnemyAction, false);

        // update position
        player.updatePos(PlayerPath);
        enemy.updatePos(EnemyPath);

        // resource gathering
        player.updateResouce(PlayerResource);
        enemy.updateResouce(EnemyResource);


        return ret;
    }

    public static List<Command> validAction(Player player) {
        List<Command> ret = new ArrayList<>();
        if (player.speed > 0) {
            ret.add(Abilities.DO_NOTHING);
            ret.add(Abilities.DECELERATE);
            if (player.pos_y != 1) {
                ret.add(Abilities.TURN_LEFT);
            }
            if (player.pos_y != 4) {
                ret.add(Abilities.TURN_RIGHT);
            }
            if (player.lizard > 0) {
                ret.add(Abilities.LIZARD);
            }
        }
        if (player.speed < Supports.getCurrentSpeedLimit(player.damage)) {
            ret.add(Abilities.ACCELERATE);
        }
        if (player.boost > 0 && player.nBoost <= 1) {
            ret.add(Abilities.BOOST);
        }
        if(player.damage > 0){
            ret.add(Abilities.FIX);
        }
        return ret;
    }

    public static Command predictAction(GlobalState state, Map globe, int depth) {
        if (depth==0||state.enemy.pos_x > globe.nxeff) {
            return Abilities.ACCELERATE;
        }

        if (state.player.pos_x == state.enemy.pos_x - 1
                && state.player.pos_y == state.enemy.pos_y
                && state.enemy.speed == 0) {
            return Abilities.DO_NOTHING;
        }
        return (new OpponentMove(state, globe, depth)).bestMove(state, globe).get(0);
    }

    public static Command bestAttack(List<Command> Commands, GlobalState curState, Map globe) {
        // offensive move
        GlobalState state1 = Actions.simulateActions(Commands.get(0), Actions.predictAction(curState, globe, 3), curState, globe);
        GlobalState state2 = null;
        if (Commands.size() > 2) {
            state2 = Actions.simulateActions(Commands.get(1), Actions.predictAction(state1, globe, 3), state1, globe);
        }
        int x = curState.player.pos_x;
        int x1 = curState.enemy.pos_x;
        int y = curState.player.pos_y;
        int y1 = curState.enemy.pos_y;
        if (curState.player.emp > 0) {
            if (x < x1 && Math.abs(y - y1) <= 1) {
                // jangan EMP kalau kita malah nabrak lawan
                if (y == y1) {
                    GlobalState stateCrash = Actions.simulateActions(Abilities.DO_NOTHING, Abilities.DO_NOTHING, curState, globe);
                    if (stateCrash.player.pos_x < curState.enemy.pos_x) {
                        return Abilities.EMP;
                    }
                } else {
                    return Abilities.EMP;
                }
            }
        } else if (curState.player.oil > 0) {
            if (x1 + curState.enemy.speed >= x && y == y1) {
                return Abilities.OIL;
            }
        } else if (curState.player.tweet > 0) {
            if (x > x1 && state2 != null) {
                // kalau abis round ini prediksinya FIX, ga usah simpen cybertruck
                if (!Supports.isCommandEqual(Actions.predictAction(state1, globe, 3), Abilities.FIX)) {
                    // simpen agak jauh dari lawan, biar kasus kalau abs(x-x1)=1 ga terjadi
                    // hati-hati juga, bisa jadi lawan nge-EMP kita pas placing cybertruck
                    int cyber_x = state1.enemy.pos_x + 2;
                    int cyber_y = state2.enemy.pos_y;
                    if (Math.abs(y - y1) <= 1 && cyber_x == x && cyber_y == y) {
                        cyber_x--;
                    }
                    if (cyber_x < state1.player.pos_x) {
//                        int prevCyber_x = curState.player.cyber_x;
//                        int prevCyber_y = curState.player.cyber_y;
//                        if(prevCyber_x!=0){
//                            //curState.map.getTile(prevCyber_x, prevCyber_y).deleteCybertruck();
//                            curState.deleteCyberTruck(prevCyber_x, prevCyber_y);
//                        }
//                        curState.setCyberTruck(cyber_x, cyber_y);
//                        curState.player.changeCyberTruck(cyber_x, cyber_y);
                        return Abilities.TWEET(cyber_y, cyber_x);
                    }
                }
            }
        } else if (curState.player.oil > 0) {
            for (int px = Math.max(1, x - 10); px <= Math.min(1500, x + 10); px++) {
                if (y > 1) {
                    if (globe.getTile(px, y - 1).isBad()) {
                        return Abilities.OIL;
                    }
                } else if (y < 4) {
                    if (globe.getTile(px, y + 1).isBad()) {
                        return Abilities.OIL;
                    }
                }
            }
            if (curState.player.oil >= 3) {
                return Abilities.OIL;
            }
        }
        return Abilities.DO_NOTHING;
    }
}
