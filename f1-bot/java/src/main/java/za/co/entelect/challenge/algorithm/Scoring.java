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
import za.co.entelect.challenge.globalentities.GlobalState;
import za.co.entelect.challenge.globalentities.Player;
import za.co.entelect.challenge.utils.Abilities;
import za.co.entelect.challenge.utils.Actions;
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
        boolean isvalid;
        for(Command cmd : commands){
            isvalid = simulate(currentCar, this.gameState, cmd);
            if(!isvalid)
                return INVALID_COMMAND;
        }
        
        return multiplyWeights(initialCar, currentCar);
    }

    public static double score(Search.Node n, GlobalState initialState){
        double score = multiplyWeights(initialState, n.State);

        GlobalState immediateNextState = Actions.simulateActions(n.Actions.get(0), Actions.predictAction(initialState.clone()), initialState);
        score += multiplyWeights(initialState, immediateNextState);

        return score;
    }

    /* Menghitung skor berdasarkan finalState dan initialState */
    public static double multiplyWeights(GlobalState initialState, GlobalState finalState){
        double score = 0;
        Player initialPlayer = initialState.player;
        Player finalPlayer = finalState.player;

        score += (finalPlayer.pos_x - initialPlayer.pos_x) * Weights.POSITION;
        score += (finalPlayer.speed - initialPlayer.speed) * Weights.SPEED;
        score += (finalPlayer.damage - initialPlayer.damage) * Weights.DAMAGE;

        score += (finalPlayer.boost - initialPlayer.boost) * Weights.BOOST;
        score += (finalPlayer.oil - initialPlayer.oil) * Weights.OIL;
        score += (finalPlayer.tweet - initialPlayer.tweet) * Weights.TWEET;
        score += (finalPlayer.lizard - initialPlayer.lizard) * Weights.LIZARD;
        score += (finalPlayer.emp - initialPlayer.emp) * Weights.EMP;

        return score;
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
     * F.S.: car berubah mendapatkan efek setelah menjalankan command cmd
     * Return true jika valid, false jika tidak */
    public static boolean simulate(Car car, GameState gameState, Command cmd){
        List<Lane[]> lanes = gameState.lanes;
        int startBlockPos = car.position.block + 1;
        if(Supports.isCommandEqual(cmd, Abilities.ACCELERATE)){
            car.speed = Supports.getAcceleratedSpeed(car.speed, car.damage);
            car.position.block += car.speed;
        } else if(Supports.isCommandEqual(cmd, Abilities.BOOST)){
            if(!Supports.hasPowerUp(PowerUps.BOOST, car.powerups))
                return false;
            car.speed = Supports.getBoostedSpeed(car.damage);
            car.position.block += car.speed;
        } else if(Supports.isCommandEqual(cmd, Abilities.TURN_LEFT)){
            if(car.position.lane == 1)
                return false;
            startBlockPos = car.position.block;
            car.position.block += (car.speed - 1);
            car.position.lane -= 1;
        } else if(Supports.isCommandEqual(cmd, Abilities.TURN_RIGHT)){
            if(car.position.lane == 4)
                return false;
            startBlockPos = car.position.block;
            car.position.block += (car.speed - 1);
            car.position.lane += 1;
        } else if(Supports.isCommandEqual(cmd, Abilities.FIX)){
            car.damage -= 2;
            car.damage = Math.max(0, car.damage);
        } else if(Supports.isCommandEqual(cmd, Abilities.LIZARD)){
            if(!Supports.hasPowerUp(PowerUps.LIZARD, car.powerups))
                return false;
            // Mengurangi 1 powerup lizard
            // PowerUps[] newPowers = new PowerUps[car.powerups.length - 1];
            // int i = 0;
            // while(i < newPowers.length){
            //     if(car.powerups[i] == PowerUps.LIZARD)
            //         break;
            //     newPowers[i] = car.powerups[i];
            //     i++;
            // }
            // while(i < newPowers.length){
            //     newPowers[i] = car.powerups[i + 1];
            //     i++;
            // }
            // car.powerups = newPowers;
            car.position.block += car.speed;

        } else if(Supports.isCommandEqual(cmd, Abilities.DECELERATE)){
            car.speed = Supports.getDeceleratedSpeed(car.speed, false, car.damage);
            car.position.block += car.speed;
        } else{
            car.position.block += car.speed;
        }

        /* Kalau mobil melebihi batas render block, command invalid */
        //if(car.position.block > lanes.get(0)[lanes.get(0).length - 1].position.block 
        //        && lanes.get(0)[lanes.get(0).length - 1].position.block < 1500){
        //            return false;
        //}

        List<Terrain> terrains = Supports.getBlocks(car.position.lane,
                                                        startBlockPos,
                                                        car.speed,
                                                        lanes);

        if(Supports.isCommandEqual(cmd, Abilities.LIZARD)){
            for(int i = 0; i < terrains.size() - 1; i++){
                terrains.set(i, Terrain.EMPTY);
            }
        }

        if(!Supports.isCommandEqual(cmd, Abilities.FIX)){
            simulateBlocks(car, terrains);
        }

        return true;
    }

    /* PROSEDUR Menyimulasikan car melewati block-block
     * F.S.: car berubah mendapatkan efek dari terrain yang dilaluinya */
    public static void simulateBlocks(Car car, List<Terrain> terrains){
        List<PowerUps> newPowers = new ArrayList<>();
        for(Terrain terrain : terrains){
            if(terrain == Terrain.MUD){
                car.damage += 1;
                car.speed = Supports.getDeceleratedSpeed(car.speed, true, car.damage);
            } else if(terrain == Terrain.OIL_SPILL){
                car.damage += 1;
                car.speed = Supports.getDeceleratedSpeed(car.speed, true, car.damage);
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
