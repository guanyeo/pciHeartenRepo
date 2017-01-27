package guan.pcihearten;

/**
 * Created by User on 1/27/2017.
 */

public class trigger_data {
    private long p1Hp;
    private long p2Hp;


    public trigger_data(){

    }

    public trigger_data(long p1Hp, long p2Hp){
        this.p1Hp = p1Hp;
        this.p2Hp = p2Hp;
    }

    public long getP1Hp() {
        return p1Hp;
    }

    public void setP1Hp(long p1Hp) {
        this.p1Hp = p1Hp;
    }

    public long getP2Hp() {
        return p2Hp;
    }

    public void setP2Hp(long p2Hp) {
        this.p2Hp = p2Hp;
    }



}
