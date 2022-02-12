package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.Terrain;
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

    public void update(GameState curState) {
        // update cuma dipake buat player

    }

    public void update(LogState state) {
        // update cuma dipake buat opponent
    }

    public void getDrops(List<Tile> Path) {
        // update powerup, score, speed, dan damage player berdasarkan Path
        for (Tile block : Path) {
            if (block.tile == Terrain.EMPTY)
                continue;
            else if (block.tile == Terrain.MUD || block.tile == Terrain.OIL_SPILL) {
                this.givePenalty(block);
                this.speed = Math.min(
                        Supports.getCurrentSpeedLimit(this.damage),
                        Supports.getDeceleratedSpeed(this.speed, true)
                );
            } else if (block.tile == Terrain.WALL) {
                this.givePenalty(block);
                this.speed = Math.min(
                        Supports.getCurrentSpeedLimit(this.damage),
                        Supports.getDeceleratedSpeed(this.speed, false)
                );
            } else if (block.tile == Terrain.OIL_POWER) {
                this.oil++;
            } else if (block.tile == Terrain.BOOST) {
                this.boost++;
            } else if (block.tile == Terrain.LIZARD) {
                this.lizard++;
            } else if (block.tile == Terrain.TWEET) {
                this.tweet++;
            } else if (block.tile == Terrain.EMP) {
                this.emp++;
            }
        }
    }

    private void givePenalty(Tile block) {
        if (block.tile == Terrain.MUD) {
            this.score -= 3;
            this.damage += 1;
        } else if (block.tile == Terrain.OIL_SPILL) {
            this.score -= 4;
            this.damage += 1;
        } else if (block.tile == Terrain.WALL) {
            this.score -= 5;
            this.damage += 2;
        } else if (block.tile == Terrain.CYBERTRUCK) {
            this.score -= 7;
            this.damage += 2;
        }

    }
}
