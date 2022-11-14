package entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link User} entity
 */
public class UserDto implements Serializable {
    @NotNull
    @Size(min = 1, max = 255)
    private final String userName;
    private final List<Integer> bookingIds;

    public UserDto(String userName, List<Integer> bookingIds) {
        this.userName = userName;
        this.bookingIds = bookingIds;
    }

    public String getUserName() {
        return userName;
    }

    public List<Integer> getBookingIds() {
        return bookingIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto entity = (UserDto) o;
        return Objects.equals(this.userName, entity.userName) &&
                Objects.equals(this.bookingIds, entity.bookingIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, bookingIds);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "userName = " + userName + ", " +
                "bookingIds = " + bookingIds + ")";
    }
}