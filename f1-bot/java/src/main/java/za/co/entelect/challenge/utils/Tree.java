package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.globalentities.Player;
import za.co.entelect.challenge.globalentities.Tile;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    public static GlobalState SimulateActions(Command PlayerAction, Command EnemyAction, GlobalState InitState){
        GlobalState ret = InitState.clone();
        Player player = ret.player;
        Player enemy = ret.enemy;
        if(player.nBoost > 0){
            player.nBoost--;
            if(player.nBoost==0){
                player.speed = Supports.getCurrentSpeedLimit(player.damage);
            }
        }
        if(enemy.nBoost > 0){
            enemy.nBoost--;
            if(enemy.nBoost==0){
                enemy.speed = Supports.getCurrentSpeedLimit(enemy.damage);
            }
        }
        // calculate path sampe tile == cybertruck atau selesai
        List<Tile> PlayerPath = Supports.getPath(player.pos_x, player.pos_y, player.speed, player.damage, PlayerAction, ret);
        List<Tile> EnemyPath = Supports.getPath(enemy.pos_x, enemy.pos_y, enemy.speed, enemy.damage, EnemyAction, ret);
        List<Tile> Cyber = new ArrayList<Tile>();

        if(Supports.isCommandEqual(PlayerAction, Abilities.FIX)){
            player.damage = Math.max(0, player.damage-2);
        }
        else if(Supports.isCommandEqual(PlayerAction, Abilities.BOOST)){
            player.boost -= 1;
            player.nBoost = 5;
            player.score += 4;
        }
        else if(Supports.isCommandEqual(PlayerAction, Abilities.LIZARD)){
            player.lizard -= 1;
            player.score += 4;
        }

        if(Supports.isCommandEqual(EnemyAction, Abilities.FIX)){
            enemy.damage = Math.max(0, enemy.damage-2);
        }
        else if(Supports.isCommandEqual(EnemyAction, Abilities.BOOST)){
            enemy.boost -= 1;
            enemy.nBoost = 5;
            enemy.score += 4;
        }
        else if(Supports.isCommandEqual(EnemyAction, Abilities.LIZARD)){
            enemy.lizard -= 1;
            enemy.score += 4;
        }

        if(PlayerPath.get(PlayerPath.size()-1).tile == Terrain.CYBERTRUCK){
            player.score -= 7;
            player.damage = Math.min(player.damage+2, 5);
            player.nBoost = 0;
            Cyber.add(PlayerPath.get(PlayerPath.size()-1));
            PlayerPath.remove(PlayerPath.size()-1);
        }
        if(EnemyPath.get(EnemyPath.size()-1).tile == Terrain.CYBERTRUCK){
            enemy.score -= 7;
            enemy.damage = Math.min(enemy.damage+2, 5);
            enemy.nBoost = 0;
            Cyber.add(EnemyPath.get(EnemyPath.size()-1));
            EnemyPath.remove(EnemyPath.size()-1);
        }
        Tile playerFinalTile = PlayerPath.get(PlayerPath.size()-1);
        Tile enemyFinalTile = EnemyPath.get(EnemyPath.size()-1);

        // collision resolve
        boolean udah = false;
        if(Supports.isTileEqual(playerFinalTile, enemyFinalTile)){
            PlayerPath.remove(PlayerPath.size()-1);
            for(int i=1; i<PlayerPath.size(); i++){
                PlayerPath.set(i, new Tile(PlayerPath.get(i).x, PlayerPath.get(0).y));
            }
            EnemyPath.remove(EnemyPath.size()-1);
            for(int i=1; i<EnemyPath.size(); i++){
                EnemyPath.set(i, new Tile(EnemyPath.get(i).x, EnemyPath.get(0).y));
            }
            udah = true;
        }
        for(int i=1; i<PlayerPath.size()&&!udah; i++){
            Tile playerTile = PlayerPath.get(i);
            if (playerTile.y!=enemyFinalTile.y) {
                break;
            }
            if(playerTile.x == enemyFinalTile.x){
                if(PlayerPath.get(0).x < EnemyPath.get(0).x){
                    int xa = playerTile.x;
                    int ya = playerTile.y;
                    for(int j=i; j<PlayerPath.size(); j++){
                        PlayerPath.remove(i);
                    }
                    if(!Supports.sameCoordinate(PlayerPath.get(i-1), xa-1, ya)){
                        PlayerPath.add(ret.map.getTile(xa-1, ya));
                    }
                    udah = true;
                    break;
                }
            }
        }

        for(int i=1; i<EnemyPath.size()&&!udah; i++){
            Tile enemyTile = EnemyPath.get(i);
            if (enemyTile.y!=playerFinalTile.y) {
                break;
            }
            if(enemyTile.x == playerFinalTile.x){
                if(EnemyPath.get(0).x < PlayerPath.get(0).x){
                    int xa = enemyTile.x;
                    int ya = enemyTile.y;
                    for(int j=i; j<EnemyPath.size(); j++){
                        EnemyPath.remove(i);
                    }
                    if(!Supports.sameCoordinate(EnemyPath.get(i-1), xa-1, ya)){
                        EnemyPath.add(ret.map.getTile(xa-1, ya));
                    }
                    break;
                }
            }
        }
        // update position
        player.changeLoc(PlayerPath.get(PlayerPath.size()-1));
        player.changeLoc(EnemyPath.get(EnemyPath.size()-1));

        // resource gathering
        player.getDrops(PlayerPath);
        enemy.getDrops(EnemyPath);

        // update speed
        player.changeSpeed(PlayerAction);
        enemy.changeSpeed(EnemyAction);

        for(Tile cybertrucks: Cyber){
            ret.map.map[cybertrucks.x][cybertrucks.y].eraseLayer();
        }
        return ret;
    }

}
