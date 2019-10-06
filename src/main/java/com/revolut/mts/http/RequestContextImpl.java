package com.revolut.mts.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revolut.mts.http.routing.RoutePath;
import com.revolut.mts.http.routing.RoutedHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * A delegating facade for {@link RoutedHandler}
 */
public class RequestContextImpl implements RequestContext {

    private static final Logger logger = LogManager.getLogger(RequestContextImpl.class);

    private ResponseProvider responseProvider;
    private RoutePath path;

    private InputStream inputStream;
    private static ObjectMapper jsonMapper;
    private void initObjectMapper() {
        if (jsonMapper == null) {
            jsonMapper = new ObjectMapper();
            jsonMapper.registerModule(new JavaTimeModule());
        }
    }

    public RequestContextImpl(ResponseProvider responseProvider) {
        this.responseProvider = responseProvider;
    }

    public RequestContextImpl(ResponseProvider responseProvider, InputStream inputStream) {
        this.responseProvider = responseProvider;
        this.inputStream = inputStream;
    }

    void setPath(RoutePath path) {
        this.path = path;
    }

    @Override
    public <T> HResponse<T> respond(HStatus status, T body) {
        return responseProvider.respond(status, body);
    }

    @Override
    public <T> HResponse<T> error(HStatus status, String message) {
        return responseProvider.error(status, message);
    }

    @Override
    public String getString(int index) {
        return path.getString(index);
    }

    @Override
    public int getInt(int index) {
        return path.getInt(index);
    }

    @Override
    public <T> T body(Class<T> clazz) {
        initObjectMapper();
        try {
            return jsonMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
