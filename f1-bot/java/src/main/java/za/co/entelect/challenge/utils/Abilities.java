package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.command.*;

public class Abilities {
    // NORMAL COMMANDS
    public final static Command ACCELERATE = new AccelerateCommand();
    public final static Command BOOST = new BoostCommand();
    public final static Command DECELERATE = new DecelerateCommand();
    public final static Command DO_NOTHING = new DoNothingCommand();
    public final static Command FIX = new FixCommand();
    // BOOST COMMAND
    public final static Command EMP = new EmpCommand();
    public final static Command LIZARD = new LizardCommand();
    public final static Command OIL = new OilCommand();

    public static Command TWEET(int lane, int block) {
        return new TweetCommand(lane, block);
    }

    // MOVEMENT COMMAND
    public final static Command TURN_LEFT = new ChangeLaneCommand(-1);
    public final static Command TURN_RIGHT = new ChangeLaneCommand(1);

    // ENEMY READING ABILITIES
    // ENEMY STATES
    public static Command convertOffensive(Command cmd) {
        if (cmd instanceof OilCommand
                || cmd instanceof TweetCommand
                || cmd instanceof EmpCommand) {
            return DO_NOTHING;
        }
        return cmd;
    }

    public static void print(Command command){
        if(Supports.isCommandEqual(command, Abilities.ACCELERATE)){
            System.out.print(" ACC ");
        }
        else if(Supports.isCommandEqual(command, Abilities.DECELERATE)){
            System.out.print(" DCC ");
        }
        else if(Supports.isCommandEqual(command, Abilities.BOOST)){
            System.out.print(" BOOST ");
        }
        else if(Supports.isCommandEqual(command, Abilities.DO_NOTHING)){
            System.out.print(" NOTHING ");
        }
        else if(Supports.isCommandEqual(command, Abilities.LIZARD)){
            System.out.print(" LIZARD ");
        }
        else if(Supports.isCommandEqual(command, Abilities.FIX)){
            System.out.print(" FIX ");
        }
        else if(Supports.isCommandEqual(command, Abilities.TURN_LEFT)){
            System.out.print(" TURN_LEFT ");
        }
        else if(Supports.isCommandEqual(command, Abilities.TURN_RIGHT)){
            System.out.print(" TURN_RIGHT ");
        }
    }

}
