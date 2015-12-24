package com.jshipper.acled.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.transaction.annotation.Transactional;

import com.jshipper.acled.model.Conflict;
import com.jshipper.acled.service.ConflictService;

/**
 * REST services for ACLED dataset
 * 
 * @author jshipper
 *
 */
@Singleton
@Path(ConflictResource.PATH)
public class ConflictResource {
  public static final String PATH = "conflict";

  private ConflictService conflictService;

  @Inject
  public ConflictResource(ConflictService conflictService) {
    this.conflictService = conflictService;
  }

  @GET
  @Path("/getAll")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getAllConflicts() {
    return Response.ok(conflictService.getAllConflicts()).build();
  }

  @GET
  @Path("/getConflictsByDate/{date}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getConflictsByDate(@PathParam("date") String date) {
    DateFormat dateFormat = new SimpleDateFormat(Conflict.DATE_FORMAT);
    Date d = null;
    try {
      d = dateFormat.parse(date);
    } catch (ParseException e) {
      return Response.status(Status.BAD_REQUEST)
        .entity("Date not in expected format: " + Conflict.DATE_FORMAT).build();
    }
    return Response.ok(conflictService.getConflictsByDate(d)).build();
  }

  @GET
  @Path("/getConflictsInDateRange")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getConflictsInDateRange(
    @QueryParam("startDate") String startDate,
    @QueryParam("endDate") String endDate) {
    DateFormat dateFormat = new SimpleDateFormat(Conflict.DATE_FORMAT);
    Date d1 = null;
    try {
      d1 = dateFormat.parse(startDate);
    } catch (ParseException e) {
      return Response.status(Status.BAD_REQUEST)
        .entity("Start date not in expected format: " + Conflict.DATE_FORMAT)
        .build();
    }
    Date d2 = null;
    try {
      d2 = dateFormat.parse(endDate);
    } catch (ParseException e) {
      return Response.status(Status.BAD_REQUEST)
        .entity("End date not in expected format: " + Conflict.DATE_FORMAT)
        .build();
    }
    return Response.ok(conflictService.getConflictsInDateRange(d1, d2)).build();
  }

  @GET
  @Path("/getConflictsByCountry/{country}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getConflictsByCountry(@PathParam("country") String country) {
    return Response.ok(conflictService.getConflictsByCountry(country)).build();
  }

  @GET
  @Path("/getConflictsByActor/{actor}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getConflictsByActor(@PathParam("actor") String actor) {
    return Response.ok(conflictService.getConflictsByActor(actor)).build();
  }

  @GET
  @Path("/getConflictsByActors")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getConflictsByActors(@QueryParam("actor1") String actor1,
    @QueryParam("actor2") String actor2) {
    return Response.ok(conflictService.getConflictsByActors(actor1, actor2))
      .build();
  }

  @GET
  @Path("/getConflictsByFatalities/{fatalities}")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response
    getConflictsByFatalities(@PathParam("fatalities") Integer fatalities) {
    return Response.ok(conflictService.getConflictsByFatalities(fatalities))
      .build();
  }

  @GET
  @Path("/getConflictsInFatalityRange")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getConflictsInFatalityRange(
    @QueryParam("lowEnd") Integer lowEnd,
    @QueryParam("highEnd") Integer highEnd) {
    return Response
      .ok(conflictService.getConflictsInFatalityRange(lowEnd, highEnd)).build();
  }

  @GET
  @Path("/getAllCountries")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getAllCountries() {
    return Response.ok(conflictService.getAllCountries()).build();
  }

  @GET
  @Path("/getAllActors")
  @Produces(MediaType.APPLICATION_JSON)
  @Transactional
  public Response getAllActors() {
    return Response.ok(conflictService.getAllActors()).build();
  }
}
