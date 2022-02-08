package za.co.entelect.challenge.entities;

import com.google.gson.annotations.SerializedName;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.State;

public class Car {
    public Car clone(){
        Car car = new Car();
        car.id = id;
        car.position = new Position(position.lane, position.block);
        car.speed = speed;
        car.damage = damage;
        car.state = state;
        PowerUps[] powerups = new PowerUps[this.powerups.length];
        for(int i = 0; i < powerups.length; i++){
            powerups[i] = this.powerups[i];
        }
        car.powerups = powerups;
        car.boosting = boosting;
        car.boostCounter = boostCounter;

        return car;
    }

    @SerializedName("id")
    public int id;

    @SerializedName("position")
    public Position position;

    @SerializedName("speed")
    public int speed;

    @SerializedName("damage")
    public int damage;

    @SerializedName("state")
    public State state;

    @SerializedName("powerups")
    public PowerUps[] powerups;

    @SerializedName("boosting")
    public Boolean boosting;

    @SerializedName("boostCounter")
    public int boostCounter;
}
