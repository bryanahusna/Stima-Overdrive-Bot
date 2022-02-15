package za.co.entelect.challenge.globalentities;

public class GlobalState {
    public Player player;
    public Player enemy;
    public int pref_x1;
    public int pref_x2;
    public int pref_y1;
    public int pref_y2;
    public int cyber_x1;
    public int cyber_x2;
    public int cyber_y1;
    public int cyber_y2;

    public GlobalState() {
        this.player = new Player(1);
        this.enemy = new Player(2);
        this.cyber_x1 = 0;
        this.cyber_x2 = 0;
        this.cyber_y1 = 0;
        this.cyber_y2 = 0;
        this.pref_x1 = 0;
        this.pref_x2 = 0;
        this.pref_y1 = 0;
        this.pref_y2 = 0;
    }

    public GlobalState clone() {
        GlobalState clone = new GlobalState();
        clone.player = this.player.clone();
        clone.enemy = this.enemy.clone();
        clone.cyber_x1 = this.cyber_x1;
        clone.cyber_x2 = this.cyber_x2;
        clone.cyber_y1 = this.cyber_y1;
        clone.cyber_y2 = this.cyber_y2;
        clone.pref_x1 = this.pref_x1;
        clone.pref_x2 = this.pref_x2;
        clone.pref_y1 = this.pref_y1;
        clone.pref_y2 = this.pref_y2;
        return clone;
    }

    public void deleteCyberTruck(int x, int y){
        if(this.cyber_x1==x&&this.cyber_y1==y){
            this.cyber_x1 = 0;
            this.cyber_y1 = 0;
        }
        else if(this.cyber_x2==x&&this.cyber_y2==y){
            this.cyber_x2 = 0;
            this.cyber_y2 = 0;
        }
    }

    public void setCyberTruck(int x, int y){
        if(this.cyber_x1==0){
            this.pref_x1 = x;
            this.pref_y1 = y;
            this.cyber_x1 = x;
            this.cyber_y1 = y;
        }
        else{
            this.pref_x2 = x;
            this.pref_y2 = y;
            this.cyber_x2 = x;
            this.cyber_y2 = y;
        }
    }

    public boolean isSomethingDeleted(int id){
        if(id==1){
            return this.pref_x1!=this.cyber_x1||this.pref_y1!=this.cyber_y1;
        }
        else {
            return this.pref_x2!=this.cyber_x2||this.pref_y2!=this.cyber_y2;
        }
    }
}
