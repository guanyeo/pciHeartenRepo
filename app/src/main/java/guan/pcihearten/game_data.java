package guan.pcihearten;

/**
 * Created by User on 1/15/2017.
 */

public class game_data {
    private String id;
    private String answer;
    private long answerTag;
    private String question;
    private long questionTag;

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

    public  long getAnswerTag() {
        return answerTag;
    }

    public void setAnswerTag(long answerTag) {
        this.answerTag = answerTag;
    }

    public String getQuestion(){
        return question;
    }

    public void setQuestion (String question){
        this.question = question;
    }

    public long getQuestionTag(){
        return questionTag;
    }

    public void setQuestionTag (long questionTag){
        this.questionTag = questionTag;
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







}
