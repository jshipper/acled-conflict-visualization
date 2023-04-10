package com.jshipper.acled.dao;

import com.jshipper.acled.model.Conflict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ConflictRepository extends JpaRepository<Conflict, Long> {
    List<Conflict> findByDate(Date date);
    List<Conflict> findByDateBetween(Date startDate, Date endDate);
    List<Conflict> findByCountryIgnoreCase(String country);
    @Query("SELECT c FROM Conflict c WHERE LOWER(c.actor1) = LOWER(:actor) OR LOWER(c.actor2) = LOWER(:actor)")
    List<Conflict> findByActor(@Param("actor") String actor);
    @Query("SELECT c FROM Conflict c WHERE (LOWER(c.actor1) = LOWER(:actor1) OR LOWER(c.actor1) = LOWER(:actor2)) AND (LOWER(c.actor2) = LOWER(:actor1) OR LOWER(c.actor2) = LOWER(:actor2))")
    List<Conflict> findByActors(@Param("actor1") String actor1, @Param("actor2") String actor2);
    List<Conflict> findByFatalities(Integer fatalities);
    List<Conflict> findByFatalitiesBetween(Integer lowEnd, Integer highEnd);
    @Query("SELECT c FROM Conflict c WHERE (:startDate IS NULL OR c.date >= :startDate) " +
            "AND (:endDate IS NULL OR c.date <= :endDate) " +
            "AND (:country IS NULL OR LOWER(c.country) = LOWER(:country)) " +
            "AND (:actor1 IS NULL OR LOWER(c.actor1) = LOWER(:actor1) OR LOWER(c.actor2) = LOWER(:actor1)) " +
            "AND (:actor2 IS NULL OR LOWER(c.actor1) = LOWER(:actor2) OR LOWER(c.actor2) = LOWER(:actor2)) " +
            "AND (:lowEnd IS NULL OR c.fatalities >= :lowEnd) " +
            "AND (:highEnd IS NULL OR c.fatalities <= :highEnd) ")
    List<Conflict> findByCriteria(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("country") String country,
                                  @Param("actor1") String actor1, @Param("actor2") String actor2, @Param("lowEnd") Integer lowEnd, @Param("highEnd") Integer highEnd);
    @Query("SELECT DISTINCT c.country FROM Conflict c")
    List<String> getAllCountries();
    // TODO: Union with actor2
    @Query("SELECT DISTINCT c.actor1 FROM Conflict c")
    List<String> getAllActors();
    // TODO: Union with actor2
    @Query("SELECT DISTINCT c.actor1 FROM Conflict c WHERE LOWER(c.country) = LOWER(:country)")
    List<String> getActorsByCountry(@Param("country") String country);
}
