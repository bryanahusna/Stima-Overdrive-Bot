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

    public void set(Terrain tile, Terrain layer) {
        // setter
    }
}
