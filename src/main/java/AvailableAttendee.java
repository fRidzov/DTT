import java.util.List;

public class AvailableAttendee {
    private Attendee attendee;
    private List<String> availableStartDates;

    public AvailableAttendee(Attendee attendee, List<String> availableStartDates) {
        this.attendee = attendee;
        this.availableStartDates = availableStartDates;
    }

    public Attendee getAttendee() {
        return attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public List<String> getAvailableStartDates() {
        return availableStartDates;
    }

    public void setAvailableStartDates(List<String> availableStartDates) {
        this.availableStartDates = availableStartDates;
    }
}
