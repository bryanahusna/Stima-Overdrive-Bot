package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.Bot;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.utils.LogState;

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
    public Player(int id){
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
    public void update(GameState curState){
        // update cuma dipake buat player

    }

    public void update(LogState state){
        // update cuma dipake buat opponent
    }

    public void getDrops(List<Tile> Path){
        // update powerup, score, dan damage player berdasarkan Path
    }
}
