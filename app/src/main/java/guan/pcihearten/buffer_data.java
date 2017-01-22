package guan.pcihearten;

/**
 * Created by User on 1/20/2017.
 */

public class buffer_data {
    private String id;
    private String state;
    private String p1;
    private String p2;
    private String game_key;

public buffer_data(){

}

    public buffer_data(String state, String p1, String p2, String game_key) {
        this.state = state;
        this.p1 = p1;
        this.p2 = p2;
        this.game_key = game_key;

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

    public void setP1 () {this.p1=p1;}

    public String getP2(){return p2;}

    public void setP2 () {this.p2=p2;}

    public String getGame_key(){return game_key;}

    public void setGame_key () {this.game_key=game_key;}






}
