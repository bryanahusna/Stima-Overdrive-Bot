package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Actions;
import za.co.entelect.challenge.utils.LogState;
import za.co.entelect.challenge.utils.Supports;

import java.util.List;

public class Player {
    public int id;
    public int pos_x;
    public int pos_y;
    public int speed;
    public int damage;
    public int boost;
    public int oil;
    public int tweet;
    public int lizard;
    public int emp;
    public int nBoost; // if boost doesnt activate, dia 0
    public int score;
    public int cyber_x;
    public int cyber_y;

    public Player(int id) {
        // id 1 means player
        // id 2 means opponent
        this.id = id;
        this.pos_x = 0;
        this.pos_y = 0;
        this.speed = 0;
        this.damage = 0;
        this.boost = 0;
        this.oil = 0;
        this.tweet = 0;
        this.lizard = 0;
        this.emp = 0;
        this.nBoost = 0;
        this.score = 0;
        this.cyber_x = 0;
        this.cyber_y = 0;
    }

    public void update(GameState curState) {
        // update cuma dipake buat player
        this.pos_x = curState.player.position.block;
        this.pos_y = curState.player.position.lane;
        this.speed = curState.player.speed;
        this.damage = curState.player.damage;
        this.boost = 0;
        this.emp = 0;
        this.lizard = 0;
        this.tweet = 0;
        this.oil = 0;
        for(PowerUps p: curState.player.powerups){
            if(p==PowerUps.BOOST){
                this.boost++;
            }
            else if(p==PowerUps.EMP){
                this.emp++;
            }
            else if(p==PowerUps.LIZARD){
                this.lizard++;
            }
            else if(p==PowerUps.OIL){
                this.oil++;
            }
            else if(p==PowerUps.TWEET){
                this.tweet++;
            }
        }
    }

    public void update(LogState state) {
        // Update cuma dipake buat opponent
        if (state.prevState.enemy.pos_x >= state.prevState.player.pos_x) {
            // CLEAN MAP
        }

        /* Cek kalo misalnya kita ketinggalan / salah prediksi lawan.
         *  Kalo offset / speed akhir mereka lebih daripada yang seharusnya,
         *  Kurangi damage sampai logic valid
         * */
        int offsetX = state.currentState.enemy.pos_x - state.prevState.enemy.pos_x
                + Math.abs(state.prevState.enemy.pos_y - state.currentState.enemy.pos_y);
        int oppSpeed = state.currentState.enemy.speed;
        int damage = Math.max(state.prevState.enemy.damage, 0);
        while (Math.max(offsetX, oppSpeed) > Supports.getBoostedSpeed(damage)
                && damage > 0) {
            damage--;
        }
        state.prevState.enemy.damage = damage; // Update damage

        /* Hitung COMMAND lawan */
        Command cmd = state.calcOpponentCommand();
        Player opp;
        if (cmd == null) {
            // either EMP-ed, atau emg algonya ga jalan (tapi harusnya less likely sih
            opp = state.prevState.enemy.clone();
            this.id = opp.id;
            this.pos_x = opp.pos_x;
            this.pos_y = opp.pos_y;
            this.speed = opp.speed;
            this.damage = opp.damage;
            this.boost = opp.boost;
            this.oil = opp.oil;
            this.tweet = opp.tweet;
            this.lizard = opp.lizard;
            this.emp = opp.emp;
            this.nBoost = opp.nBoost;
            this.score = opp.score;
        }
        else{
            GlobalState predOppNS = Actions.simulateActions(
                    Abilities.convertOffensive(state.action),
                    cmd, state.prevState
            );
            opp = predOppNS.enemy;
            opp.lizard = Math.max(opp.lizard, 0);
            opp.boost = Math.max(opp.boost, 0);

            // Move calculated opp to this
            this.id = opp.id;
            this.pos_x = opp.pos_x;
            this.pos_y = opp.pos_y;
            this.speed = opp.speed;
            this.damage = opp.damage;
            this.boost = opp.boost;
            this.oil = opp.oil;
            this.tweet = opp.tweet;
            this.lizard = opp.lizard;
            this.emp = opp.emp;
            this.nBoost = opp.nBoost;
            this.score = opp.score;
        }


    }

    public Player clone() {
        Player clone = new Player(this.id);
        clone.id = this.id;
        clone.pos_x = this.pos_x;
        clone.pos_y = this.pos_y;
        clone.speed = this.speed;
        clone.damage = this.damage;
        clone.boost = this.boost;
        clone.oil = this.oil;
        clone.tweet = this.tweet;
        clone.lizard = this.lizard;
        clone.emp = this.emp;
        clone.nBoost = this.nBoost;
        clone.score = this.score;
        clone.cyber_x = this.cyber_y;
        clone.cyber_y = this.cyber_y;
        return clone;
    }

    public void changeCyberTruck(int x, int y){
        this.cyber_x = x;
        this.cyber_y = y;
    }
    public void changeSpeed(Command PlayerAction) {
        if (Supports.isCommandEqual(PlayerAction, Abilities.ACCELERATE)) {
            this.speed = Supports.getAcceleratedSpeed(this.speed, this.damage);
        } else if (Supports.isCommandEqual(PlayerAction, Abilities.DECELERATE)) {
            this.nBoost = 0;
            this.speed = Supports.getDeceleratedSpeed(this.speed, false, this.damage);
        } else if (Supports.isCommandEqual(PlayerAction, Abilities.BOOST)) {
            this.speed = Supports.getBoostedSpeed(this.damage);
        }
    }

    public void changeLoc(Tile T) {
        this.pos_x = T.x;
        this.pos_y = T.y;
    }

    public void getDrops(List<Tile> Path) {
        // update powerup, score, speed, dan damage player berdasarkan Path
        for (Tile block : Path) {
            if (block.tile == Terrain.EMPTY)
                continue;
            else if (block.tile == Terrain.MUD || block.tile == Terrain.OIL_SPILL) {
                this.givePenalty(block);
                this.speed = Supports.getDeceleratedSpeed(this.speed, true, this.damage);
            } else if (block.tile == Terrain.WALL) {
                this.givePenalty(block);
                this.speed = Supports.getDeceleratedSpeed(this.speed, false, this.damage);
            } else if (block.tile == Terrain.OIL_POWER) {
                this.oil++;
                this.score += 4;
            } else if (block.tile == Terrain.BOOST) {
                this.boost++;
                this.score += 4;
            } else if (block.tile == Terrain.LIZARD) {
                this.lizard++;
                this.score += 4;
            } else if (block.tile == Terrain.TWEET) {
                this.tweet++;
                this.score += 4;
            } else if (block.tile == Terrain.EMP) {
                this.emp++;
                this.score += 4;
            }
        }
    }

    private void givePenalty(Tile block) {
        if (block.tile == Terrain.MUD) {
            this.score -= 3;
            this.damage += 1;
            this.nBoost = 0;
        } else if (block.tile == Terrain.OIL_SPILL) {
            this.score -= 4;
            this.damage += 1;
            this.nBoost = 0;
        } else if (block.tile == Terrain.WALL) {
            this.score -= 5;
            this.damage += 2;
            this.nBoost = 0;
        } else if (block.tile == Terrain.CYBERTRUCK) {
            this.score -= 7;
            this.damage += 2;
            this.nBoost = 0;
        }

    }

    public void getFromAction(Command PlayerAction) {
        if (Supports.isCommandEqual(PlayerAction, Abilities.FIX)) {
            this.damage = Math.max(0, this.damage - 2);
        } else if (Supports.isCommandEqual(PlayerAction, Abilities.BOOST)) {
            this.boost -= 1;
            this.nBoost = 5;
            this.score += 4;
        } else if (Supports.isCommandEqual(PlayerAction, Abilities.LIZARD)) {
            this.lizard -= 1;
            this.score += 4;
        }
    }
}
