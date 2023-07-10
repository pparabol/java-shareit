package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private final User requestor;

    @Transient
    private LocalDateTime created = LocalDateTime.now();

}
