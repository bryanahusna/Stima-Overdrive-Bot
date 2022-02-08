package za.co.entelect.challenge.algorithm;

import java.util.ArrayList;
import java.util.List;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Supports;

/* CARA PAKAI:
    1. Buat object baru dari class Scoring, dengan list commands (terurut) yang diinginkan dan gameState sekarang
    2. Jalankan method calculate, akan menghasilkan skornya
    
    Return INVALID_COMMAND jika ada command yang tidak valid (misal menggunakan boost ketika tidak punya boost) */
public class Scoring {
    public static final float INVALID_COMMAND = -9999999;
    public List<Command> commands;
    public GameState gameState;

    public Scoring(List<Command> commands, GameState gameState){
        this.gameState = gameState;
        this.commands = commands;
    }

    public float calculate(){
        Car initialCar = gameState.player;
        Car currentCar = initialCar.clone();
        for(Command cmd : commands){
            
        }

        return INVALID_COMMAND;
    }

    public static Car simulate(Car initialCar, GameState gameState, Command cmd){
        Car endCar = initialCar.clone();
        List<Lane[]> lanes = gameState.lanes;
        if(Supports.isCommandEqual(cmd, Abilities.ACCELERATE)){
            List<Terrain> terrains = Supports.getBlocks(initialCar.position.lane,
                                                        initialCar.position.block + 1,
                                                        Supports.getAcceleratedSpeed(initialCar.speed, initialCar.damage),
                                                        lanes);
            
        }

        return endCar;
    }

    /* PROSEDUR: F.S.: endCar mendapatkan efek dari terrain yang dilaluinya */
    public static void simulate(Car endCar, List<Terrain> terrains){
        endCar.position.block += terrains.size();
        List<PowerUps> newPowers = new ArrayList<>();
        for(Terrain terrain : terrains){
            if(terrain == Terrain.MUD){
                endCar.damage += 1;
                endCar.speed = Supports.getDeceleratedSpeed(endCar.speed, true);
            } else if(terrain == Terrain.OIL_SPILL){
                endCar.damage += 1;
                endCar.speed = Supports.getDeceleratedSpeed(endCar.speed, true);
            } else if(terrain == Terrain.OIL_POWER){
                newPowers.add(PowerUps.OIL);
            } else if(terrain == Terrain.BOOST){
                newPowers.add(PowerUps.BOOST);
            } else if(terrain == Terrain.WALL){
                endCar.damage += 2;
                endCar.speed = 3;
            } else if(terrain == Terrain.LIZARD){
                newPowers.add(PowerUps.LIZARD);
            } else if(terrain == Terrain.TWEET){
                newPowers.add(PowerUps.TWEET);
            } else if(terrain == Terrain.EMP){
                newPowers.add(PowerUps.EMP);
            } else if(terrain == Terrain.CYBERTRUCK){
                endCar.damage += 2;
                endCar.speed = 3;
            }
        }
        // TODO: menambahkan power up yang baru
        endCar.damage = Math.min(endCar.damage, 5);
        endCar.speed = Math.min(endCar.speed, Supports.getCurrentSpeedLimit(endCar.damage));
    }
}

// @SerializedName("0")
//     EMPTY,
//     @SerializedName("1")
//     MUD,
//     @SerializedName("2")
//     OIL_SPILL,
//     @SerializedName("3")
//     OIL_POWER,
//     @SerializedName("4")
//     FINISH,
//     @SerializedName("5")
//     BOOST,
//     @SerializedName("6")
//     WALL,
//     @SerializedName("7")
//     LIZARD,
//     @SerializedName("8")
//     TWEET,
//     @SerializedName("9")
//     EMP,
//     @SerializedName("10")
//     CYBERTRUCK,
//     @SerializedName("11")
//     ENEMY,