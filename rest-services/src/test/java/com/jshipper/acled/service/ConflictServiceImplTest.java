package com.jshipper.acled.service;

import static com.jshipper.acled.config.TestDataConfig.ACTOR1_MODULUS;
import static com.jshipper.acled.config.TestDataConfig.ACTOR2_MODULUS;
import static com.jshipper.acled.config.TestDataConfig.CONFLICTS;
import static com.jshipper.acled.config.TestDataConfig.NUM_RECORDS;
import static com.jshipper.acled.config.TestDataConfig.finalDate;
import static com.jshipper.acled.config.TestDataConfig.initialDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.jshipper.acled.config.ConflictServiceConfig;
import com.jshipper.acled.config.TestDataConfig;
import com.jshipper.acled.model.Conflict;

public class ConflictServiceImplTest {
  private static AnnotationConfigApplicationContext context;

  @BeforeClass
  public static void setup() throws IOException {
    context = new AnnotationConfigApplicationContext();
    context.register(ConflictServiceConfig.class);
    context.register(TestDataConfig.class);
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
    assertEquals(NUM_RECORDS, retrievedConflicts.size());
    assertEquals(CONFLICTS, retrievedConflicts);
  }

  @Test
  public void testGetConflictsByDate() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<Conflict> retrievedConflicts =
      conflictService.getConflictsByDate(initialDate.getTime());
    for (Conflict conflict : retrievedConflicts) {
      Calendar conflictCal = new GregorianCalendar();
      conflictCal.setTime(conflict.getDate());
      assertEquals(initialDate.get(Calendar.DAY_OF_YEAR),
        conflictCal.get(Calendar.DAY_OF_YEAR));
      assertEquals(initialDate.get(Calendar.YEAR),
        conflictCal.get(Calendar.YEAR));
    }
  }

  @Test
  public void testGetConflictsInDateRange() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    // NOTE: These dates may not be valid if NUM_RECORDS is changed
    Calendar startDateCal = new GregorianCalendar(2015, 0, 6);
    Calendar endDateCal = new GregorianCalendar(2015, 0, 10);
    List<Conflict> retrievedConflicts = conflictService
      .getConflictsInDateRange(startDateCal.getTime(), endDateCal.getTime());
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
    // Test with start date null
    retrievedConflicts =
      conflictService.getConflictsInDateRange(null, endDateCal.getTime());
    assertEquals(endDateCal.get(Calendar.DAY_OF_YEAR)
      - initialDate.get(Calendar.DAY_OF_YEAR) + 1, retrievedConflicts.size());
    // Test with end date null
    retrievedConflicts =
      conflictService.getConflictsInDateRange(startDateCal.getTime(), null);
    assertEquals(
      finalDate.get(Calendar.DAY_OF_YEAR)
        - startDateCal.get(Calendar.DAY_OF_YEAR) + 1,
      retrievedConflicts.size());
    // Test with both null
    retrievedConflicts = conflictService.getConflictsInDateRange(null, null);
    assertEquals(0, retrievedConflicts.size());
    // Test with end date before start date
    retrievedConflicts = conflictService
      .getConflictsInDateRange(endDateCal.getTime(), startDateCal.getTime());
    assertEquals(0, retrievedConflicts.size());
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
    assertEquals((int) NUM_RECORDS / ACTOR1_MODULUS
      + (int) NUM_RECORDS / ACTOR2_MODULUS + 1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()));
    }
    List<Conflict> retrievedConflicts2 =
      conflictService.getConflictsByActor("acTor 0");
    assertEquals((int) NUM_RECORDS / ACTOR1_MODULUS
      + (int) NUM_RECORDS / ACTOR2_MODULUS + 1, retrievedConflicts2.size());
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
    List<Conflict> retrievedConflicts3 =
      conflictService.getConflictsByActors("ActOR 1", "acTor 0");
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
    // NOTE: This test will break if NUM_RECORDS set to less than 3
    List<Conflict> retrievedConflicts =
      conflictService.getConflictsInFatalityRange(1, NUM_RECORDS - 2);
    assertEquals(NUM_RECORDS - 2, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 1
        && conflict.getFatalities() <= NUM_RECORDS - 2);
    }
    // Test with low end null
    retrievedConflicts =
      conflictService.getConflictsInFatalityRange(null, NUM_RECORDS - 1);
    assertEquals(NUM_RECORDS, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() <= NUM_RECORDS - 1);
    }
    // Test with high end null
    retrievedConflicts = conflictService.getConflictsInFatalityRange(1, null);
    assertEquals(NUM_RECORDS - 1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 1);
    }
    // Test with both null
    retrievedConflicts =
      conflictService.getConflictsInFatalityRange(null, null);
    assertEquals(0, retrievedConflicts.size());
    // Test with high end before low end
    retrievedConflicts = conflictService.getConflictsInFatalityRange(2, 1);
    assertEquals(0, retrievedConflicts.size());
  }

  @Test
  public void testGetAllCountries() {
    ConflictService conflictService = context.getBean(ConflictService.class);
    List<String> retrievedCountries = conflictService.getAllCountries();
    assertEquals(NUM_RECORDS, retrievedCountries.size());
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
    if (ACTOR1_MODULUS > ACTOR2_MODULUS) {
      numRecords = ACTOR1_MODULUS;
    } else {
      numRecords = ACTOR2_MODULUS;
    }
    assertEquals(numRecords, retrievedActors.size());
    for (int i = 0; i < retrievedActors.size(); i++) {
      assertEquals("Actor " + i, retrievedActors.get(i));
    }
  }
}
