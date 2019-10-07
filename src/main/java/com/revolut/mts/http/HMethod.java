package com.revolut.mts.http;

/**
 * The only used HTTP methods
 */
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

   /**
    * Find the method from a given string
    * @param method Method in string representation
    * @return Found method
    */
   public static HMethod map(String method) {
      for (var m : values()) {
         if (method.equals(m.method)) {
            return m;
         }
      }
      return UNKNOWN;
   }
}
