package com.revolut.mts;

import fi.iki.elonen.NanoHTTPD;

enum HMethod {
   GET(NanoHTTPD.Method.GET),
   POST(NanoHTTPD.Method.POST),

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
