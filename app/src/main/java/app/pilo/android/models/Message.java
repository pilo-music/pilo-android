package app.pilo.android.models;

public class Message {
    private int id;
    private String subject;
    private String text;
    private String type;
    private String created_at;
    private int sender;

    public Message() {
        this.id = 0;
        this.sender = 0;
        this.subject = "";
        this.text = "";
        this.type = "";
        this.created_at = "";
    }

    public Message(int id, int sender, String subject, String text, String type, String created_at) {
        this.id = id;
        this.sender = sender;
        this.subject = subject;
        this.text = text;
        this.type = type;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }
}
