package com.jshipper.acled.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.jshipper.acled.model.Conflict;

/**
 * Data access object (DAO) interface for the ACLED dataset
 * 
 * @author jshipper
 *
 */
public interface ConflictDao {
  /**
   * Get all conflicts
   * 
   * @return A list of all conflicts
   */
  public List<Conflict> getAllConflicts();

  /**
   * Get all conflicts with the provided date
   * 
   * @param date
   *          Should represent a day
   * @return A list of all conflicts with the provided date, empty list if none
   */
  public List<Conflict> getConflictsByDate(Date date);

  /**
   * Get all conflicts that are within the provided date range (inclusive)
   * 
   * @param startDate
   *          Start of date range
   * @param endDate
   *          End of date range
   * @return A list of all conflicts within the provided date range. Empty list
   *         if none, if both dates are null, or if end date is before start
   *         date.
   */
  public List<Conflict> getConflictsInDateRange(Date startDate, Date endDate);

  /**
   * Get all conflicts with the provided country (case-insensitive)
   * 
   * @param country
   *          Country's name
   * @return A list of all conflicts with the provided country, empty list if
   *         none
   */
  public List<Conflict> getConflictsByCountry(String country);

  /**
   * Get all conflicts with the provided actor (case-insensitive)
   * 
   * @param actor
   *          Actor's name
   * @return A list of all conflicts with the provided actor, empty list if none
   */
  public List<Conflict> getConflictsByActor(String actor);

  /**
   * Get all conflicts that involved both provided actors (case-insensitive)
   * 
   * @param actor1
   *          One actor's name
   * @param actor2
   *          The other actor's name
   * @return A list of all conflicts that involved both provided actors, empty
   *         list if none
   */
  public List<Conflict> getConflictsByActors(String actor1, String actor2);

  /**
   * Get all conflicts with the provided number of fatalities
   * 
   * @param fatalities
   *          Number of fatalities
   * @return A list of all conflicts with the provided number of fatalities,
   *         empty list if none
   */
  public List<Conflict> getConflictsByFatalities(Integer fatalities);

  /**
   * Get all conflicts with a number of fatalities within the provide range
   * (inclusive)
   * 
   * @param lowEnd
   *          Low end number of fatalities
   * @param highEnd
   *          High end number of fatalities
   * @return A list of all conflicts with a number of fatalities within the
   *         provided range. Empty list if none, if both numbers are null, or if
   *         highEnd lower than lowEnd
   */
  public List<Conflict> getConflictsInFatalityRange(Integer lowEnd,
    Integer highEnd);

  /**
   * Get all conflicts that meet the specified criteria. All are optional, but
   * at least one must not be null.
   * 
   * @param startDate
   *          Start of date range
   * @param endDate
   *          End of date range
   * @param country
   *          Country's name
   * @param actor1
   *          One actor's name
   * @param actor2
   *          The other actor's name
   * @param lowEnd
   *          Low end number of fatalities
   * @param highEnd
   *          High end number of fatalities
   * @return A list of all conflicts that meet the specified criteria. Empty
   *         list if none or if any constraints are violated.
   */
  public List<Conflict> getConflictsByCriteria(Date startDate, Date endDate,
    String country, String actor1, String actor2, Integer lowEnd,
    Integer highEnd);

  /**
   * Get all countries associated with conflicts
   * 
   * @return A list of all countries associated with conflicts
   */
  public List<String> getAllCountries();

  /**
   * Get all actors associated with conflicts
   * 
   * @return A list of all actors associated with conflicts
   */
  public List<String> getAllActors();

  /**
   * Get the actors associated with a given country
   * 
   * @param country
   *          Country's name
   * @return A list of all actors associated with the given actor
   */
  public List<String> getActorsByCountry(String country);

  /**
   * Save all conflicts
   * 
   * @param conflicts
   */
  public void saveAll(Collection<Conflict> conflicts);

  /**
   * Delete all conflicts
   * 
   * @param conflicts
   */
  public void deleteAll();
}
