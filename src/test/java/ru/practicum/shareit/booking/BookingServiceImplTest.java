package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private BookingDtoRequest bookingDtoRequest;
    private UserDto owner;
    private UserDto booker;
    private ItemDto savedItem;
    private BookingDto savedBooking;

    @BeforeEach
    void setUp() {
        owner = userService.saveUser(new UserDto(1L, "name", "email@mail.ru"));
        booker = userService.saveUser(new UserDto(2L, "user", "user@mail.ru"));
        savedItem = itemService.saveItem(
                owner.getId(),
                ItemDto.builder().name("item").description("description").available(true).build()
        );
        bookingDtoRequest = new BookingDtoRequest(
                savedItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );
        savedBooking = bookingService.saveBooking(booker.getId(), bookingDtoRequest);
    }

    @Test
    void saveBooking() {
        TypedQuery<Booking> query = em.createQuery("from Booking b where b.item.id = :itemId", Booking.class);
        Booking booking = query.setParameter("itemId", bookingDtoRequest.getItemId()).getSingleResult();

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
        assertThat(booking.getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
        assertThat(booking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void saveBookingWithEndBeforeStartShouldThrowException() {
        bookingDtoRequest = new BookingDtoRequest(
                savedItem.getId(),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(1)
        );

        assertThatThrownBy(
                () -> bookingService.saveBooking(booker.getId(), bookingDtoRequest)
        ).isInstanceOf(ValidationException.class)
                .hasMessage("Время бронирования указано некорректно");
    }

    @Test
    void saveBookingByItemOwnerShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.saveBooking(owner.getId(), bookingDtoRequest)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь недоступна для бронирования владельцем");
    }

    @Test
    void approveBooking() {
        BookingDto actual = bookingService.approveBooking(owner.getId(), savedBooking.getId(), true);

        assertThat(actual.getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(actual.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
        assertThat(actual.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void approveBookingByBookerShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.approveBooking(booker.getId(), savedBooking.getId(), true)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Изменять статус бронирования может только владелец вещи");
    }

    @Test
    void approveAlreadyApprovedBookingShouldThrowException() {
        bookingService.approveBooking(owner.getId(), savedBooking.getId(), true);

        assertThatThrownBy(
                () -> bookingService.approveBooking(owner.getId(), savedBooking.getId(), true)
        ).isInstanceOf(ValidationException.class)
                .hasMessage(String.format("Бронирование с ID %d уже подтверждено", savedBooking.getId()));
    }

    @Test
    void getBooking() {
        BookingDto actual = bookingService.getBooking(owner.getId(), savedBooking.getId());

        assertThat(actual.getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(actual.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
    }

    @Test
    void getBookingByOtherUserShouldThrowException() {
        UserDto otherUser = userService.saveUser(new UserDto(3L, "other", "other@o.ru"));

        assertThatThrownBy(
                () -> bookingService.getBooking(otherUser.getId(), savedBooking.getId())
        ).isInstanceOf(NotFoundException.class)
                .hasMessage(
                        String.format("Информация о бронировании с ID %d недоступна для просмотра",
                                savedBooking.getId())
                );

    }

    @Test
    void getBookingsByIncorrectStateShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.getBookerBookings(booker.getId(), "unknown", PageRequest.of(0, 2))
        ).isInstanceOf(ValidationException.class)
                .hasMessage("Unknown state: unknown");
    }
}
