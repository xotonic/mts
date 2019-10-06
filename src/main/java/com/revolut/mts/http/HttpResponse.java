package com.revolut.mts.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpResponse {

    private final HStatus status;
    private final String mime;
    private final String body;
    private Map<String, String> extraHeaders;

    public HttpResponse(HStatus status, String mime, String body) {
        this.status = status;
        this.mime = mime;
        this.body = body;
        this.extraHeaders = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        extraHeaders.put(name, value);
    }

    public HStatus getStatus() {
        return status;
    }

    public void write(HttpExchange exchange) throws IOException {
        final var headers = exchange.getResponseHeaders();
        headers.add("Content-Type", mime);
        headers.add("Date", getServerTime());
        extraHeaders.forEach(headers::add);
        final var bytes = body.getBytes();
        exchange.sendResponseHeaders(status.code(), bytes.length);
        var os = exchange.getResponseBody();
        os.write(bytes);
        exchange.close();
    }

    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

}
