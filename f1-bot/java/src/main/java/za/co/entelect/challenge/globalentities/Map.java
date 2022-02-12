package za.co.entelect.challenge.globalentities;

import java.util.List;

import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.Terrain;

public class Map {
    // isi entire map disini
    // refactor it if necessary
    private final int x_size= 1500;
    private final int y_size = 4;
    public int nxeff;
    public Tile[][] map;

    public Map(){
        this.map =  new Tile[x_size][y_size];
        nxeff = 0;
        for(int i = 1; i <= x_size; i++){
            for(int j = 1; j <= y_size; j++){
                this.map[i - 1][j - 1] = new Tile(i, j);
            }
        }
    }

    public Map(Map m){
        this.map =  new Tile[x_size][y_size];
        for(int i = 1; i <= x_size; i++){
            for(int j = 1; j <= y_size; j++){
                this.map[i - 1][j - 1] = m.getTile(i, j);
            }
        }
        this.nxeff = m.nxeff;
    }

    public Map clone(){
        Map m = new Map();
        for(int i = 1; i <= x_size; i++){
            for(int j = 1; j <= y_size; j++){
                m.map[i - 1][j - 1] = this.getTile(i, j);
            }
        }
        m.nxeff = this.nxeff;
        return m;
    }

    public void setTile(int x, int y, Tile t){
        this.map[x - 1][y - 1] = t.clone();
    }

    public Tile getTile(int x, int y){
        return (this.map[x - 1][y - 1]).clone();
    }

    public void updateNewRound(GameState curState){
        List<Lane[]> lanes = curState.lanes;
        for(Lane[] lintasan : lanes){
            for(Lane l : lintasan){
                Tile temp = new Tile(l.position.block, l.position.lane);
                temp.tile = l.terrain;
                if(l.occupiedByCyberTruck){
                    temp.layer = Terrain.CYBERTRUCK;
                }
                this.map[l.position.block - 1][l.position.lane - 1] = temp;
            }
        }
    }
}
