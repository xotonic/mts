package com.revolut.mts.http;

import fi.iki.elonen.NanoHTTPD;

public enum HMethod {
   GET(NanoHTTPD.Method.GET),
   POST(NanoHTTPD.Method.POST),
   PUT(NanoHTTPD.Method.PUT),

   UNKNOWN(null)
   ;

   private NanoHTTPD.Method method;
   HMethod(NanoHTTPD.Method method) {
      this.method = method;
   }
   public static HMethod map(NanoHTTPD.Method method) {
      for (var m : values()) {
         if (m.method.equals(method)) {
            return m;
         }
      }
      return UNKNOWN;
   }
}
