package guan.pcihearten;


public class leaderboard_push {

    private String id;
    private String name;
    private String score;
    private Long played;
    private Long total;


    public leaderboard_push() {
        //Constructor needed
    }

    public leaderboard_push(String name, String score, Long played) {
        this.name = name;
        this.score = score;
        this.played = played;

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

    public Long getPlayed(){return played;}

    public void setPlayed(Long played){this.played=played;}

    public Long getTotal(){return total;}

    public void setTotal(Long total){this.total=total;}


}



