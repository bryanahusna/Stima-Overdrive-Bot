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
}
