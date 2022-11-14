package dtos;

import entities.Event;

import java.io.Serializable;

/**
 * A DTO for the {@link entities.Event} entity
 */
public class EventDto implements Serializable {
    private final Integer id;
    private final String name;
    private final String location;
    private final String gusmester;
    private final Double price;
    private final Integer seats;

    public EventDto(Integer id, String name, String location, String gusmester, Double price, Integer seats) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.gusmester = gusmester;
        this.price = price;
        this.seats = seats;
    }

    public EventDto(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.location = event.getLocation();
        this.gusmester = event.getGusmester();
        this.price = event.getPrice();
        this.seats = event.getSeats();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getGusmester() {
        return gusmester;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getSeats() {
        return seats;
    }
}