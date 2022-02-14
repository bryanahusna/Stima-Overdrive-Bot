package za.co.entelect.challenge.globalentities;

public class GlobalState {
    public Player player;
    public Player enemy;
    //public Map map;
    public int cyber_x1;
    public int cyber_x2;
    public int cyber_y1;
    public int cyber_y2;

    public GlobalState() {
        this.player = new Player(1);
        this.enemy = new Player(2);
//        this.map = new Map();
        this.cyber_x1 = 0;
        this.cyber_x2 = 0;
        this.cyber_y1 = 0;
        this.cyber_y2 = 0;
    }

    public GlobalState clone() {
        GlobalState clone = new GlobalState();
        clone.player = this.player.clone();
        clone.enemy = this.enemy.clone();
        //clone.map = this.map.clone();
        clone.cyber_x1 = this.cyber_x1;
        clone.cyber_x2 = this.cyber_x2;
        clone.cyber_y1 = this.cyber_y1;
        clone.cyber_y2 = this.cyber_y2;
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

    public void deleteCyberTruck(int x, int y){
        if(this.cyber_x1==x&&this.cyber_y1==y){
            this.cyber_x1 = 0;
            this.cyber_y1 = 0;
        }
        else if(this.cyber_x2==x&&this.cyber_y2==y){
            cyber_x2 = 0;
            cyber_y2 = 0;
        }
    }

    public void setCyberTruck(int x, int y){
        if(this.cyber_x1==0){
            this.cyber_x1 = x;
            this.cyber_y1 = y;
        }
        else{
            this.cyber_x2 = x;
            this.cyber_y2 = y;
        }
    }
//    public Map getMap() {
//        return this.map.clone();
//    }
}
