package guan.pcihearten;

/**
 * Created by User on 6/6/2017.
 */

public class read_push {
//unused
    private String name;
    private String score;
    private Long played;
    private Long total;
    private Long read_total;
    private String rank_level;
    private String photoUrl;



    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public read_push() {
    }

    public read_push(Long read_total, String rank_level){
        this.read_total = read_total;
        this.rank_level = rank_level;
    }

    public Long getRead_total() {
        return read_total;
    }

    public void setRead_total(Long read_total) {
        this.read_total = read_total;
    }

    public String getRank_level() {
        return rank_level;
    }

    public void setRank_level(String rank_level) {
        this.rank_level = rank_level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setPlayed(Long played) {
        this.played = played;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public Long getPlayed() {
        return played;
    }

    public Long getTotal() {
        return total;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
