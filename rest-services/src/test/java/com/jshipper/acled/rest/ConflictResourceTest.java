package com.jshipper.acled.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jshipper.acled.config.ConflictServiceConfig;
import com.jshipper.acled.config.ConflictResourceConfig;
import com.jshipper.acled.config.TestDataConfig;
import com.jshipper.acled.model.Conflict;
import com.jshipper.acled.rest.ConflictResource;

public class ConflictResourceTest extends JerseyTest {
  private ObjectMapper mapper;

  @Override
  public Application configure() {
    mapper = new ObjectMapper();
    mapper.setDateFormat(new SimpleDateFormat(Conflict.DATE_FORMAT));
    ConflictResourceConfig config = new ConflictResourceConfig();
    PropertiesPropertySource props = null;
    try {
      props = new ResourcePropertySource("classpath:/test-app.properties");
    } catch (IOException e) {
      e.printStackTrace();
    }
    AnnotationConfigApplicationContext context =
      new AnnotationConfigApplicationContext();
    context.register(ConflictServiceConfig.class);
    context.register(TestDataConfig.class);
    context.getEnvironment().getPropertySources().addLast(props);
    context.refresh();
    config.property("contextConfig", context);
    return config;
  }

  @Test
  public void getAll() {
    JsonNode jsonResponse =
      target(ConflictResource.PATH + "/getAll").request().get(JsonNode.class);
    List<Conflict> retrievedConflicts =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    assertEquals(TestDataConfig.NUM_RECORDS, retrievedConflicts.size());
    assertEquals(TestDataConfig.CONFLICTS, retrievedConflicts);
  }

  @Test
  public void testGetConflictsByDate() throws IOException {
    Calendar c = new GregorianCalendar(2015, 0, 1);
    DateFormat dateFormat = new SimpleDateFormat(Conflict.DATE_FORMAT);
    JsonNode jsonResponse =
      target(ConflictResource.PATH + "/getConflictsByDate/"
        + dateFormat.format(c.getTime())).request().get(JsonNode.class);
    List<Conflict> retrievedConflicts =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    for (Conflict conflict : retrievedConflicts) {
      Calendar conflictCal = new GregorianCalendar();
      conflictCal.setTime(conflict.getDate());
      assertEquals(c.get(Calendar.DAY_OF_YEAR),
        conflictCal.get(Calendar.DAY_OF_YEAR));
      assertEquals(c.get(Calendar.YEAR), conflictCal.get(Calendar.YEAR));
    }
    // Test for BAD_REQUEST response
    Response response =
      target(ConflictResource.PATH + "/getConflictsByDate/20150101").request()
        .get(Response.class);
    assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    StringWriter responseContent = new StringWriter();
    IOUtils.copy((InputStream) response.getEntity(), responseContent);
    assertEquals("Date not in expected format: " + Conflict.DATE_FORMAT,
      responseContent.toString());
  }

  @Test
  public void testGetConflictsInDateRange() throws IOException {
    Calendar startDateCal = new GregorianCalendar(2015, 0, 1);
    // NOTE: This end date may not be valid if NUM_RECORDS is changed
    Calendar endDateCal = new GregorianCalendar(2015, 0, 5);
    DateFormat dateFormat = new SimpleDateFormat(Conflict.DATE_FORMAT);
    JsonNode jsonResponse =
      target(ConflictResource.PATH + "/getConflictsInDateRange")
        .queryParam("startDate", dateFormat.format(startDateCal.getTime()))
        .queryParam("endDate", dateFormat.format(endDateCal.getTime()))
        .request().get(JsonNode.class);
    List<Conflict> retrievedConflicts =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    assertEquals(
      endDateCal.get(Calendar.DAY_OF_YEAR)
        - startDateCal.get(Calendar.DAY_OF_YEAR) + 1,
      retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      Calendar conflictCal = new GregorianCalendar();
      conflictCal.setTime(conflict.getDate());
      assertTrue(
        conflictCal.get(Calendar.YEAR) >= startDateCal.get(Calendar.YEAR)
          && conflictCal.get(Calendar.YEAR) <= endDateCal.get(Calendar.YEAR)
          && conflictCal.get(Calendar.DAY_OF_YEAR) >= startDateCal
            .get(Calendar.DAY_OF_YEAR)
        && conflictCal.get(Calendar.DAY_OF_YEAR) <= endDateCal
          .get(Calendar.DAY_OF_YEAR));
    }
    // Test for BAD_REQUEST responses
    Response response =
      target(ConflictResource.PATH + "/getConflictsInDateRange")
        .queryParam("startDate", "20150101")
        .queryParam("endDate", dateFormat.format(endDateCal.getTime()))
        .request().get(Response.class);
    assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    StringWriter responseContent = new StringWriter();
    IOUtils.copy((InputStream) response.getEntity(), responseContent);
    assertEquals("Start date not in expected format: " + Conflict.DATE_FORMAT,
      responseContent.toString());
    response = target(ConflictResource.PATH + "/getConflictsInDateRange")
      .queryParam("startDate", dateFormat.format(startDateCal.getTime()))
      .queryParam("endDate", "20150105").request().get(Response.class);
    assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    responseContent = new StringWriter();
    IOUtils.copy((InputStream) response.getEntity(), responseContent);
    assertEquals("End date not in expected format: " + Conflict.DATE_FORMAT,
      responseContent.toString());
  }

  @Test
  public void testGetConflictsByCountry() {
    JsonNode jsonResponse =
      target(ConflictResource.PATH + "/getConflictsByCountry/Country 0")
        .request().get(JsonNode.class);
    List<Conflict> retrievedConflicts =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue("Country 0".equalsIgnoreCase(conflict.getCountry()));
    }
    jsonResponse =
      target(ConflictResource.PATH + "/getConflictsByCountry/counTry 0")
        .request().get(JsonNode.class);
    List<Conflict> retrievedConflicts2 =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    assertEquals(1, retrievedConflicts2.size());
    for (Conflict conflict : retrievedConflicts2) {
      assertTrue("counTry 0".equalsIgnoreCase(conflict.getCountry()));
    }
    assertEquals(retrievedConflicts, retrievedConflicts2);
  }

  @Test
  public void testGetConflictsByActor() {
    JsonNode jsonResponse =
      target(ConflictResource.PATH + "/getConflictsByActor/Actor 0").request()
        .get(JsonNode.class);
    List<Conflict> retrievedConflicts =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    assertEquals(
      (int) TestDataConfig.NUM_RECORDS / TestDataConfig.ACTOR1_MODULUS
        + (int) TestDataConfig.NUM_RECORDS / TestDataConfig.ACTOR2_MODULUS + 1,
      retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()));
    }
    jsonResponse =
      target(ConflictResource.PATH + "/getConflictsByActor/acTor 0").request()
        .get(JsonNode.class);
    List<Conflict> retrievedConflicts2 =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    assertEquals(
      (int) TestDataConfig.NUM_RECORDS / TestDataConfig.ACTOR1_MODULUS
        + (int) TestDataConfig.NUM_RECORDS / TestDataConfig.ACTOR2_MODULUS + 1,
      retrievedConflicts2.size());
    for (Conflict conflict : retrievedConflicts2) {
      assertTrue("acTor 0".equalsIgnoreCase(conflict.getActor1())
        || "acTor 0".equalsIgnoreCase(conflict.getActor2()));
    }
    assertEquals(retrievedConflicts, retrievedConflicts2);
  }

  @Test
  public void testGetConflictsByActors() {
    JsonNode jsonResponse =
      target(ConflictResource.PATH + "/getConflictsByActors")
        .queryParam("actor1", "Actor 0").queryParam("actor2", "Actor 1")
        .request().get(JsonNode.class);
    List<Conflict> retrievedConflicts =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    // NOTE: This expected size could change if NUM_RECORDS or moduli are
    // changed
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()))
        && ("Actor 1".equalsIgnoreCase(conflict.getActor1())
          || "Actor 1".equalsIgnoreCase(conflict.getActor2())));
    }
    jsonResponse = target(ConflictResource.PATH + "/getConflictsByActors")
      .queryParam("actor1", "acTor 0").queryParam("actor2", "ActOR 1").request()
      .get(JsonNode.class);
    List<Conflict> retrievedConflicts2 =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    // NOTE: This expected size could change if NUM_RECORDS or moduli are
    // changed
    assertEquals(1, retrievedConflicts2.size());
    for (Conflict conflict : retrievedConflicts2) {
      assertTrue(("acTor 0".equalsIgnoreCase(conflict.getActor1())
        || "acTor 0".equalsIgnoreCase(conflict.getActor2()))
        && ("ActOR 1".equalsIgnoreCase(conflict.getActor1())
          || "ActOR 1".equalsIgnoreCase(conflict.getActor2())));
    }
    assertEquals(retrievedConflicts, retrievedConflicts2);
    jsonResponse = target(ConflictResource.PATH + "/getConflictsByActors")
      .queryParam("actor1", "ActOR 1").queryParam("actor2", "acTor 0").request()
      .get(JsonNode.class);
    List<Conflict> retrievedConflicts3 =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    // NOTE: This expected size could change if NUM_RECORDS or moduli are
    // changed
    assertEquals(1, retrievedConflicts3.size());
    for (Conflict conflict : retrievedConflicts3) {
      assertTrue(("acTor 0".equalsIgnoreCase(conflict.getActor1())
        || "acTor 0".equalsIgnoreCase(conflict.getActor2()))
        && ("ActOR 1".equalsIgnoreCase(conflict.getActor1())
          || "ActOR 1".equalsIgnoreCase(conflict.getActor2())));
    }
    assertEquals(retrievedConflicts, retrievedConflicts3);
    assertEquals(retrievedConflicts2, retrievedConflicts3);
  }

  @Test
  public void testGetConflictsByFatalities() {
    JsonNode jsonResponse =
      target(ConflictResource.PATH + "/getConflictsByFatalities/0").request()
        .get(JsonNode.class);
    List<Conflict> retrievedConflicts =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() == 0);
    }
  }

  @Test
  public void testGetConflictsInFatalityRange() {
    // NOTE: This test will break if NUM_RECORDS set to less than 2
    JsonNode jsonResponse =
      target(ConflictResource.PATH + "/getConflictsInFatalityRange")
        .queryParam("lowEnd", 0)
        .queryParam("highEnd", TestDataConfig.NUM_RECORDS - 2).request()
        .get(JsonNode.class);
    List<Conflict> retrievedConflicts =
      mapper.convertValue(jsonResponse, new TypeReference<List<Conflict>>() {
      });
    assertEquals(TestDataConfig.NUM_RECORDS - 1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 0
        && conflict.getFatalities() <= TestDataConfig.NUM_RECORDS - 2);
    }
  }

  @Test
  public void testGetAllCountries() {
    JsonNode jsonResponse = target(ConflictResource.PATH + "/getAllCountries")
      .request().get(JsonNode.class);
    List<String> retrievedCountries =
      mapper.convertValue(jsonResponse, new TypeReference<List<String>>() {
      });
    assertEquals(TestDataConfig.NUM_RECORDS, retrievedCountries.size());
    for (int i = 0; i < retrievedCountries.size(); i++) {
      assertEquals("Country " + i, retrievedCountries.get(i));
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void testGetAllActors() {
    JsonNode jsonResponse = target(ConflictResource.PATH + "/getAllActors")
      .request().get(JsonNode.class);
    List<String> retrievedActors =
      mapper.convertValue(jsonResponse, new TypeReference<List<String>>() {
      });
    int numRecords;
    if (TestDataConfig.ACTOR1_MODULUS > TestDataConfig.ACTOR2_MODULUS) {
      numRecords = TestDataConfig.ACTOR1_MODULUS;
    } else {
      numRecords = TestDataConfig.ACTOR2_MODULUS;
    }
    assertEquals(numRecords, retrievedActors.size());
    for (int i = 0; i < retrievedActors.size(); i++) {
      assertEquals("Actor " + i, retrievedActors.get(i));
    }
  }
}