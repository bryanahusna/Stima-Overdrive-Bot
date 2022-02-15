package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Actions;
import za.co.entelect.challenge.utils.LogState;
import za.co.entelect.challenge.utils.Supports;


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

    }

    public void update(GameState curState, int id) {
        if(id==1) {
            this.pos_x = curState.player.position.block;
            this.pos_y = curState.player.position.lane;
            this.speed = curState.player.speed;
            this.damage = curState.player.damage;
            this.boost = 0;
            this.emp = 0;
            this.lizard = 0;
            this.tweet = 0;
            this.oil = 0;
            for (PowerUps p : curState.player.powerups) {
                if (p == PowerUps.BOOST) {
                    this.boost++;
                } else if (p == PowerUps.EMP) {
                    this.emp++;
                } else if (p == PowerUps.LIZARD) {
                    this.lizard++;
                } else if (p == PowerUps.OIL) {
                    this.oil++;
                } else if (p == PowerUps.TWEET) {
                    this.tweet++;
                }
            }
        }
        else{
            this.pos_x = curState.opponent.position.block;
            this.pos_y = curState.opponent.position.lane;
            this.speed = curState.opponent.speed;
        }
    }

    public void update(LogState state, Map globe) {

//        Cek kalo misalnya kita ketinggalan / salah prediksi lawan.
//        Kalo offset / speed akhir mereka lebih daripada yang seharusnya,
//        Kurangi damage sampai logic valid


//        bikin simulasi dulu
        Map sim = globe.clone();
        int x_enemy = state.prevState.enemy.pos_x;
        int x_player = state.prevState.player.pos_x;
        int xf_enemy = state.currentState.enemy.pos_x;
        if(x_enemy <= x_player){
            sim.createSimulation(x_enemy, xf_enemy);
        }
        int offsetX = state.currentState.enemy.pos_x - state.prevState.enemy.pos_x
                + Math.abs(state.prevState.enemy.pos_y - state.currentState.enemy.pos_y);
        int oppSpeed = state.currentState.enemy.speed;
        int damage = Math.max(state.prevState.enemy.damage, 0);
        while (Math.max(offsetX, oppSpeed) > Supports.getBoostedSpeed(damage)
                && damage > 0) {
            damage--;
        }
        state.prevState.enemy.damage = damage; // Update damage

//        Hitung COMMAND lawan
        Command cmd = state.calcOpponentCommand(sim);
        Player opp;
        if (cmd == null) {
//        either EMP-ed, atau emg algonya ga jalan (tapi harusnya less likely sih)
            opp = state.prevState.enemy.clone();
            this.boost = opp.boost;
            this.oil = opp.oil;
            this.tweet = opp.tweet;
            this.lizard = opp.lizard;
            this.emp = opp.emp;
            this.nBoost = opp.nBoost;
            this.damage = opp.damage;
            this.score = opp.score;
        }
        else{
            GlobalState predResource = Actions.simulateActions(
                    Abilities.convertOffensive(state.action),
                    cmd, state.prevState, sim
            );
            opp = predResource.enemy.clone();
            this.oil = opp.oil;
            this.tweet = opp.tweet;
            this.emp = opp.emp;
            this.nBoost = opp.nBoost;
            this.score = opp.score;
            this.damage = opp.damage;
            this.lizard = Math.max(opp.lizard, 0);
            this.boost = Math.max(opp.boost, 0);
            if(predResource.isSomethingDeleted(1)){
                globe.map[predResource.pref_x1-1][predResource.pref_y1-1].deleteCybertruck();
            }
            else if(predResource.isSomethingDeleted(2)){
                globe.map[predResource.pref_x2-1][predResource.pref_y2-1].deleteCybertruck();
            }
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
        return clone;
    }


    public void updatePos(Path P) {
        Path pos = P.clone();
        this.pos_x += pos.dx;
        this.pos_y += pos.dy;
        this.speed = pos.v;
    }

    public void updateResouce(Resource drops){
        this.oil += drops.oil;
        this.lizard += drops.lizard;
        this.tweet += drops.tweet;
        this.emp += drops.emp;
        this.boost += drops.boost;
        this.score += drops.score;
        this.damage = Math.min(5, this.damage + drops.damage);
        if(drops.bad > 0){
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

    public void printAll(){
        System.out.println("id: "+this.id);
        System.out.println("pos_x: "+this.pos_x);
        System.out.println("pos_y: " + this.pos_y);
        System.out.println("speed: " +this.speed);
        System.out.println("damage: "+this.damage);
        System.out.println("boost: "+this.boost);
        System.out.println("oil: "+this.oil);
        System.out.println("tweet: "+this.tweet);
        System.out.println("lizard: "+this.lizard);
        System.out.println("emp: "+this.emp);
        System.out.println("boost: "+this.nBoost);
        System.out.println("score: "+this.score);
    }
}
