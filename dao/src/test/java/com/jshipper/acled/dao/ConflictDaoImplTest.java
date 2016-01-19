package com.jshipper.acled.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jshipper.acled.model.Conflict;

public class ConflictDaoImplTest {
  public static final int NUM_RECORDS = 21;
  public static final int ACTOR1_MODULUS = 5;
  public static final int ACTOR2_MODULUS = 13;
  private static SessionFactory sessionFactory;
  private final ConflictDaoImpl dao = new ConflictDaoImpl(sessionFactory);
  private static final List<Conflict> conflicts;
  private static final Calendar initialDate = new GregorianCalendar(2015, 0, 1);
  private static final Calendar finalDate =
    new GregorianCalendar(2015, 0, NUM_RECORDS);
  private static boolean isTestDataCreated = false;

  static {
    // Create some test records
    Calendar c = new GregorianCalendar();
    c.setTime(initialDate.getTime());
    conflicts = new ArrayList<>();
    for (int i = 0; i < NUM_RECORDS; i++) {
      Conflict conflict = new Conflict();
      conflict.setId(new Long(i));
      conflict.setCountry("Country " + i);
      conflict.setActor1("Actor " + (i % ACTOR1_MODULUS));
      conflict.setActor2("Actor " + ((i + 1) % ACTOR2_MODULUS));
      conflict.setDate(c.getTime());
      c.add(Calendar.DAY_OF_YEAR, 1);
      conflict.setFatalities(i);
      conflicts.add(conflict);
    }
  }

  @BeforeClass
  public static void setup() throws IOException {
    sessionFactory = getSessionFactory();
  }

  @AfterClass
  public static void close() {
    sessionFactory.close();
  }

  public static SessionFactory getSessionFactory() throws IOException {
    Configuration config = new Configuration();
    Properties props = new Properties();
    props.load(
      ConflictDaoImplTest.class.getResourceAsStream("/test-app.properties"));
    config.addPackage("com.jshipper.acled.model").setProperties(props)
      .addAnnotatedClass(Conflict.class);
    return config.buildSessionFactory(
      new StandardServiceRegistryBuilder().applySettings(props).build());
  }

  @Before
  public void setupTest() {
    if (!isTestDataCreated) {
      // Clear out table
      Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
      dao.deleteAll();
      tx.commit();
      // Save records to table
      tx = sessionFactory.getCurrentSession().beginTransaction();
      dao.saveAll(conflicts);
      tx.commit();
      isTestDataCreated = true;
    }
  }

  @Test
  public void testGetAllConflicts() {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts = dao.getAllConflicts();
    tx.commit();
    assertEquals(NUM_RECORDS, retrievedConflicts.size());
    assertEquals(conflicts, retrievedConflicts);
  }

  @Test
  public void testGetConflictsByDate() {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts =
      dao.getConflictsByDate(initialDate.getTime());
    tx.commit();
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
    // NOTE: These dates may not be valid if NUM_RECORDS is changed
    Calendar startDateCal = new GregorianCalendar(2015, 0, 6);
    Calendar endDateCal = new GregorianCalendar(2015, 0, 10);
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts =
      dao.getConflictsInDateRange(startDateCal.getTime(), endDateCal.getTime());
    tx.commit();
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
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts =
      dao.getConflictsInDateRange(null, endDateCal.getTime());
    tx.commit();
    assertEquals(endDateCal.get(Calendar.DAY_OF_YEAR)
      - initialDate.get(Calendar.DAY_OF_YEAR) + 1, retrievedConflicts.size());
    // Test with end date null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts =
      dao.getConflictsInDateRange(startDateCal.getTime(), null);
    tx.commit();
    assertEquals(
      finalDate.get(Calendar.DAY_OF_YEAR)
        - startDateCal.get(Calendar.DAY_OF_YEAR) + 1,
      retrievedConflicts.size());
    // Test with both null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsInDateRange(null, null);
    tx.commit();
    assertEquals(0, retrievedConflicts.size());
    // Test with end date before start date
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts =
      dao.getConflictsInDateRange(endDateCal.getTime(), startDateCal.getTime());
    tx.commit();
    assertEquals(0, retrievedConflicts.size());
  }

  @Test
  public void testGetConflictsByCountry() {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts = dao.getConflictsByCountry("Country 0");
    tx.commit();
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue("Country 0".equalsIgnoreCase(conflict.getCountry()));
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts2 = dao.getConflictsByCountry("counTry 0");
    tx.commit();
    assertEquals(1, retrievedConflicts2.size());
    for (Conflict conflict : retrievedConflicts2) {
      assertTrue("counTry 0".equalsIgnoreCase(conflict.getCountry()));
    }
    assertEquals(retrievedConflicts, retrievedConflicts2);
  }

  @Test
  public void testGetConflictsByActor() {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts = dao.getConflictsByActor("Actor 0");
    tx.commit();
    assertEquals((int) NUM_RECORDS / ACTOR1_MODULUS
      + (int) NUM_RECORDS / ACTOR2_MODULUS + 1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()));
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts2 = dao.getConflictsByActor("acTor 0");
    tx.commit();
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
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts =
      dao.getConflictsByActors("Actor 0", "Actor 1");
    tx.commit();
    // NOTE: This expected size could change if NUM_RECORDS or moduli are
    // changed
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()))
        && ("Actor 1".equalsIgnoreCase(conflict.getActor1())
          || "Actor 1".equalsIgnoreCase(conflict.getActor2())));
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts2 =
      dao.getConflictsByActors("acTor 0", "ActOR 1");
    tx.commit();
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
    tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts3 =
      dao.getConflictsByActors("ActOR 1", "acTor 0");
    tx.commit();
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
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts = dao.getConflictsByFatalities(0);
    tx.commit();
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() == 0);
    }
  }

  @Test
  public void testGetConflictsInFatalityRange() {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    // NOTE: This test will break if NUM_RECORDS set to less than 3
    List<Conflict> retrievedConflicts =
      dao.getConflictsInFatalityRange(1, NUM_RECORDS - 2);
    tx.commit();
    assertEquals(NUM_RECORDS - 2, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 1
        && conflict.getFatalities() <= NUM_RECORDS - 2);
    }
    // Test with low end null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsInFatalityRange(null, NUM_RECORDS - 1);
    tx.commit();
    assertEquals(NUM_RECORDS, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() <= NUM_RECORDS - 1);
    }
    // Test with high end null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsInFatalityRange(1, null);
    tx.commit();
    assertEquals(NUM_RECORDS - 1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 1);
    }
    // Test with both null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsInFatalityRange(null, null);
    tx.commit();
    assertEquals(0, retrievedConflicts.size());
    // Test with high end before low end
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsInFatalityRange(2, 1);
    tx.commit();
    assertEquals(0, retrievedConflicts.size());
  }

  @Test
  public void testGetConflictsByCriteria() {
    // Tests with dates
    // NOTE: These dates may not be valid if NUM_RECORDS is changed
    Calendar startDateCal = new GregorianCalendar(2015, 0, 6);
    Calendar endDateCal = new GregorianCalendar(2015, 0, 10);
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts =
      dao.getConflictsByCriteria(startDateCal.getTime(), endDateCal.getTime(),
        null, null, null, null, null);
    tx.commit();
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
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(null, endDateCal.getTime(),
      null, null, null, null, null);
    tx.commit();
    assertEquals(endDateCal.get(Calendar.DAY_OF_YEAR)
      - initialDate.get(Calendar.DAY_OF_YEAR) + 1, retrievedConflicts.size());
    // Test with end date null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(startDateCal.getTime(),
      null, null, null, null, null, null);
    tx.commit();
    assertEquals(
      finalDate.get(Calendar.DAY_OF_YEAR)
        - startDateCal.get(Calendar.DAY_OF_YEAR) + 1,
      retrievedConflicts.size());
    // Test with both null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts =
      dao.getConflictsByCriteria(null, null, null, null, null, null, null);
    tx.commit();
    assertEquals(0, retrievedConflicts.size());
    // Test with end date before start date
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(endDateCal.getTime(),
      startDateCal.getTime(), null, null, null, null, null);
    tx.commit();
    assertEquals(0, retrievedConflicts.size());

    // Tests with country name
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(null, null, "Country 0",
      null, null, null, null);
    tx.commit();
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue("Country 0".equalsIgnoreCase(conflict.getCountry()));
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts2 = dao.getConflictsByCriteria(null, null,
      "counTry 0", null, null, null, null);
    tx.commit();
    assertEquals(1, retrievedConflicts2.size());
    for (Conflict conflict : retrievedConflicts2) {
      assertTrue("counTry 0".equalsIgnoreCase(conflict.getCountry()));
    }
    assertEquals(retrievedConflicts, retrievedConflicts2);

    // Tests with actor names
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(null, null, null, "Actor 0",
      "Actor 1", null, null);
    tx.commit();
    // NOTE: This expected size could change if NUM_RECORDS or moduli are
    // changed
    assertEquals(1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()))
        && ("Actor 1".equalsIgnoreCase(conflict.getActor1())
          || "Actor 1".equalsIgnoreCase(conflict.getActor2())));
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts2 = dao.getConflictsByCriteria(null, null, null,
      "acTor 0", "ActOR 1", null, null);
    tx.commit();
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
    tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts3 = dao.getConflictsByCriteria(null, null,
      null, "ActOR 1", "acTor 0", null, null);
    tx.commit();
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

    // Tests with fatalities
    tx = sessionFactory.getCurrentSession().beginTransaction();
    // NOTE: This test will break if NUM_RECORDS set to less than 3
    retrievedConflicts = dao.getConflictsByCriteria(null, null, null, null,
      null, 1, NUM_RECORDS - 2);
    tx.commit();
    assertEquals(NUM_RECORDS - 2, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 1
        && conflict.getFatalities() <= NUM_RECORDS - 2);
    }
    // Test with low end null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(null, null, null, null,
      null, null, NUM_RECORDS - 1);
    tx.commit();
    assertEquals(NUM_RECORDS, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() <= NUM_RECORDS - 1);
    }
    // Test with high end null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts =
      dao.getConflictsByCriteria(null, null, null, null, null, 1, null);
    tx.commit();
    assertEquals(NUM_RECORDS - 1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 1);
    }
    // Test with both null
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts =
      dao.getConflictsByCriteria(null, null, null, null, null, null, null);
    tx.commit();
    assertEquals(0, retrievedConflicts.size());
    // Test with high end before low end
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts =
      dao.getConflictsByCriteria(null, null, null, null, null, 2, 1);
    tx.commit();
    assertEquals(0, retrievedConflicts.size());

    // Tests with criteria combinations
    // NOTE: These tests are very brittle, tied to values of constants
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(startDateCal.getTime(),
      endDateCal.getTime(), "Country 5", null, null, null, null);
    tx.commit();
    assertEquals(1, retrievedConflicts.size());
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
      assertEquals("Country 5", conflict.getCountry());
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(startDateCal.getTime(),
      endDateCal.getTime(), null, "Actor 0", "Actor 6", null, null);
    tx.commit();
    assertEquals(1, retrievedConflicts.size());
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
      assertTrue("Actor 0".equalsIgnoreCase(conflict.getActor1())
        || "Actor 0".equalsIgnoreCase(conflict.getActor2()));
      assertTrue("Actor 6".equalsIgnoreCase(conflict.getActor1())
        || "Actor 6".equalsIgnoreCase(conflict.getActor2()));
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(startDateCal.getTime(),
      endDateCal.getTime(), null, null, null, 5, 6);
    tx.commit();
    assertEquals(2, retrievedConflicts.size());
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
      assertTrue(
        conflict.getFatalities() >= 5 && conflict.getFatalities() <= 6);
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(startDateCal.getTime(),
      endDateCal.getTime(), "Country 5", null, null, 5, 6);
    tx.commit();
    assertEquals(1, retrievedConflicts.size());
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
      assertEquals("Country 5", conflict.getCountry());
      assertTrue(
        conflict.getFatalities() >= 5 && conflict.getFatalities() <= 6);
    }
    tx = sessionFactory.getCurrentSession().beginTransaction();
    retrievedConflicts = dao.getConflictsByCriteria(startDateCal.getTime(),
      endDateCal.getTime(), "Country 7", "Actor 2", "Actor 8", 5, 8);
    tx.commit();
    assertEquals(1, retrievedConflicts.size());
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
      assertEquals("Country 7", conflict.getCountry());
      assertTrue(("Actor 2".equalsIgnoreCase(conflict.getActor1())
        || "Actor 2".equalsIgnoreCase(conflict.getActor2()))
        && ("Actor 8".equalsIgnoreCase(conflict.getActor1())
          || "Actor 8".equalsIgnoreCase(conflict.getActor2())));
      assertTrue(
        conflict.getFatalities() >= 5 && conflict.getFatalities() <= 8);
    }
  }

  @Test
  public void testGetAllCountries() {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<String> retrievedCountries = dao.getAllCountries();
    tx.commit();
    assertEquals(NUM_RECORDS, retrievedCountries.size());
    for (int i = 0; i < retrievedCountries.size(); i++) {
      assertEquals("Country " + i, retrievedCountries.get(i));
    }
  }

  @SuppressWarnings("unused")
  @Test
  public void testGetAllActors() {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<String> retrievedActors = dao.getAllActors();
    tx.commit();
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
