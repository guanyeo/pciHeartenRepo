package guan.pcihearten;

/**
 * Created by User on 4/19/2017.
 */

public class result_push {

    private String id;
    private String answer;
    private String question;
    private Long questionTag;


    public result_push() {
    }

    public result_push(String question, String answer, Long questionTag){
        this.question = question;
        this.answer = answer;
        this.questionTag = questionTag;
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

    public long getQuestionTag() {
        return questionTag;
    }

    public void setQuestionTag(long questionTag) {
        this.questionTag = questionTag;
    }
}
