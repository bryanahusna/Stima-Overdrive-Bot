package za.co.entelect.challenge.globalentities;

public class GlobalState {
    public Player player;
    public Player enemy;
    public Map map;

    public GlobalState() {
        this.player = new Player(1);
        this.enemy = new Player(2);
        this.map = new Map();
    }

    public GlobalState clone() {
        GlobalState clone = new GlobalState();
        clone.player = this.player.clone();
        clone.enemy = this.enemy.clone();
        clone.map = this.map.clone();
        return clone;
    }

    public GlobalState switch_() {
        // Mengembalikan state baru yang menukar posisi player dan enemy
        GlobalState tmpState = this.clone();
        Player tmpPlayer = tmpState.player.clone();
        tmpState.player = tmpState.enemy.clone();
        tmpState.enemy = tmpPlayer;

        return tmpState;
    }

    public Map getMap() {
        return this.map.clone();
    }
}
