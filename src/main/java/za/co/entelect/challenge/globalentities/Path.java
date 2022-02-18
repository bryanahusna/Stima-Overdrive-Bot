package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Supports;

import java.util.ArrayList;
import java.util.List;

public class Path {
    /* Menyimpan lintasan yang dilalui oleh player/opponent */
    public int x = 0;
    public int y = 0;
    public int dx = 0;
    public int dy = 0;
    public int v = 0;
    public int damage = 0;
    public boolean collided = false;

    public void updateCoor(Player player){
        Player p = player.clone();
        this.x = p.pos_x;
        this.y = p.pos_y;
    }

    public void updatePath(Command cmd, Player player) {
        // Mirip user-defined constructor
        Player p = player.clone();
        this.damage = p.damage;
        this.collided = false;
        if (Supports.isCommandEqual(cmd, Abilities.ACCELERATE)) {
            this.v = Supports.getAcceleratedSpeed(p.speed, p.damage);
            this.dx += this.v;
        } else if (Supports.isCommandEqual(cmd, Abilities.DECELERATE)) {
            this.v = Supports.getDeceleratedSpeed(p.speed, false, p.damage);
            this.dx += v;
        } else if (Supports.isCommandEqual(cmd, Abilities.TURN_RIGHT)) {
            if (p.speed > 0) {
                this.dx += p.speed - 1;
                this.dy++;
            }
            this.v = p.speed;
        } else if (Supports.isCommandEqual(cmd, Abilities.TURN_LEFT)) {
            if (p.speed > 0) {
                this.dx += p.speed - 1;
                this.dy--;
            }
            this.v = p.speed;
        } else if (Supports.isCommandEqual(cmd, Abilities.DO_NOTHING)) {
            this.dx += p.speed;
            this.v = p.speed;
        } else if (Supports.isCommandEqual(cmd, Abilities.BOOST)) {
            this.v = Supports.getBoostedSpeed(p.damage);
            this.dx += this.v;
        } else if (Supports.isCommandEqual(cmd, Abilities.LIZARD)) {
            this.dx += p.speed;
            this.v = p.speed;
        }
    }

    public void resolveCollision(Path other, Command cmd, Command cmdOther) {
        /* Kalau menabrak mobil lain,
        lintasannya di-update di sini -> makanya void :-) */
        boolean lizard1 = Supports.isCommandEqual(cmd, Abilities.LIZARD);
        boolean lizard2 = Supports.isCommandEqual(cmdOther, Abilities.LIZARD);
        boolean sameY = (this.dy + this.y == other.y + other.dy);
        boolean sameX = (this.dx + this.x == other.x + other.dx);
        boolean crasher = (this.dx + this.x > other.x + other.dx) && (this.x < other.x);
        boolean crashed = (this.dx + this.x < other.x + other.dx) && (this.x > other.x);
        boolean meetPath = (sameY && (crasher || crashed));
        boolean normalCollide = (lizard1 || lizard2) && sameX && sameY;
        normalCollide = (normalCollide || meetPath);
        if (normalCollide) {
            if (crasher) {
                this.dx = other.x + other.dx - 1 - this.x;
                this.collided = true;
            } else {
                other.dx = this.x + this.dx - 1 - other.x;
                other.collided = true;
            }
        }

        if (sameX && sameY) {
            this.dx--;
            this.dy = 0;
            other.dx--;
            other.dy = 0;
            this.collided = true;
            other.collided = true;
        }
    }

    public List<Tile> allPath(Map globe, Command cmd) {
        /* Mereturn block-block Terrain/Tile yang dilalui dalam lintasan tersebut
         * Sama seperti getBlocks di starter bot */
        List<Tile> paths = new ArrayList<>();
        if (this.dy == this.dx && this.dx == 0) {
            return paths;
        } else if (Supports.isCommandEqual(cmd, Abilities.LIZARD) && this.x + this.dx < 1500) {
            paths.add(globe.getTile(this.x + this.dx, this.y + this.dy));
            return paths;
        } else {
            for (int i = (this.collided ? this.x + 1 : this.x); i <= this.x + this.dx; i++) {
                if (this.x + this.dx >= 1500) {
                    break;
                } else {
                    paths.add(globe.getTile(i, this.y + this.dy));
                }
            }
            return paths;
        }
    }

    public Path clone() {
        Path clone = new Path();
        clone.x = this.x;
        clone.y = this.y;
        clone.dx = this.dx;
        clone.dy = this.dy;
        clone.v = this.v;
        clone.damage = this.damage;
        clone.collided = this.collided;
        return clone;
    }
}
