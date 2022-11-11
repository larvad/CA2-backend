package entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column (name ="eventName", nullable = false)
    private String name;
    @Column (name ="eventLocation", nullable = false)
    private String location;
    @Column (name ="eventGusmester", nullable = false)
    private String gusmester;
    @Column (name ="eventPrice", nullable = false)
    private Double price;
    @Column (name ="eventSeats", nullable = false)
    private Integer seats;

    @JoinTable (name = "user_event", joinColumns = {
            @JoinColumn(name = "eventId", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "user_name", referencedColumnName = "user_name")})
    @ManyToMany
    private List<User> userList = new ArrayList<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void addUser(User user) {
        this.userList.add(user);
        user.getEventList().add(this);
    }
    public Event(){}

    public Event(String name, String location, String gusmester, Double price, Integer seats) {
        this.name = name;
        this.location = location;
        this.gusmester = gusmester;
        this.price = price;
        this.seats = seats;
    }



}
