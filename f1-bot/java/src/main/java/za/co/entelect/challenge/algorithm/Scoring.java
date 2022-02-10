package za.co.entelect.challenge.algorithm;

import java.util.ArrayList;
import java.util.List;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.constants.Weights;
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
            simulate(currentCar, this.gameState, cmd);
        }
        
        return multiplyWeights(initialCar, currentCar);
        //return INVALID_COMMAND;
    }

    public static float multiplyWeights(Car initialCar, Car finalCar){
        float score = (finalCar.position.block - initialCar.position.block) * Weights.POSITION;
        score += (finalCar.speed - initialCar.speed) * Weights.SPEED;
        score += (finalCar.damage - initialCar.damage) * Weights.DAMAGE;
        int[] fpowers = new int[5];
        int[] ipowers = new int[5];
        for(PowerUps p : finalCar.powerups){
            fpowers[p.ordinal()] += 1;
        }
        for(PowerUps p : initialCar.powerups){
            ipowers[p.ordinal()] += 1;
        }
        score += (fpowers[0] - ipowers[0]) * Weights.BOOST;
        score += (fpowers[1] - ipowers[1]) * Weights.OIL;
        score += (fpowers[2] - ipowers[2]) * Weights.TWEET;
        score += (fpowers[3] - ipowers[3]) * Weights.LIZARD;
        score += (fpowers[4] - ipowers[4]) * Weights.EMP;

        return score;
    }

    /* PROSEDUR Menyimulasikan car menerima command
     * F.S.: car berubah mendapatkan efek setelah menjalankan command cmd */
    public static void simulate(Car car, GameState gameState, Command cmd){
        List<Lane[]> lanes = gameState.lanes;
        // TODO:
        // Jika mengambil block melebihi map
        // Jika belok kiri/kanan sementara berada di paling atas/bawah
        // Mengurangi powerup lizard ketika digunakan
        if(Supports.isCommandEqual(cmd, Abilities.ACCELERATE)){
            car.speed = Supports.getAcceleratedSpeed(car.speed, car.damage);
            car.position.block += car.speed;
            List<Terrain> terrains = Supports.getBlocks(car.position.lane,
                                                        car.position.block + 1,
                                                        car.speed,
                                                        lanes);
            simulateBlocks(car, terrains);
        } else if(Supports.isCommandEqual(cmd, Abilities.BOOST)){
            car.speed = Supports.getBoostedSpeed(car.damage);
            car.position.block += car.speed;
            List<Terrain> terrains = Supports.getBlocks(car.position.lane,
                                                        car.position.block + 1,
                                                        car.speed,
                                                        lanes);
            simulateBlocks(car, terrains);
        } else if(Supports.isCommandEqual(cmd, Abilities.TURN_LEFT)){
            car.position.block += (car.speed - 1);
            car.position.lane -= 1;
            List<Terrain> terrains = Supports.getBlocks(car.position.lane - 1,
                                                        car.position.block,
                                                        car.speed,
                                                        lanes);
            simulateBlocks(car, terrains);
        } else if(Supports.isCommandEqual(cmd, Abilities.TURN_RIGHT)){
            car.position.block += (car.speed - 1);
            car.position.lane += 1;
            List<Terrain> terrains = Supports.getBlocks(car.position.lane + 1,
                                                        car.position.block,
                                                        car.speed,
                                                        lanes);
            simulateBlocks(car, terrains);
        } else if(Supports.isCommandEqual(cmd, Abilities.FIX)){
            car.damage -= 2;
            car.damage = Math.max(0, car.damage);
        } else if(Supports.isCommandEqual(cmd, Abilities.LIZARD)){
            car.position.block += car.speed;
            List<Terrain> terrains = Supports.getBlocks(car.position.lane,
                                                        car.position.block + 1,
                                                        car.speed,
                                                        lanes);
            for(int i = 0; i < terrains.size() - 1; i++){
                terrains.set(i, Terrain.EMPTY);
            }
            simulateBlocks(car, terrains);
        } else if(Supports.isCommandEqual(cmd, Abilities.DECELERATE)){
            car.speed = Supports.getDeceleratedSpeed(car.speed, false);
            car.position.block += car.speed;
            List<Terrain> terrains = Supports.getBlocks(car.position.lane,
                                                        car.position.block + 1,
                                                        car.speed,
                                                        lanes);
            simulateBlocks(car, terrains);
        } else{
            car.position.block += car.speed;
            List<Terrain> terrains = Supports.getBlocks(car.position.lane,
                                                        car.position.block + 1,
                                                        car.speed,
                                                        lanes);
            simulateBlocks(car, terrains);
        }
    }

    /* PROSEDUR Menyimulasikan car melewati block-block
     * F.S.: car berubah mendapatkan efek dari terrain yang dilaluinya */
    public static void simulateBlocks(Car car, List<Terrain> terrains){
        //car.position.block += terrains.size();
        List<PowerUps> newPowers = new ArrayList<>();
        for(Terrain terrain : terrains){
            if(terrain == Terrain.MUD){
                car.damage += 1;
                car.speed = Supports.getDeceleratedSpeed(car.speed, true);
            } else if(terrain == Terrain.OIL_SPILL){
                car.damage += 1;
                car.speed = Supports.getDeceleratedSpeed(car.speed, true);
            } else if(terrain == Terrain.OIL_POWER){
                newPowers.add(PowerUps.OIL);
            } else if(terrain == Terrain.BOOST){
                newPowers.add(PowerUps.BOOST);
            } else if(terrain == Terrain.WALL){
                car.damage += 2;
                car.speed = 3;
            } else if(terrain == Terrain.LIZARD){
                newPowers.add(PowerUps.LIZARD);
            } else if(terrain == Terrain.TWEET){
                newPowers.add(PowerUps.TWEET);
            } else if(terrain == Terrain.EMP){
                newPowers.add(PowerUps.EMP);
            } else if(terrain == Terrain.CYBERTRUCK){
                car.damage += 2;
                car.speed = 3;
            }
        }
        car.damage = Math.min(car.damage, 5);
        car.speed = Math.min(car.speed, Supports.getCurrentSpeedLimit(car.damage));

        int nip = car.powerups.length;
        int nnp = newPowers.size();
        PowerUps[] newPowersArr = new PowerUps[nip + nnp];
        for(int i = 0; i < nip; i++){
            newPowersArr[i] = car.powerups[i];
        }
        for(int i = 0; i < nnp; i++){
            newPowersArr[i + nip] = newPowers.get(i);
        }
        car.powerups = newPowersArr;
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