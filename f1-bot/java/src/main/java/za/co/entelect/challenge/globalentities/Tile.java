package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.enums.Terrain;

public class Tile {
    // Storing object tiap tile
    public int x;
    public int y;
    public Terrain tile; // original tile
    public Terrain layer; // atasan tile

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        tile = Terrain.EMPTY;
        layer = Terrain.EMPTY;
    }

    public Tile clone() {
        Tile t = new Tile(this.x, this.y);
        t.tile = this.tile;
        t.layer = this.layer;

        return t;
    }

    public boolean isBad(){
        if(this.layer==Terrain.CYBERTRUCK){
            return true;
        }
        return this.tile==Terrain.MUD||this.tile==Terrain.WALL||this.tile==Terrain.OIL_SPILL;
    }

    // equals true jika tile dan layernya sama
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Tile))
            return false;
        Tile t = (Tile) o;

        return t.tile == this.tile && t.layer == this.layer;
    }

    public void setCybertruck() {
        this.layer = Terrain.CYBERTRUCK;
    }

    public void deleteCybertruck() {
        this.layer = Terrain.EMPTY;
    }
}
