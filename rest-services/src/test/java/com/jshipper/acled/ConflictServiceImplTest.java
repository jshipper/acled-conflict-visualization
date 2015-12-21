package com.jshipper.acled;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.ResourcePropertySource;

public class ConflictServiceImplTest {
  private static AnnotationConfigApplicationContext context;

  @BeforeClass
  public static void setup() throws IOException {
    PropertiesPropertySource props =
      new ResourcePropertySource("classpath:/test-app.properties");
    context = new AnnotationConfigApplicationContext();
    context.register(ConflictDaoConfig.class);
    context.register(TestDataConfig.class);
    context.getEnvironment().getPropertySources().addLast(props);
    context.refresh();
  }

  @AfterClass
  public static void close() {
    context.close();
  }

  @Test
  public void testGetAllConflicts() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<Conflict> retrievedConflicts = conflictService.getAllConflicts();
    assertEquals(TestDataConfig.NUM_RECORDS, retrievedConflicts.size());
    assertEquals(TestDataConfig.CONFLICTS, retrievedConflicts);
  }

  @Test
  public void testGetConflictsByDate() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    Calendar c = Calendar.getInstance();
    c.set(2015, 0, 1, 0, 0, 0);
    List<Conflict> retrievedConflicts =
      conflictService.getConflictsByDate(c.getTime());
    for (Conflict conflict : retrievedConflicts) {
      Calendar conflictCal = Calendar.getInstance();
      conflictCal.setTime(conflict.getDate());
      assertEquals(c.get(Calendar.DAY_OF_YEAR),
        conflictCal.get(Calendar.DAY_OF_YEAR));
      assertEquals(c.get(Calendar.YEAR), conflictCal.get(Calendar.YEAR));
    }
  }

  @Test
  public void testGetConflictsInDateRange() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    Calendar startDateCal = Calendar.getInstance();
    startDateCal.set(2015, 0, 1, 0, 0, 0);
    // NOTE: This end date may not be valid if NUM_RECORDS is changed
    Calendar endDateCal = Calendar.getInstance();
    endDateCal.set(2015, 0, 5, 0, 0, 0);
    List<Conflict> retrievedConflicts = conflictService
      .getConflictsInDateRange(startDateCal.getTime(), endDateCal.getTime());
    assertEquals(
      endDateCal.get(Calendar.DAY_OF_YEAR)
        - startDateCal.get(Calendar.DAY_OF_YEAR) + 1,
      retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      Calendar conflictCal = Calendar.getInstance();
      conflictCal.setTime(conflict.getDate());
      assertTrue(
        conflictCal.get(Calendar.YEAR) >= startDateCal.get(Calendar.YEAR)
          && conflictCal.get(Calendar.YEAR) <= endDateCal.get(Calendar.YEAR)
          && conflictCal.get(Calendar.DAY_OF_YEAR) >= startDateCal
            .get(Calendar.DAY_OF_YEAR)
        && conflictCal.get(Calendar.DAY_OF_YEAR) <= endDateCal
          .get(Calendar.DAY_OF_YEAR));
    }
  }

  @Test
  public void testGetConflictsByCountry() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<Conflict> retrievedConflicts =
      conflictService.getConflictsByCountry("Country 0");
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue("Country 0".equalsIgnoreCase(conflict.getCountry()));
    }
    List<Conflict> retrievedConflicts2 =
      conflictService.getConflictsByCountry("counTry 0");
    assertEquals(1, retrievedConflicts2.size());
    for (Conflict conflict : retrievedConflicts2) {
      assertTrue("counTry 0".equalsIgnoreCase(conflict.getCountry()));
    }
    assertEquals(retrievedConflicts, retrievedConflicts2);
  }

  @Test
  public void testGetConflictsByActor() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<Conflict> retrievedConflicts =
      conflictService.getConflictsByActor("Actor 0");
    assertEquals(
      (int) TestDataConfig.NUM_RECORDS / TestDataConfig.ACTOR1_MODULUS
        + (int) TestDataConfig.NUM_RECORDS / TestDataConfig.ACTOR2_MODULUS + 1,
      retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()));
    }
    List<Conflict> retrievedConflicts2 =
      conflictService.getConflictsByActor("acTor 0");
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
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<Conflict> retrievedConflicts =
      conflictService.getConflictsByActors("Actor 0", "Actor 1");
    // NOTE: This expected size could change if NUM_RECORDS or moduli are
    // changed
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()))
        && ("Actor 1".equalsIgnoreCase(conflict.getActor1())
          || "Actor 1".equalsIgnoreCase(conflict.getActor2())));
    }
    List<Conflict> retrievedConflicts2 =
      conflictService.getConflictsByActors("acTor 0", "ActOR 1");
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
  }

  @Test
  public void testGetConflictsByFatalities() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<Conflict> retrievedConflicts =
      conflictService.getConflictsByFatalities(0);
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() == 0);
    }
  }

  @Test
  public void testGetConflictsInFatalityRange() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    // NOTE: This test will break if NUM_RECORDS set to less than 2
    List<Conflict> retrievedConflicts = conflictService
      .getConflictsInFatalityRange(0, TestDataConfig.NUM_RECORDS - 2);
    assertEquals(TestDataConfig.NUM_RECORDS - 1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 0
        && conflict.getFatalities() <= TestDataConfig.NUM_RECORDS - 2);
    }
  }

  @Test
  public void testGetAllCountries() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<String> retrievedCountries = conflictService.getAllCountries();
    assertEquals(TestDataConfig.NUM_RECORDS, retrievedCountries.size());
    for (int i = 0; i < retrievedCountries.size(); i++) {
      assertEquals("Country " + i, retrievedCountries.get(i));
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void testGetAllActors() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<String> retrievedActors = conflictService.getAllActors();
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
