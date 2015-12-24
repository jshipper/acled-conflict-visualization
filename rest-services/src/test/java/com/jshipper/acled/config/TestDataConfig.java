package com.jshipper.acled.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jshipper.acled.model.Conflict;

/**
 * Sets up and persists test data
 * 
 * @author jshipper
 *
 */
@Configuration
public class TestDataConfig {
  public static final int NUM_RECORDS = 21;
  public static final int ACTOR1_MODULUS = 5;
  public static final int ACTOR2_MODULUS = 13;
  public static List<Conflict> CONFLICTS;
  public static final Calendar initialDate = new GregorianCalendar(2015, 0, 1);
  public static final Calendar finalDate =
    new GregorianCalendar(2015, 0, NUM_RECORDS);

  @Bean
  public boolean createTestData(SessionFactory sessionFactory) {
    CONFLICTS = new ArrayList<>();
    Session session = sessionFactory.openSession();
    session.getTransaction().begin();
    // Create some test records
    Calendar c = new GregorianCalendar();
    c.setTime(initialDate.getTime());
    for (int i = 0; i < NUM_RECORDS; i++) {
      Conflict conflict = new Conflict();
      conflict.setId(new Long(i));
      conflict.setCountry("Country " + i);
      conflict.setActor1("Actor " + (i % ACTOR1_MODULUS));
      conflict.setActor2("Actor " + ((i + 1) % ACTOR2_MODULUS));
      conflict.setDate(c.getTime());
      c.add(Calendar.DAY_OF_YEAR, 1);
      conflict.setFatalities(i);
      CONFLICTS.add(conflict);
      session.persist(conflict);
    }
    session.flush();
    session.getTransaction().commit();
    session.close();
    return true;
  }
}
