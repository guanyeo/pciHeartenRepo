package guan.pcihearten;


public class guanTesto {

    private String id;
    private String name;
    private String score;


    public guanTesto() {
        //Constructor needed
    }

    public guanTesto(String name, String score) {
        this.name = name;
        this.score = score;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public  String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }



}



