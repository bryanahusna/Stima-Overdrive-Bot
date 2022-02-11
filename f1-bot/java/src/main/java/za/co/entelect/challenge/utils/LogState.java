package za.co.entelect.challenge.utils;

import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.globalentities.GlobalState;

public class LogState {
    public GlobalState InitState;
    public GlobalState FinalState;
    public Command Action;

    public LogState(GlobalState InitState, GlobalState FinalState, Command Action){
        this.InitState = InitState;
        this.FinalState = FinalState;
        this.Action = Action;
    }

}
