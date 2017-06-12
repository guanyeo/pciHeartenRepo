package guan.pcihearten;

/**
 * Created by User on 1/15/2017.
 */

public class game_data {
    private String id;
    private String answer;
    private String question;
    private String photoUrl;
    private String quesPhoto;
    private String state;

    private String choice1;
    private String choice2;
    private String choice3;


    public game_data(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion(){
        return question;
    }

    public void setQuestion (String question){
        this.question = question;
    }

    public String getChoice1(){
        return choice1;
    }

    public void setChoice1 (String choice1){
        this.choice1 = choice1;
    }

    public String getChoice2(){
        return choice2;
    }

    public void setChoice2 (String choice2){
        this.choice2 = choice2;
    }

    public String getChoice3(){
        return choice3;
    }

    public void setChoice3 (String choice3){
        this.choice3 = choice3;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getQuesPhoto() {
        return quesPhoto;
    }

    public void setQuesPhoto(String quesPhoto) {
        this.quesPhoto = quesPhoto;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
