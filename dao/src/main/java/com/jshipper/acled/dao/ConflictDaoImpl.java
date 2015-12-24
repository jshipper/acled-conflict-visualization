package com.jshipper.acled.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.jshipper.acled.model.Conflict;

/**
 * Data access object (DAO) implementation for the ACLED dataset
 * 
 * @author jshipper
 *
 */
public class ConflictDaoImpl implements ConflictDao {
  public static final int BATCH_SIZE = 20;
  private SessionFactory sessionFactory;

  public ConflictDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Conflict> getAllConflicts() {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.ALL_QUERY);
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Conflict> getConflictsByDate(Date date) {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.BY_DATE_QUERY);
    query.setDate("date", date);
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Conflict> getConflictsInDateRange(Date startDate, Date endDate) {
    if (startDate == null && endDate == null
      || (startDate != null && endDate != null && endDate.before(startDate))) {
      return Collections.EMPTY_LIST;
    }
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.IN_DATE_RANGE_QUERY);
    query.setDate("startDate", startDate);
    query.setDate("endDate", endDate);
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Conflict> getConflictsByCountry(String country) {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.BY_COUNTRY_QUERY);
    query.setString("country", country);
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Conflict> getConflictsByActor(String actor) {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.BY_ACTOR_QUERY);
    query.setString("actor", actor);
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Conflict> getConflictsByActors(String actor1, String actor2) {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.BY_ACTORS_QUERY);
    query.setString("actor1", actor1);
    query.setString("actor2", actor2);
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Conflict> getConflictsByFatalities(Integer fatalities) {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.BY_FATALITIES_QUERY);
    query.setInteger("fatalities", fatalities);
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Conflict> getConflictsInFatalityRange(Integer lowEnd,
    Integer highEnd) {
    if (lowEnd == null && highEnd == null
      || (lowEnd != null && highEnd != null && highEnd < lowEnd)) {
      return Collections.EMPTY_LIST;
    }
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.IN_FATALITY_RANGE_QUERY);
    if (lowEnd != null) {
      query.setInteger("lowEnd", lowEnd);
    } else {
      query.setParameter("lowEnd", null);
    }
    if (highEnd != null) {
      query.setInteger("highEnd", highEnd);
    } else {
      query.setParameter("highEnd", null);
    }
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getAllCountries() {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.ALL_COUNTRIES_QUERY);
    return query.list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getAllActors() {
    // TODO: Case insensitive building of list
    Session session = sessionFactory.getCurrentSession();
    Query queryActor1s = session.getNamedQuery(Conflict.ALL_ACTOR1S_QUERY);
    List<String> actors = queryActor1s.list();
    Query queryActor2s = session.getNamedQuery(Conflict.ALL_ACTOR2S_QUERY);
    List<String> actor2s = queryActor2s.list();
    for (String actor : actor2s) {
      if (!actors.contains(actor)) {
        actors.add(actor);
      }
    }
    return actors;
  }

  @Override
  public void saveAll(Collection<Conflict> conflicts) {
    Session session = sessionFactory.getCurrentSession();
    int i = 0;
    for (Conflict conflict : conflicts) {
      session.save(conflict);
      i++;
      if (i % BATCH_SIZE == 0) {
        session.flush();
        session.clear();
      }
    }
    session.flush();
  }

  @Override
  public void deleteAll() {
    Session session = sessionFactory.getCurrentSession();
    Query query = session.getNamedQuery(Conflict.ALL_QUERY);
    ScrollableResults results = query.setFlushMode(FlushMode.MANUAL).scroll();
    int i = 0;
    while (results.next()) {
      Conflict conflict = (Conflict) results.get(0);
      session.delete(conflict);
      i++;
      if (i % BATCH_SIZE == 0) {
        session.flush();
        session.clear();
      }
    }
    session.flush();
  }
}
