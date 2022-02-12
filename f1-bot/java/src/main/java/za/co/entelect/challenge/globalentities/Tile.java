package za.co.entelect.challenge.globalentities;

import za.co.entelect.challenge.enums.Terrain;

public class Tile {
    // Storing object tiap tile
    public int x;
    public int y;
    public Terrain tile; // original tile
    public Terrain layer; // atasan tile

    public Tile(int x, int y){
        this.x = x;
        this.y = y;
        tile = Terrain.EMPTY;
        layer = Terrain.EMPTY;
    }

    public Tile clone(){
        Tile t = new Tile(this.x, this.y);
        t.tile = this.tile;
        t.layer = this.layer;

        return t;
    }

    // Kalau layernya tidak EMPTY, kembalikan layer. Kalau layer EMPTY, kembalikan tile
    public Terrain getType(){
        if(this.layer != Terrain.EMPTY){
            return this.layer;
        } else{
            return this.tile;
        }
    }

    public void set(Terrain tile, Terrain layer) {
        this.tile = tile;
        this.layer = layer;
    }

    // equals true jika tile dan layernya sama
    @Override
    public boolean equals(Object o){
        if(o == this)
            return true;
        if(!(o instanceof Tile))
            return false;
        Tile t = (Tile) o;

        return t.tile == this.tile && t.layer == this.layer;
    }

    // Jika keduanya mempunyai layer yang bukan EMPTY, bandingkan layernya
    // Jika layer keduanya EMPTY, bandingkan tilenya
    public boolean isEqualsEff(Tile t){
        if(t.layer != Terrain.EMPTY && this.layer != Terrain.EMPTY){
            return t.layer == this.layer;
        }

        return t.tile == this.tile;
    }

    public void setCybertruck(){
        this.layer = Terrain.CYBERTRUCK;
    }

    public void deleteCybertruck(){
        this.layer = Terrain.EMPTY;
    }
}
