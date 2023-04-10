package com.jshipper.acled.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jshipper.acled.model.Conflict;
import com.jshipper.acled.service.ConflictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST services for ACLED dataset
 * 
 * @author jshipper
 *
 */
@RestController
@RequestMapping(ConflictController.PATH)
public class ConflictController {
  public static final String PATH = "/conflict";

  @Autowired
  private ConflictService conflictService;

  @GetMapping("/getAll")
  public ResponseEntity<List<Conflict>> getAllConflicts() {
    return new ResponseEntity<>(conflictService.getAllConflicts(), HttpStatus.OK);
  }

  @GetMapping("/getConflictsByDate/{date}")
  public ResponseEntity<List<Conflict>> getConflictsByDate(@PathVariable("date") String date) {
    DateFormat dateFormat = new SimpleDateFormat(Conflict.DATE_FORMAT);
    Date d = null;
    try {
      d = dateFormat.parse(date);
    } catch (ParseException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      // TODO: Return error "Date not in expected format: " + Conflict.DATE_FORMAT
    }
    return new ResponseEntity<>(conflictService.getConflictsByDate(d), HttpStatus.OK);
  }

  @GetMapping("/getConflictsInDateRange")
  public ResponseEntity<List<Conflict>> getConflictsInDateRange(
    @RequestParam(value = "startDate", required = false) String startDate,
    @RequestParam(value = "endDate", required = false) String endDate) {
    DateFormat dateFormat = new SimpleDateFormat(Conflict.DATE_FORMAT);
    Date d1 = null;
    if (startDate != null) {
      try {
        d1 = dateFormat.parse(startDate);
      } catch (ParseException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // TODO: Return error "Start date not in expected format: " + Conflict.DATE_FORMAT
      }
    }
    Date d2 = null;
    if (endDate != null) {
      try {
        d2 = dateFormat.parse(endDate);
      } catch (ParseException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // TODO: Return error "End date not in expected format: " + Conflict.DATE_FORMAT
      }
      if (d1 != null && d2 != null && d2.before(d1)) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // TODO: Return error "End date should be after start date"
      }
    }
    return new ResponseEntity<>(conflictService.getConflictsInDateRange(d1, d2), HttpStatus.OK);
  }

  @GetMapping("/getConflictsByCountry/{country}")
  public ResponseEntity<List<Conflict>> getConflictsByCountry(@PathVariable("country") String country) {
    return new ResponseEntity<>(conflictService.getConflictsByCountry(country), HttpStatus.OK);
  }

  @GetMapping("/getConflictsByActor/{actor}")
  public ResponseEntity<List<Conflict>> getConflictsByActor(@PathVariable("actor") String actor) {
    return new ResponseEntity<>(conflictService.getConflictsByActor(actor), HttpStatus.OK);
  }

  @GetMapping("/getConflictsByActors")
  public ResponseEntity<List<Conflict>> getConflictsByActors(@RequestParam("actor1") String actor1,
    @RequestParam("actor2") String actor2) {
    return new ResponseEntity<>(conflictService.getConflictsByActors(actor1, actor2), HttpStatus.OK);
  }

  @GetMapping("/getConflictsByFatalities/{fatalities}")
  public ResponseEntity<List<Conflict>> getConflictsByFatalities(@PathVariable("fatalities") Integer fatalities) {
    return new ResponseEntity<>(conflictService.getConflictsByFatalities(fatalities), HttpStatus.OK);
  }

  @GetMapping("/getConflictsInFatalityRange")
  public ResponseEntity<List<Conflict>> getConflictsInFatalityRange(
    @RequestParam(value = "lowEnd", required = false) Integer lowEnd,
    @RequestParam(value = "highEnd", required = false) Integer highEnd) {
    if (lowEnd != null && highEnd != null && lowEnd > highEnd) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      // TODO: Return error "High end should be greater than low end"
    }
    return new ResponseEntity<>(conflictService.getConflictsInFatalityRange(lowEnd, highEnd), HttpStatus.OK);
  }

  @GetMapping("/getConflictsByCriteria")
  public ResponseEntity<List<Conflict>> getConflictsByCriteria(
    @RequestParam(value = "startDate", required = false) String startDate,
    @RequestParam(value = "endDate", required = false) String endDate,
    @RequestParam(value = "country", required = false) String country, @RequestParam(value = "actor1", required = false) String actor1,
    @RequestParam(value = "actor2", required = false) String actor2, @RequestParam(value = "lowEnd", required = false) Integer lowEnd,
    @RequestParam(value = "highEnd", required = false) Integer highEnd) {
    DateFormat dateFormat = new SimpleDateFormat(Conflict.DATE_FORMAT);
    Date d1 = null;
    if (startDate != null) {
      try {
        d1 = dateFormat.parse(startDate);
      } catch (ParseException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // TODO: Return error "Start date not in expected format: " + Conflict.DATE_FORMAT
      }
    }
    Date d2 = null;
    if (endDate != null) {
      try {
        d2 = dateFormat.parse(endDate);
      } catch (ParseException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // TODO: Return error "End date not in expected format: " + Conflict.DATE_FORMAT
      }
      if (d1 != null && d2 != null && d2.before(d1)) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // TODO: Return error "End date should be after start date"
      }
    }
    if (lowEnd != null && highEnd != null && lowEnd > highEnd) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      // TODO: Return error "High end should be greater than low end"
    }
    return new ResponseEntity<>(conflictService.getConflictsByCriteria(d1, d2, country,
      actor1, actor2, lowEnd, highEnd), HttpStatus.OK);
  }

  @GetMapping("/getAllCountries")
  public ResponseEntity<List<String>> getAllCountries() {
    return new ResponseEntity<>(conflictService.getAllCountries(), HttpStatus.OK);
  }

  @GetMapping("/getAllActors")
  public ResponseEntity<List<String>> getAllActors() {
    return new ResponseEntity<>(conflictService.getAllActors(), HttpStatus.OK);
  }

  @GetMapping("/getActorsByCountry/{country}")
  public ResponseEntity<List<String>> getActorsByCountry(@PathVariable("country") String country) {
    return new ResponseEntity<>(conflictService.getActorsByCountry(country), HttpStatus.OK);
  }
}
