package com.jshipper.acled.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import com.jshipper.acled.rest.ConflictResource;

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
    register(ConflictServiceConfig.class);
    register(ConflictResource.class);
    setApplicationName("acled");
  }
}
