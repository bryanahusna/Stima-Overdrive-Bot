package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.entities.GameState;

public class Map {
    // isi entire map disini
    // refactor it if necessary
    private final int x_size= 1500;
    private final int y_size = 4;
    public Tile[][] map;
    public Map(){
        this.map =  new Tile[x_size][y_size];
        for(int i=0; i<x_size; i++){
            for(int j=0; j<y_size; j++){
                this.map[i][j] = new Tile(i, j);
            }
        }

    }
    public Tile getTile(int x, int y){
        return this.map[x][y];
    }
    public void update(GameState curState){
        // Update setiap nemu view baru
    }

}
