package za.co.entelect.challenge.globalentities;

public class GlobalState {
    public GlobalState clone(){
        GlobalState clone = new GlobalState();
        clone.player = this.player.clone();
        clone.enemy = this.enemy.clone();
        clone.map = this.map.clone();
        return clone;
    }
    public Player player;
    public Player enemy;
    public Map map;
    public GlobalState(){
        this.player = new Player(1);
        this.enemy = new Player(2);
        this.map = new Map();
    }
}
