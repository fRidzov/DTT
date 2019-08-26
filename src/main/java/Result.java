import java.util.List;

public class Result {
    private String country;
    private String startingDate;
    private List<String> emails;

    public Result() {
    }

    public Result(String country, String startingDate, List<String> emails) {
        this.country = country;
        this.startingDate = startingDate;
        this.emails = emails;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
