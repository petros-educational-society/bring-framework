package com.petros.bringframework.web.http;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nullable;
import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Represents an HTTP request or response entity, consisting of headers and body.
 * @author Viktor Basanets
 * @Project: bring-framework
 */
@Getter
@EqualsAndHashCode
public class HttpEntity<T> {
    /**
     * The empty {@code HttpEntity}, with nobody or headers.
     */
    public static final HttpEntity<?> EMPTY = new HttpEntity<>();

    private HttpHeaders headers;

    @Nullable
    private final T body;

    /**
     * Create a new, empty {@code HttpEntity}.
     */
    protected HttpEntity() {
        this(null, null);
    }

    /**
     * Create a new {@code HttpEntity} with the given body and no headers.
     * @param body the entity body
     */
    public HttpEntity(T body) {
        this(body, null);
    }

    /**
     * Create a new {@code HttpEntity} with the given headers and no body.
     * @param headers the entity headers
     */
    public HttpEntity(Map<String, String> headers) {
        this(null, headers);
    }

    /**
     * Create a new {@code HttpEntity} with the given body and headers.
     * @param body the entity body
     * @param headers the entity headers
     */
    public HttpEntity(@Nullable T body, @Nullable Map<String, String> headers) {
        this.body = body;
        if (headers != null) {
            this.headers = HttpHeaders.of(headers.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, h -> List.of(h.getValue()))),
                    (s1, s2) -> s1.contains(s2) || s2.contains(s1));
        }
    }

    public boolean hasBody() {
        return nonNull(body);
    }
}
