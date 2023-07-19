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
import java.util.List;

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
    void saveBookingWithIncorrectItemIdShouldThrowException() {
        bookingDtoRequest = new BookingDtoRequest(
                55L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThatThrownBy(
                () -> bookingService.saveBooking(booker.getId(), bookingDtoRequest)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Вещь с ID 55 не найдена");
    }

    @Test
    void saveBookingWithIncorrectUserIdShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.saveBooking(350, bookingDtoRequest)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID 350 не найден");
    }

    @Test
    void saveBookingWithUnavailableItemShouldThrowException() {
        savedItem = itemService.saveItem(
                owner.getId(),
                ItemDto.builder().name("item2").description("description2").available(false).build()
        );
        bookingDtoRequest = new BookingDtoRequest(
                savedItem.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );

        assertThatThrownBy(
                () -> bookingService.saveBooking(booker.getId(), bookingDtoRequest)
        ).isInstanceOf(ValidationException.class)
                .hasMessage(String.format("Вещь с ID %d недоступна для аренды", savedItem.getId()));
    }

    @Test
    void approveBooking() {
        BookingDto actual = bookingService.approveBooking(owner.getId(), savedBooking.getId(), true);

        assertThat(actual.getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(actual.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
        assertThat(actual.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void approveNonExistingBookingShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.approveBooking(owner.getId(), 777, true)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование с ID 777 не найдено");
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
    void getBookingByOwner() {
        BookingDto actual = bookingService.getBooking(owner.getId(), savedBooking.getId());

        assertThat(actual.getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(actual.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
    }

    @Test
    void getBookingByBooker() {
        BookingDto actual = bookingService.getBooking(booker.getId(), savedBooking.getId());

        assertThat(actual.getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(actual.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
    }

    @Test
    void getNonExistingBookingShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.getBooking(owner.getId(), 70)
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Бронирование с ID 70 не найдено");
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
    void getBookerBookingsByIncorrectStateShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.getBookerBookings(booker.getId(), "unknown", PageRequest.of(0, 2))
        ).isInstanceOf(ValidationException.class)
                .hasMessage("Unknown state: unknown");
    }

    @Test
    void getBookerBookingsByIncorrectUserIdShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.getBookerBookings(44, "WAITING", PageRequest.of(0, 2))
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID 44 не найден");
    }

    @Test
    void getAllBookerBookings() {
        List<BookingDto> actual = bookingService.getBookerBookings(
                booker.getId(), "ALL", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(actual.get(0).getEnd()).isEqualTo(bookingDtoRequest.getEnd());
        assertThat(actual.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void getCurrentBookerBookings() {
        savedBooking = bookingService.saveBooking(
                booker.getId(),
                new BookingDtoRequest(
                        savedItem.getId(),
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(3)
                )
        );

        List<BookingDto> actual = bookingService.getBookerBookings(
                booker.getId(), "CURRENT", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual).contains(savedBooking);
    }

    @Test
    void getPastBookerBookings() {
        savedBooking = bookingService.saveBooking(
                booker.getId(),
                new BookingDtoRequest(
                        savedItem.getId(),
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.now().minusDays(1)
                )
        );

        List<BookingDto> actual = bookingService.getBookerBookings(
                booker.getId(), "PAST", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual).contains(savedBooking);
    }

    @Test
    void getFutureBookerBookings() {
        List<BookingDto> actual = bookingService.getBookerBookings(
                booker.getId(), "FUTURE", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual).contains(savedBooking);
    }

    @Test
    void getWaitingBookerBookings() {
        List<BookingDto> actual = bookingService.getBookerBookings(
                booker.getId(), "WAITING", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual).contains(savedBooking);
    }

    @Test
    void getOwnerBookingsByIncorrectStateShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.getOwnerBookings(owner.getId(), "unknown", PageRequest.of(0, 2))
        ).isInstanceOf(ValidationException.class)
                .hasMessage("Unknown state: unknown");
    }

    @Test
    void getOwnerBookingsByIncorrectUserIdShouldThrowException() {
        assertThatThrownBy(
                () -> bookingService.getOwnerBookings(44, "WAITING", PageRequest.of(0, 2))
        ).isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователь с ID 44 не найден");
    }

    @Test
    void getAllOwnerBookings() {
        List<BookingDto> actual = bookingService.getOwnerBookings(
                owner.getId(), "ALL", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getStart()).isEqualTo(bookingDtoRequest.getStart());
        assertThat(actual.get(0).getEnd()).isEqualTo(bookingDtoRequest.getEnd());
        assertThat(actual.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void getCurrentOwnerBookings() {
        savedBooking = bookingService.saveBooking(
                booker.getId(),
                new BookingDtoRequest(
                        savedItem.getId(),
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(3)
                )
        );

        List<BookingDto> actual = bookingService.getOwnerBookings(
                owner.getId(), "CURRENT", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual).contains(savedBooking);
    }

    @Test
    void getPastOwnerBookings() {
        savedBooking = bookingService.saveBooking(
                booker.getId(),
                new BookingDtoRequest(
                        savedItem.getId(),
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.now().minusDays(1)
                )
        );

        List<BookingDto> actual = bookingService.getOwnerBookings(
                owner.getId(), "PAST", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual).contains(savedBooking);
    }

    @Test
    void getFutureOwnerBookings() {
        List<BookingDto> actual = bookingService.getOwnerBookings(
                owner.getId(), "FUTURE", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual).contains(savedBooking);
    }

    @Test
    void getWaitingOwnerBookings() {
        List<BookingDto> actual = bookingService.getOwnerBookings(
                owner.getId(), "WAITING", PageRequest.of(0, 2)
        );

        assertThat(actual).hasSize(1);
        assertThat(actual).contains(savedBooking);
    }
}
