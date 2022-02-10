package za.co.entelect.challenge.entities;

import za.co.entelect.challenge.enums.Terrain;

public class Map {
    // isi entire map disini
    // refactor it if necessary
    private final int x_size= 1500;
    private final int y_size = 4;
    public Tile[][] map;
    public Map(){
        map =  new Tile[x_size][y_size];
    }

    public void Update(){
        // Update setiap nemu view baru
    }

}
