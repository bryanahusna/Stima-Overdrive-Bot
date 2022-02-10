package za.co.entelect.challenge.entities;

import za.co.entelect.challenge.enums.Terrain;

public class Tile {
    // Storing object tiap tile
    public Terrain tile; // original tile
    public Terrain layer; // atasan tile
    public Tile(){
        tile = Terrain.EMPTY;
        layer = Terrain.EMPTY;
    }
    public void set(Terrain tile, Terrain layer) {
        // setter
    }
}
