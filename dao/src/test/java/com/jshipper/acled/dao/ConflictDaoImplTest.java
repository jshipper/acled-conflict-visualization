package com.jshipper.acled.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

import com.jshipper.acled.dao.ConflictDaoImpl;
import com.jshipper.acled.model.Conflict;

public class ConflictDaoImplTest {
  public static final int NUM_RECORDS = 21;
  public static final int ACTOR1_MODULUS = 5;
  public static final int ACTOR2_MODULUS = 13;
  private static SessionFactory sessionFactory;
  private final ConflictDaoImpl dao = new ConflictDaoImpl(sessionFactory);
  private static final List<Conflict> conflicts;
  private static boolean isTestDataCreated = false;

  static {
    // Create some test records
    Calendar c = new GregorianCalendar(2015, 0, 1);
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
  public static void setup() {
    sessionFactory = getSessionFactory();
  }

  @AfterClass
  public static void close() {
    sessionFactory.close();
  }

  public static SessionFactory getSessionFactory() {
    Configuration config = new Configuration();
    Properties props = new Properties();
    // Hibernate properties
    props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
    props.put("hibernate.hbm2ddl.auto", "create");
    props.put("hibernate.current_session_context_class",
      "org.hibernate.context.internal.ThreadLocalSessionContext");
    // Connection properties
    props.put("hibernate.connection.username", "root");
    props.put("hibernate.connection.password", "mysqlnotsecure");
    props.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
    props.put("hibernate.connection.url", "jdbc:mysql://localhost/test");
    // Debugging properties
    props.put("hibernate.show_sql", "true");
    props.put("hibernate.format_sql", "true");
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
    Calendar c = new GregorianCalendar(2015, 0, 1);
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    List<Conflict> retrievedConflicts = dao.getConflictsByDate(c.getTime());
    tx.commit();
    for (Conflict conflict : retrievedConflicts) {
      Calendar conflictCal = new GregorianCalendar();
      conflictCal.setTime(conflict.getDate());
      assertEquals(c.get(Calendar.DAY_OF_YEAR),
        conflictCal.get(Calendar.DAY_OF_YEAR));
      assertEquals(c.get(Calendar.YEAR), conflictCal.get(Calendar.YEAR));
    }
  }

  @Test
  public void testGetConflictsInDateRange() {
    Calendar startDateCal = new GregorianCalendar(2015, 0, 1);
    // NOTE: This end date may not be valid if NUM_RECORDS is changed
    Calendar endDateCal = new GregorianCalendar(2015, 0, 5);
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
    // NOTE: This test will break if NUM_RECORDS set to less than 2
    List<Conflict> retrievedConflicts =
      dao.getConflictsInFatalityRange(0, NUM_RECORDS - 2);
    tx.commit();
    assertEquals(NUM_RECORDS - 1, retrievedConflicts.size());
    for (Conflict conflict : retrievedConflicts) {
      assertTrue(conflict.getFatalities() >= 0
        && conflict.getFatalities() <= NUM_RECORDS - 2);
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
