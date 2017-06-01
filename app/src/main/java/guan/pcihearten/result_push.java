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


    public result_push() {
    }

    public result_push(String question, String answer, String time, Long total_correct, Long total_wrong){
        this.question = question;
        this.answer = answer;
        this.time = time;
        this.total_correct = total_correct;
        this.total_wrong = total_wrong;
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
}
