package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.algorithm.Search;
import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.globalentities.Player;
import za.co.entelect.challenge.globalentities.Tile;

import java.util.ArrayList;
import java.util.List;

public class Actions {
    public static GlobalState simulateActions(Command PlayerAction, Command EnemyAction, GlobalState InitState) {
        GlobalState ret = InitState.clone();
        Player player = ret.player;
        Player enemy = ret.enemy;
        if (player.nBoost > 0) {
            player.nBoost--;
            if (player.nBoost == 0) {
                player.speed = Supports.getCurrentSpeedLimit(player.damage);
            }
        }
        if (enemy.nBoost > 0) {
            enemy.nBoost--;
            if (enemy.nBoost == 0) {
                enemy.speed = Supports.getCurrentSpeedLimit(enemy.damage);
            }
        }
        // calculate path sampe tile == cybertruck atau selesai
        int speedPlayer = player.speed;
        int speedEnemy = enemy.speed;
        int playerX = player.pos_x;
        int playerY = player.pos_y;
        int enemyX = enemy.pos_x;
        int enemyY = enemy.pos_y;
        int playerDamage = player.damage;
        int enemyDamage = enemy.damage;
        List<Tile> PlayerPath = Supports.getPath(
                playerX,
                playerY,
                speedPlayer,
                playerDamage,
                PlayerAction,
                ret
        );
        List<Tile> EnemyPath = Supports.getPath(
                enemyX,
                enemyY,
                speedEnemy,
                enemyDamage,
                EnemyAction,
                ret
        );

        List<Tile> Cyber = new ArrayList<>();

        // for command that cause preliminary effect
        player.getFromAction(PlayerAction);
        enemy.getFromAction(EnemyAction);

        if (PlayerPath.get(PlayerPath.size() - 1).tile == Terrain.CYBERTRUCK) {
            player.score -= 7;
            player.damage = Math.min(player.damage + 2, 5);
            player.nBoost = 0;
            Cyber.add(PlayerPath.get(PlayerPath.size() - 1));
            PlayerPath.remove(PlayerPath.size() - 1);
        }
        if (EnemyPath.get(EnemyPath.size() - 1).tile == Terrain.CYBERTRUCK) {
            enemy.score -= 7;
            enemy.damage = Math.min(enemy.damage + 2, 5);
            enemy.nBoost = 0;
            Cyber.add(EnemyPath.get(EnemyPath.size() - 1));
            EnemyPath.remove(EnemyPath.size() - 1);
        }
        Tile playerFinalTile = PlayerPath.get(PlayerPath.size() - 1).clone();
        Tile enemyFinalTile = EnemyPath.get(EnemyPath.size() - 1).clone();

        // collision resolve
        boolean udah = false;
        if (Supports.isTileEqual(playerFinalTile, enemyFinalTile)) {
            PlayerPath.remove(PlayerPath.size() - 1);
            for (int i = 1; i < PlayerPath.size(); i++) {
                PlayerPath.set(i, new Tile(PlayerPath.get(i).x, PlayerPath.get(0).y));
            }
            EnemyPath.remove(EnemyPath.size() - 1);
            for (int i = 1; i < EnemyPath.size(); i++) {
                EnemyPath.set(i, new Tile(EnemyPath.get(i).x, EnemyPath.get(0).y));
            }
            udah = true;
        }
        for (int i = 1; i < PlayerPath.size() && !udah; i++) {
            Tile playerTile = PlayerPath.get(i);
            if (playerTile.y != enemyFinalTile.y) {
                break;
            }
            if (playerTile.x == enemyFinalTile.x) {
                if (PlayerPath.get(0).x < EnemyPath.get(0).x) {
                    int xa = playerTile.x;
                    int ya = playerTile.y;
                    for (int j = i; j < PlayerPath.size(); j++) {
                        PlayerPath.remove(i);
                    }
                    if (!Supports.sameCoordinate(PlayerPath.get(i - 1), xa - 1, ya)) {
                        PlayerPath.add(ret.map.getTile(xa - 1, ya));
                    }
                    udah = true;
                    break;
                }
            }
        }

        for (int i = 1; i < EnemyPath.size() && !udah; i++) {
            Tile enemyTile = EnemyPath.get(i);
            if (enemyTile.y != playerFinalTile.y) {
                break;
            }
            if (enemyTile.x == playerFinalTile.x) {
                if (EnemyPath.get(0).x < PlayerPath.get(0).x) {
                    int xa = enemyTile.x;
                    int ya = enemyTile.y;
                    for (int j = i; j < EnemyPath.size(); j++) {
                        EnemyPath.remove(i);
                    }
                    if (!Supports.sameCoordinate(EnemyPath.get(i - 1), xa - 1, ya)) {
                        EnemyPath.add(ret.map.getTile(xa - 1, ya));
                    }
                    break;
                }
            }
        }
        // update position
        player.changeLoc(PlayerPath.get(PlayerPath.size() - 1));
        enemy.changeLoc(EnemyPath.get(EnemyPath.size() - 1));

        // resource gathering
        player.getDrops(PlayerPath);
        enemy.getDrops(EnemyPath);

        // update speed
        player.changeSpeed(PlayerAction);
        enemy.changeSpeed(EnemyAction);

        for (Tile cybertrucks : Cyber) {
            ret.map.map[cybertrucks.x][cybertrucks.y].deleteCybertruck();
        }
        return ret;
    }

    public static List<Command> validAction(GlobalState state) {
        List<Command> ret = new ArrayList<>();
        if (state.player.speed > 0) {
            ret.add(Abilities.DO_NOTHING);
            ret.add(Abilities.DECELERATE);
            if (state.player.pos_y != 1) {
                ret.add(Abilities.TURN_LEFT);
            }
            if (state.player.pos_y != 4) {
                ret.add(Abilities.TURN_RIGHT);
            }
            if (state.player.lizard > 0) {
                ret.add(Abilities.LIZARD);
            }
        }
        if (state.player.speed < Supports.getCurrentSpeedLimit(state.player.damage)) {
            ret.add(Abilities.ACCELERATE);
        }
        if (state.player.boost > 0 && state.player.nBoost <= 1) {
            ret.add(Abilities.BOOST);
        }
        return ret;
    }

    public static Command predictAction(GlobalState state) {
        if (state.enemy.pos_x > state.map.nxeff) {
            return Abilities.ACCELERATE;
        }

        if (state.player.pos_x == state.enemy.pos_x - 1
                && state.player.pos_y == state.enemy.pos_y
                && state.enemy.speed == 0) {
            return Abilities.DO_NOTHING;
        }

        return (new Search(state.switch_(),true)).bestActions.get(0);
    }

    public static Command bestAttack(List<Command> Commands, GlobalState curState) {
        // offensive move
        GlobalState state1 = Actions.simulateActions(Commands.get(0), Actions.predictAction(curState), curState);
        GlobalState state2 = null;
        if (Commands.size() > 2) {
            state2 = Actions.simulateActions(Commands.get(1), Actions.predictAction(state1), state1);
        }
        int x = curState.player.pos_x;
        int x1 = curState.enemy.pos_x;
        int y = curState.player.pos_y;
        int y1 = curState.enemy.pos_y;
        if (curState.player.emp > 0) {
            if (x < x1 && Math.abs(y - y1) <= 1) {
                // jangan EMP kalau kita malah nabrak lawan
                if (y == y1) {
                    GlobalState stateCrash = Actions.simulateActions(Abilities.DO_NOTHING, Abilities.DO_NOTHING, curState);
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
                if (!Supports.isCommandEqual(Actions.predictAction(state1), Abilities.FIX)) {
                    // simpen agak jauh dari lawan, biar kasus kalau abs(x-x1)=1 ga terjadi
                    // hati-hati juga, bisa jadi lawan nge-EMP kita pas placing cybertruck
                    int cyber_x = state1.enemy.pos_x + 2;
                    int cyber_y = state2.enemy.pos_y;
                    if (Math.abs(y - y1) <= 1 && cyber_x == x && cyber_y == y) {
                        cyber_x--;
                    }
                    if (cyber_x < state1.player.pos_x) {
                        int prevCyber_x = curState.player.cyber_x;
                        int prevCyber_y = curState.player.cyber_y;
                        if(prevCyber_x!=0){
                            curState.map.getTile(prevCyber_x, prevCyber_y).deleteCybertruck();
                        }
                        curState.map.getTile(cyber_x, cyber_y).setCybertruck();
                        curState.player.changeCyberTruck(cyber_x, cyber_y);
                        return Abilities.TWEET(cyber_y, cyber_x);
                    }
                }
            }
        } else if (curState.player.oil > 0) {
            for (int px = Math.max(1, x - 10); px <= Math.min(1500, x + 10); px++) {
                if (y > 1) {
                    if (curState.map.getTile(px, y - 1).isBad()) {
                        return Abilities.OIL;
                    }
                } else if (y < 4) {
                    if (curState.map.getTile(px, y + 1).isBad()) {
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
