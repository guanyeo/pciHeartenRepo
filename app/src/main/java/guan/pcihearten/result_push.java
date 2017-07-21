package guan.pcihearten;

/**
 * Created by User on 4/19/2017.
 */

public class result_push {

    private String id;
    private String answer;
    private String question;
    private String time;
    private Long total_correct;
    private Long total_wrong;
    private String photoUrl;
    private String quesPhoto;
    private String choicePhoto;
    private String choice1Photo;
    private String choice2Photo;
    private String choice3Photo;


    public result_push() {
    }

    public result_push(String question, String answer, String quesPhoto, String time, Long total_correct, Long total_wrong, String choicePhoto){
        this.question = question;
        this.answer = answer;
        this.quesPhoto = quesPhoto;
        this.time = time;
        this.total_correct = total_correct;
        this.total_wrong = total_wrong;
        this.choicePhoto = choicePhoto;
    }

    public String getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getTotal_correct() {
        return total_correct;
    }

    public void setTotal_correct(Long total_correct) {
        this.total_correct = total_correct;
    }

    public Long getTotal_wrong() {
        return total_wrong;
    }

    public void setTotal_wrong(Long total_wrong) {
        this.total_wrong = total_wrong;
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

    public String getChoicePhoto() {
        return choicePhoto;
    }

    public void setChoicePhoto(String choicePhoto) {
        this.choicePhoto = choicePhoto;
    }

    public String getChoice1Photo() {
        return choice1Photo;
    }

    public void setChoice1Photo(String choice1Photo) {
        this.choice1Photo = choice1Photo;
    }

    public String getChoice2Photo() {
        return choice2Photo;
    }

    public void setChoice2Photo(String choice2Photo) {
        this.choice2Photo = choice2Photo;
    }

    public String getChoice3Photo() {
        return choice3Photo;
    }

    public void setChoice3Photo(String choice3Photo) {
        this.choice3Photo = choice3Photo;
    }
}
