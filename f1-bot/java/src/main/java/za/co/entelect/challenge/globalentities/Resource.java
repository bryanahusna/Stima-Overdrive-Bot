package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.constants.utils.Supports;

import java.util.ArrayList;
import java.util.List;

public class Resource {
    /* Menyimpan drop yang dilewati di dalam path */
    public int oil = 0;
    public int boost = 0;
    public int lizard = 0;
    public int tweet = 0;
    public int emp = 0;
    public int damage = 0;
    public int score = 0;
    public int bad = 0;
    public List<Tile> cyberPos = new ArrayList<>();

    public void getResource(Tile tile, Path path) {
        Tile T = tile.clone();
        this.givePenalty(tile, path);
        if (T.tile == Terrain.OIL_POWER) {
            this.oil++;
            this.score += 4;
        } else if (T.tile == Terrain.BOOST) {
            this.boost++;
            this.score += 4;
        } else if (T.tile == Terrain.LIZARD) {
            this.lizard++;
            this.score += 4;
        } else if (T.tile == Terrain.TWEET) {
            this.tweet++;
            this.score += 4;
        } else if (T.tile == Terrain.EMP) {
            this.emp++;
            this.score += 4;
        }
    }

    private void givePenalty(Tile block, Path path) {
        if (block.tile == Terrain.MUD) {
            this.score -= 3;
            this.damage++;
            this.bad++;
            path.v = Supports.getDeceleratedSpeed(path.v, true, path.damage);
        } else if (block.tile == Terrain.OIL_SPILL) {
            this.score -= 4;
            this.damage++;
            this.bad++;
            path.v = Supports.getDeceleratedSpeed(path.v, true, path.damage);
        } else if (block.tile == Terrain.WALL) {
            this.score -= 5;
            this.damage += 2;
            this.bad++;
            path.v = Supports.getDeceleratedSpeed(path.v, false, path.damage);
        } else if (block.layer == Terrain.CYBERTRUCK) {
            this.score -= 7;
            this.damage += 2;
            this.bad++;
            path.v = 3;
            path.dx = block.x - path.x - 1;
        }
    }

    public Resource(Map globe, Path path, Command cmd, boolean cyber) {
        Path pos = path.clone();
        List<Tile> paths = pos.allPath(globe, cmd);
        if (cyber) {
            for (Tile T : paths) {
                if (T.layer == Terrain.CYBERTRUCK) {
                    this.givePenalty(T, path);
                    this.cyberPos.add(T.clone());
                    break;
                }
            }
        } else {
            for (Tile T : paths) {
                this.getResource(T, path);
            }
        }
    }
}
