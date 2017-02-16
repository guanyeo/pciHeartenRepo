package guan.pcihearten;


public class leaderboard_push {

    private String id;
    private String name;
    private String score;


    public leaderboard_push() {
        //Constructor needed
    }

    public leaderboard_push(String name, String score) {
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



