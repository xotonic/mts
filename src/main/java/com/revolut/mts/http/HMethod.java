package com.revolut.mts.http;

public enum HMethod {
   GET("GET"),
   POST("POST"),
   PUT("PUT"),

   UNKNOWN(null)
   ;

   private String method;
   HMethod(String method) {
      this.method = method;
   }
   public static HMethod map(String method) {
      for (var m : values()) {
         if (method.equals(m.method)) {
            return m;
         }
      }
      return UNKNOWN;
   }
}
