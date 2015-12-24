package com.jshipper.acled.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * POJO representing the ACLED dataset, without the first 2 columns (GWNO,
 * EVENT_ID_CNTY)
 * 
 * @author jshipper
 *
 */
@Entity
@NamedQueries({
  @NamedQuery(name = Conflict.ALL_QUERY,
    query = "FROM " + Conflict.TABLE_NAME + " c "),
  @NamedQuery(name = Conflict.BY_DATE_QUERY,
    query = "FROM " + Conflict.TABLE_NAME + " c WHERE c.date = :date"),
  @NamedQuery(name = Conflict.IN_DATE_RANGE_QUERY,
    query = "FROM " + Conflict.TABLE_NAME
      + " c WHERE (:startDate IS NULL OR c.date >= :startDate) "
      + "AND (:endDate IS NULL OR c.date <= :endDate) "),
  @NamedQuery(name = Conflict.BY_COUNTRY_QUERY,
    query = "FROM " + Conflict.TABLE_NAME
      + " c WHERE lower(c.country) = lower(:country)"),
  @NamedQuery(name = Conflict.BY_ACTOR_QUERY,
    query = "FROM " + Conflict.TABLE_NAME
      + " c WHERE lower(c.actor1) = lower(:actor) OR lower(c.actor2) = lower(:actor)"),
  @NamedQuery(name = Conflict.BY_ACTORS_QUERY,
    query = "FROM " + Conflict.TABLE_NAME
      + " c WHERE (lower(c.actor1) = lower(:actor1) OR lower(c.actor1) = lower(:actor2))"
      + " AND (lower(c.actor2) = lower(:actor1) OR lower(c.actor2) = lower(:actor2))"),
  @NamedQuery(name = Conflict.BY_FATALITIES_QUERY,
    query = "FROM " + Conflict.TABLE_NAME
      + " c WHERE c.fatalities = :fatalities"),
  @NamedQuery(name = Conflict.IN_FATALITY_RANGE_QUERY,
    query = "FROM " + Conflict.TABLE_NAME
      + " c WHERE (:lowEnd IS NULL OR c.fatalities >= :lowEnd) "
      + "AND (:highEnd IS NULL OR c.fatalities <= :highEnd)"),
  @NamedQuery(name = Conflict.ALL_COUNTRIES_QUERY,
    query = "SELECT DISTINCT c.country FROM " + Conflict.TABLE_NAME + " c"),
  @NamedQuery(name = Conflict.ALL_ACTOR1S_QUERY,
    query = "SELECT DISTINCT c.actor1 FROM " + Conflict.TABLE_NAME + " c"),
  @NamedQuery(name = Conflict.ALL_ACTOR2S_QUERY,
    query = "SELECT DISTINCT c.actor2 FROM " + Conflict.TABLE_NAME + " c") })
@Table(name = Conflict.TABLE_NAME,
  uniqueConstraints = @UniqueConstraint(columnNames = { "EVENT_ID_NO_CNTY" }) )
public class Conflict implements Serializable {
  public static final String TABLE_NAME = "Conflict";
  public static final String ALL_QUERY = "getAllConflicts";
  public static final String BY_DATE_QUERY = "getConflictsByDate";
  public static final String IN_DATE_RANGE_QUERY = "getConflictsInDateRange";
  public static final String BY_COUNTRY_QUERY = "getConflictsByCountry";
  public static final String BY_ACTOR_QUERY = "getConflictsByActor";
  public static final String BY_ACTORS_QUERY = "getConflictsByActors";
  public static final String BY_FATALITIES_QUERY = "getConflictsByFatalities";
  public static final String IN_FATALITY_RANGE_QUERY =
    "getConflictsInFatalityRange";
  public static final String ALL_COUNTRIES_QUERY = "getAllCountries";
  public static final String ALL_ACTOR1S_QUERY = "getAllActor1s";
  public static final String ALL_ACTOR2S_QUERY = "getAllActor2s";
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final long serialVersionUID = -752272337227549569L;

  @Id
  @Column(name = "EVENT_ID_NO_CNTY")
  private Long id;

  @Column(name = "EVENT_DATE")
  @Temporal(TemporalType.DATE)
  private Date date;

  @Column(name = "YEAR")
  private Integer year;

  @Column(name = "TIME_PRECISION")
  private Integer timePrecision;

  @Column(name = "EVENT_TYPE")
  private String eventType;

  @Column(name = "ACTOR1")
  private String actor1;

  @Column(name = "ALLY_ACTOR_1")
  private String allyActor1;

  @Column(name = "INTER1")
  private Integer inter1;

  @Column(name = "ACTOR2")
  private String actor2;

  @Column(name = "ALLY_ACTOR_2")
  private String allyActor2;

  @Column(name = "INTER2")
  private Integer inter2;

  @Column(name = "INTERACTION")
  private Integer interaction;

  @Column(name = "COUNTRY")
  private String country;

  @Column(name = "ADMIN1")
  private String admin1;

  @Column(name = "ADMIN2")
  private String admin2;

  @Column(name = "ADMIN3")
  private String admin3;

  @Column(name = "LOCATION")
  private String location;

  @Column(name = "LATITUDE")
  private Double latitude;

  @Column(name = "LONGITUDE")
  private Double longitude;

  @Column(name = "GEO_PRECIS")
  private Integer geoPrecis;

  @Column(name = "SOURCE")
  private String source;

  @Column(name = "FATALITIES")
  private Integer fatalities;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public Integer getTimePrecision() {
    return timePrecision;
  }

  public void setTimePrecision(Integer timePrecision) {
    this.timePrecision = timePrecision;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getActor1() {
    return actor1;
  }

  public void setActor1(String actor1) {
    this.actor1 = actor1;
  }

  public String getAllyActor1() {
    return allyActor1;
  }

  public void setAllyActor1(String allyActor1) {
    this.allyActor1 = allyActor1;
  }

  public Integer getInter1() {
    return inter1;
  }

  public void setInter1(Integer inter1) {
    this.inter1 = inter1;
  }

  public String getActor2() {
    return actor2;
  }

  public void setActor2(String actor2) {
    this.actor2 = actor2;
  }

  public String getAllyActor2() {
    return allyActor2;
  }

  public void setAllyActor2(String allyActor2) {
    this.allyActor2 = allyActor2;
  }

  public Integer getInter2() {
    return inter2;
  }

  public void setInter2(Integer inter2) {
    this.inter2 = inter2;
  }

  public Integer getInteraction() {
    return interaction;
  }

  public void setInteraction(Integer interaction) {
    this.interaction = interaction;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getAdmin1() {
    return admin1;
  }

  public void setAdmin1(String admin1) {
    this.admin1 = admin1;
  }

  public String getAdmin2() {
    return admin2;
  }

  public void setAdmin2(String admin2) {
    this.admin2 = admin2;
  }

  public String getAdmin3() {
    return admin3;
  }

  public void setAdmin3(String admin3) {
    this.admin3 = admin3;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Integer getGeoPrecis() {
    return geoPrecis;
  }

  public void setGeoPrecis(Integer geoPrecis) {
    this.geoPrecis = geoPrecis;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Integer getFatalities() {
    return fatalities;
  }

  public void setFatalities(Integer fatalities) {
    this.fatalities = fatalities;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Conflict [id=");
    builder.append(id);
    builder.append(", date=");
    builder.append(date);
    builder.append(", year=");
    builder.append(year);
    builder.append(", timePrecision=");
    builder.append(timePrecision);
    builder.append(", eventType=");
    builder.append(eventType);
    builder.append(", actor1=");
    builder.append(actor1);
    builder.append(", allyActor1=");
    builder.append(allyActor1);
    builder.append(", inter1=");
    builder.append(inter1);
    builder.append(", actor2=");
    builder.append(actor2);
    builder.append(", allyActor2=");
    builder.append(allyActor2);
    builder.append(", inter2=");
    builder.append(inter2);
    builder.append(", interaction=");
    builder.append(interaction);
    builder.append(", country=");
    builder.append(country);
    builder.append(", admin1=");
    builder.append(admin1);
    builder.append(", admin2=");
    builder.append(admin2);
    builder.append(", admin3=");
    builder.append(admin3);
    builder.append(", location=");
    builder.append(location);
    builder.append(", latitude=");
    builder.append(latitude);
    builder.append(", longitude=");
    builder.append(longitude);
    builder.append(", geoPrecis=");
    builder.append(geoPrecis);
    builder.append(", source=");
    builder.append(source);
    builder.append(", fatalities=");
    builder.append(fatalities);
    builder.append("]");
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((actor1 == null) ? 0 : actor1.hashCode());
    result = prime * result + ((actor2 == null) ? 0 : actor2.hashCode());
    result = prime * result + ((admin1 == null) ? 0 : admin1.hashCode());
    result = prime * result + ((admin2 == null) ? 0 : admin2.hashCode());
    result = prime * result + ((admin3 == null) ? 0 : admin3.hashCode());
    result =
      prime * result + ((allyActor1 == null) ? 0 : allyActor1.hashCode());
    result =
      prime * result + ((allyActor2 == null) ? 0 : allyActor2.hashCode());
    result = prime * result + ((country == null) ? 0 : country.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
    result =
      prime * result + ((fatalities == null) ? 0 : fatalities.hashCode());
    result = prime * result + ((geoPrecis == null) ? 0 : geoPrecis.hashCode());
    result = prime * result + ((inter1 == null) ? 0 : inter1.hashCode());
    result = prime * result + ((inter2 == null) ? 0 : inter2.hashCode());
    result =
      prime * result + ((interaction == null) ? 0 : interaction.hashCode());
    result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    result =
      prime * result + ((timePrecision == null) ? 0 : timePrecision.hashCode());
    result = prime * result + ((year == null) ? 0 : year.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Conflict other = (Conflict) obj;
    if (actor1 == null) {
      if (other.actor1 != null)
        return false;
    } else if (!actor1.equals(other.actor1))
      return false;
    if (actor2 == null) {
      if (other.actor2 != null)
        return false;
    } else if (!actor2.equals(other.actor2))
      return false;
    if (admin1 == null) {
      if (other.admin1 != null)
        return false;
    } else if (!admin1.equals(other.admin1))
      return false;
    if (admin2 == null) {
      if (other.admin2 != null)
        return false;
    } else if (!admin2.equals(other.admin2))
      return false;
    if (admin3 == null) {
      if (other.admin3 != null)
        return false;
    } else if (!admin3.equals(other.admin3))
      return false;
    if (allyActor1 == null) {
      if (other.allyActor1 != null)
        return false;
    } else if (!allyActor1.equals(other.allyActor1))
      return false;
    if (allyActor2 == null) {
      if (other.allyActor2 != null)
        return false;
    } else if (!allyActor2.equals(other.allyActor2))
      return false;
    if (country == null) {
      if (other.country != null)
        return false;
    } else if (!country.equals(other.country))
      return false;
    if (date == null) {
      if (other.date != null)
        return false;
    } else {
      // Compare dates, equal if same day of year and same year
      Calendar c = new GregorianCalendar();
      c.setTime(date);
      int dateDay = c.get(Calendar.DAY_OF_YEAR);
      int dateYear = c.get(Calendar.YEAR);
      c.setTime(other.date);
      int otherDateDay = c.get(Calendar.DAY_OF_YEAR);
      int otherDateYear = c.get(Calendar.YEAR);
      if (dateDay != otherDateDay || dateYear != otherDateYear)
        return false;
    }
    if (eventType == null) {
      if (other.eventType != null)
        return false;
    } else if (!eventType.equals(other.eventType))
      return false;
    if (fatalities == null) {
      if (other.fatalities != null)
        return false;
    } else if (!fatalities.equals(other.fatalities))
      return false;
    if (geoPrecis == null) {
      if (other.geoPrecis != null)
        return false;
    } else if (!geoPrecis.equals(other.geoPrecis))
      return false;
    if (inter1 == null) {
      if (other.inter1 != null)
        return false;
    } else if (!inter1.equals(other.inter1))
      return false;
    if (inter2 == null) {
      if (other.inter2 != null)
        return false;
    } else if (!inter2.equals(other.inter2))
      return false;
    if (interaction == null) {
      if (other.interaction != null)
        return false;
    } else if (!interaction.equals(other.interaction))
      return false;
    if (latitude == null) {
      if (other.latitude != null)
        return false;
    } else if (!latitude.equals(other.latitude))
      return false;
    if (location == null) {
      if (other.location != null)
        return false;
    } else if (!location.equals(other.location))
      return false;
    if (longitude == null) {
      if (other.longitude != null)
        return false;
    } else if (!longitude.equals(other.longitude))
      return false;
    if (source == null) {
      if (other.source != null)
        return false;
    } else if (!source.equals(other.source))
      return false;
    if (timePrecision == null) {
      if (other.timePrecision != null)
        return false;
    } else if (!timePrecision.equals(other.timePrecision))
      return false;
    if (year == null) {
      if (other.year != null)
        return false;
    } else if (!year.equals(other.year))
      return false;
    return true;
  }
}
