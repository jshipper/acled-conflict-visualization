package com.jshipper.acled.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.jshipper.acled.dao.ConflictRepository;

import com.jshipper.acled.model.Conflict;
import org.springframework.stereotype.Component;

/**
 * Implementation of service to interface between ACLED DAO and REST services
 * 
 * @author jshipper
 *
 */
@Component
public class ConflictServiceImpl implements ConflictService {
  private ConflictRepository dao;

  @Inject
  public ConflictServiceImpl(ConflictRepository dao) {
    this.dao = dao;
  }

  @Override
  public List<Conflict> getAllConflicts() {
    return dao.findAll();
  }

  @Override
  public List<Conflict> getConflictsByDate(Date date) {
    return dao.findByDate(date);
  }

  @Override
  public List<Conflict> getConflictsInDateRange(Date startDate, Date endDate) {
    return dao.findByDateBetween(startDate, endDate);
  }

  @Override
  public List<Conflict> getConflictsByCountry(String country) {
    return dao.findByCountryIgnoreCase(country);
  }

  @Override
  public List<Conflict> getConflictsByActor(String actor) {
    return dao.findByActor(actor);
  }

  @Override
  public List<Conflict> getConflictsByActors(String actor1, String actor2) {
    return dao.findByActors(actor1, actor2);
  }

  @Override
  public List<Conflict> getConflictsByFatalities(Integer fatalities) {
    return dao.findByFatalities(fatalities);
  }

  @Override
  public List<Conflict> getConflictsInFatalityRange(Integer lowEnd, Integer highEnd) {
    return dao.findByFatalitiesBetween(lowEnd, highEnd);
  }

  @Override
  public List<Conflict> getConflictsByCriteria(Date startDate, Date endDate, String country,
                                               String actor1, String actor2, Integer lowEnd, Integer highEnd) {
    return dao.findByCriteria(startDate, endDate, country, actor1, actor2, lowEnd, highEnd);
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
  public List<String> getActorsByCountry(String country) {
    return dao.getActorsByCountry(country);
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
