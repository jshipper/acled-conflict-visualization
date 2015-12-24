package com.jshipper.acled;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * Configuration for ACLED dataset application
 * 
 * @author jshipper
 *
 */
public class ConflictResourceConfig extends ResourceConfig {
  public ConflictResourceConfig() {
    register(RequestContextFilter.class);
    register(JacksonFeature.class);
    packages("com.jshipper.acled");
  }
}
