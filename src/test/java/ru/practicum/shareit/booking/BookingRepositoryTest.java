package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(1L, "name", "email@mail.ru"));
        booker = userRepository.save(new User(2L, "user", "user@mail.ru"));
        item = itemRepository.save(
                Item.builder()
                        .name("item")
                        .description("desc")
                        .available(true)
                        .owner(owner)
                        .build()
        );
    }

    @Test
    void findCurrentBookings() {
        Booking booking = bookingRepository.save(
                makeBooking(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1))
        );

        List<Booking> result = bookingRepository.findCurrentBookings(booker.getId(), Pageable.ofSize(5));

        assertThat(result).hasSize(1);
        assertThat(result).contains(booking);
    }

    @Test
    void findPastBookings() {
        Booking booking = bookingRepository.save(
                makeBooking(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1))
        );

        List<Booking> result = bookingRepository.findPastBookings(booker.getId(), Pageable.ofSize(5));

        assertThat(result).hasSize(1);
        assertThat(result).contains(booking);
    }

    @Test
    void findFutureBookings() {
        Booking booking = bookingRepository.save(
                makeBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2))
        );

        List<Booking> result = bookingRepository.findFutureBookings(booker.getId(), Pageable.ofSize(5));

        assertThat(result).hasSize(1);
        assertThat(result).contains(booking);
    }

    @Test
    void findOwnerCurrentBookings() {
        Booking booking = bookingRepository.save(
                makeBooking(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1))
        );

        List<Booking> result = bookingRepository.findOwnerCurrentBookings(owner.getId(), Pageable.ofSize(5));

        assertThat(result).hasSize(1);
        assertThat(result).contains(booking);
    }

    @Test
    void findOwnerPastBookings() {
        Booking booking = bookingRepository.save(
                makeBooking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1))
        );

        List<Booking> result = bookingRepository.findOwnerPastBookings(owner.getId(), Pageable.ofSize(5));

        assertThat(result).hasSize(1);
        assertThat(result).contains(booking);
    }

    @Test
    void findOwnerFutureBookings() {
        Booking booking = bookingRepository.save(
                makeBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3))
        );

        List<Booking> result = bookingRepository.findOwnerFutureBookings(owner.getId(), Pageable.ofSize(5));

        assertThat(result).hasSize(1);
        assertThat(result).contains(booking);
    }

    private Booking makeBooking(LocalDateTime start, LocalDateTime end) {
        return Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();
    }
}
