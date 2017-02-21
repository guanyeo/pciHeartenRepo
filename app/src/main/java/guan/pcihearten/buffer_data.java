package guan.pcihearten;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 1/20/2017.
 */

public class buffer_data {
    private String id;
    private String state;
    private String p1;
    private String p2;
    private String game_key;
    private String p1Photo;
    private String p2Photo;
    private Long p1Hp;
    private Long p2Hp;
    private String pCancel;

public buffer_data(){

}

    public buffer_data(String state, String p1, String p2, String game_key, String p1Photo, String p2Photo, Long p1Hp, Long p2Hp) {
        this.state = state;
        this.p1 = p1;
        this.p2 = p2;
        this.game_key = game_key;
        this.p1Photo = p1Photo;
        this.p2Photo = p2Photo;
        this.p1Hp = p1Hp;
        this.p2Hp = p2Hp;


    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState (){return state;}

    public void setState(String state){this.state = state;}

   public String getP1(){return p1;}

    public void setP1 (String p1) {this.p1=p1;}

    public String getP2(){return p2;}

    public void setP2 (String p2) {this.p2=p2;}

    public String getGame_key(){return game_key;}

    public void setGame_key (String game_key) {this.game_key=game_key;}

    public void setP1Photo(String p1Photo){
        this.p1Photo = p1Photo;
    }

    public String getP1Photo(){
        return p1Photo;
    }

    public void setP2Photo(String p2Photo){
        this.p2Photo = p2Photo;
    }

    public String getP2Photo(){
        return p2Photo;
    }

    public Long getP1Hp() {
        return p1Hp;
    }

    public void setP1Hp(Long p1Hp) {
        this.p1Hp = p1Hp;
    }

    public Long getP2Hp() {
        return p2Hp;
    }

    public void setP2Hp(Long p2Hp) {
        this.p2Hp = p2Hp;
    }

    public void setpCancel(String pCancel){
        this.pCancel = pCancel;
    }

    public String getpCancel(){
        return pCancel;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("p1Hp", p1Hp);
        result.put("p2HP", p2Hp);

        return result;
    }







}
