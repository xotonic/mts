package com.revolut.mts;

/**
 * A delegating facade for {@link RoutedHandler}
 */
public class RequestContextImpl implements RequestContext {

    private ResponseProvider responseProvider;
    private RoutePath path;

    public RequestContextImpl(ResponseProvider responseProvider) {
        this.responseProvider = responseProvider;
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
}
