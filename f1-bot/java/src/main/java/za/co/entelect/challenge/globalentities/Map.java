package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.entities.GameState;

public class Map {
    // isi entire map disini
    // refactor it if necessary
    private final int x_size= 1500;
    private final int y_size = 4;
    public Tile[][] map;
    public Map(){
        map =  new Tile[x_size][y_size];
    }
    public Tile getTile(int x, int y){
        return this.map[x][y];
    }
    public void update(GameState curState){
        // Update setiap nemu view baru
    }

}
