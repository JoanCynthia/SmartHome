package redeemsystems.com.home;

public class RaiseRequest
{
    String subject, description, status;
    CharSequence date;

    public RaiseRequest(String subject, String description, String status, CharSequence date) {
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CharSequence getDate() {
        return date;
    }

    public void setDate(CharSequence date) {
        this.date = date;
    }
}
