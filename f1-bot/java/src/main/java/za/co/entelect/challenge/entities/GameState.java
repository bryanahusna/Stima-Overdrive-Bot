package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.enums.Terrain;

import java.util.List;
import java.util.Queue;

public class GameState {
    public void cybertruckLaneToTerrain(){
        for(int i = 0; i < lanes.size(); i++){
            for(int j = 0; j < lanes.get(0).length; j++){
                if(lanes.get(i)[j].occupiedByCyberTruck){
                    lanes.get(i)[j].terrain = Terrain.CYBERTRUCK;
                }
            }
        }
    }

    @SerializedName("currentRound")
    public int currentRound;

    @SerializedName("maxRounds")
    public int maxRounds;

    @SerializedName("player")
    public Car player;

    @SerializedName("opponent")
    public Car opponent;

    @SerializedName("worldMap")
    public List<Lane[]> lanes;

    public Map currentMap;
    public void setMap(Map map){
        this.currentMap = map;
    }
}
