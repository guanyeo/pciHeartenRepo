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


}
