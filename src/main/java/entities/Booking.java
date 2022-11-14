package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "booking")
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    public Booking() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="user")
    private User user;

    @ManyToOne
    @JoinColumn(name="event")
    private Event event;

    public void setUser (User user) {
        this.user = user;
        user.getBookings().add(this);
    }

    public void setEvent (Event event) {
        this.event = event;
        event.getBookings().add(this);
    }


    public Booking(User user, Event event) {
        this.user = user;
        this.event = event;
    }
}

