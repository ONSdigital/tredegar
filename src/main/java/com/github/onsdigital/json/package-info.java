/**
 * This package contains the "data records" used for converting between Java and Json.
 * <p>
 * These classes are not "Objects" in the pure sense, because they have no internal state or function. 
 * <p>
 * Practically speaking, implementing them using the Java Beans pattern would generate 
 * large amounts of boilerplate code for no value: 
 * {@link com.google.gson.Gson} [de]serialises directly from fields.
 * <p>
 * So all fields in this package are public. 
 * If you're a Java purist, this might be making your toes curl but if you think about it, 
 * for the job being done here, accessors only add bloat 
 * (and could be automatically generated in future if needed).
 */
package com.github.onsdigital.json;

