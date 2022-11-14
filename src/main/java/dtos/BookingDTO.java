package dtos;

public class BookingDTO {

    private Integer id;
    private Integer eventId;
    private String username;

    public BookingDTO(Integer id, Integer eventId, String username) {
        this.id = id;
        this.eventId = eventId;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public Integer getEventId() {
        return eventId;
    }

    public String getUsername() {
        return username;
    }
}
