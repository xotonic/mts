package com.revolut.mts;

import fi.iki.elonen.NanoHTTPD;

import java.util.StringJoiner;

public class HResponse  {

    private NanoHTTPD.Response response;

    public HResponse(NanoHTTPD.Response response) {
        this.response = response;
    }

    public NanoHTTPD.Response getResponse() {
        return response;
    }

    public HResponse withAllowHeader(Iterable<HMethod> allowedMethods) {
        var joiner = new StringJoiner(", ");
        allowedMethods.forEach(m -> joiner.add(m.name()));
        response.addHeader("Allow", joiner.toString());
        return this;
    }
}
