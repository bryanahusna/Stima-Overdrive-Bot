package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;

public class Position {
    public Position(int lane, int block){
        this.lane = lane;
        this.block = block;
    }

    @SerializedName("y")
    public int lane;

    @SerializedName("x")
    public int block;
}
