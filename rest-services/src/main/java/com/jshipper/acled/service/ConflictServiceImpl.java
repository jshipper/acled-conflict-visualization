package com.jshipper.acled.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import com.jshipper.acled.dao.ConflictDao;
import com.jshipper.acled.model.Conflict;

/**
 * Implementation of service to interface between ACLED DAO and REST services
 * 
 * @author jshipper
 *
 */
@Transactional
public class ConflictServiceImpl implements ConflictService {
  private ConflictDao dao;

  @Inject
  public ConflictServiceImpl(ConflictDao dao) {
    this.dao = dao;
  }

  @Override
  public List<Conflict> getAllConflicts() {
    return dao.getAllConflicts();
  }

  @Override
  public List<Conflict> getConflictsByDate(Date date) {
    return dao.getConflictsByDate(date);
  }

  @Override
  public List<Conflict> getConflictsInDateRange(Date startDate, Date endDate) {
    return dao.getConflictsInDateRange(startDate, endDate);
  }

  @Override
  public List<Conflict> getConflictsByCountry(String country) {
    return dao.getConflictsByCountry(country);
  }

  @Override
  public List<Conflict> getConflictsByActor(String actor) {
    return dao.getConflictsByActor(actor);
  }

  @Override
  public List<Conflict> getConflictsByActors(String actor1, String actor2) {
    return dao.getConflictsByActors(actor1, actor2);
  }

  @Override
  public List<Conflict> getConflictsByFatalities(Integer fatalities) {
    return dao.getConflictsByFatalities(fatalities);
  }

  @Override
  public List<Conflict> getConflictsInFatalityRange(Integer lowEnd,
    Integer highEnd) {
    return dao.getConflictsInFatalityRange(lowEnd, highEnd);
  }

  @Override
  public List<Conflict> getConflictsByCriteria(Date startDate, Date endDate,
    String country, String actor1, String actor2, Integer lowEnd,
    Integer highEnd) {
    return dao.getConflictsByCriteria(startDate, endDate, country, actor1,
      actor2, lowEnd, highEnd);
  }

  @Override
  public List<String> getAllCountries() {
    return dao.getAllCountries();
  }

  @Override
  public List<String> getAllActors() {
    return dao.getAllActors();
  }

  @Override
  public void saveAll(Collection<Conflict> conflicts) {
    dao.saveAll(conflicts);
  }

  @Override
  public void deleteAll() {
    dao.deleteAll();
  }
}
