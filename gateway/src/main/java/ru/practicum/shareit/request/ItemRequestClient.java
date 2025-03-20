package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static org.springframework.http.RequestEntity.post;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addRequest(Long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, null, itemRequestDto);
    }

    public ResponseEntity<Object> getRequests(Long userId) {
        return get("", userId, null);
    }

    public ResponseEntity<Object> findAllRequests(Long userId) {
        return get("/all", userId, null);
    }

    public ResponseEntity<Object> findRequestById(Long requestId) {
        return get("/" + requestId, null, null);
    }
}
