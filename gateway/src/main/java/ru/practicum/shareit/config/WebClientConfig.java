package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;

@Configuration
public class WebClientConfig {
    private static final String BOOKINGS_API_PREFIX = "/bookings";
    private static final String ITEMS_API_PREFIX = "/items";
    private static final String REQUESTS_API_PREFIX = "/requests";
    private static final String USERS_API_PREFIX = "/users";

    @Value("${shareit-server.url}")
    private String serverUrl;

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + BOOKINGS_API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        return new BookingClient(restTemplate);
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + ITEMS_API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        return new ItemClient(restTemplate);
    }

    @Bean
    public ItemRequestClient itemRequestClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + REQUESTS_API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        return new ItemRequestClient(restTemplate);
    }

    @Bean
    public UserClient userClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + USERS_API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        return new UserClient(restTemplate);
    }
}
