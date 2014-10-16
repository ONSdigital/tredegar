package com.github.onsdigital.api;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;

import com.github.davidcarboni.restolino.framework.Endpoint;

@Endpoint
public class ReleaseCalendar {
	private final static String CALENDAR_EVENT = "BEGIN:VCALENDAR\n"
			+ "PRODID:-//bobbin v0.1//NONSGML iCal Writer//EN\n"
			+ "METHOD:PUBLISH\n" + "BEGIN:VEVENT\n" + "UID:1@oncs.gov.uk\n"
			+ "DTSTAMP:20141012T170000Z\n"
			+ "CONTACT;CN=ONS:MAILTO:info@ons.gsi.gov.uk\n"
			+ "DTSTART:20141010T083000Z\n" + "DTEND:20141010T083000Z\n"
			+ "SUMMARY:Release Publish\n" + "END:VEVENT\n" + "BEGIN:VEVENT\n"
			+ "UID:2@oncs.gov.uk\n" + "DTSTAMP:20141012T170000Z\n"
			+ "CONTACT;CN=ONS:MAILTO:info@ons.gsi.gov.uk\n"
			+ "DTSTART:20141012T160000Z\n" + "DTEND:20141016T170000Z\n"
			+ "SUMMARY:Second Release\n" + "END:VEVENT\n" + "\n"
			+ "END:VCALENDAR";

	@GET
	public void get(@Context HttpServletRequest httpServletRequest,
			@Context HttpServletResponse httpServletResponse)
			throws IOException {
		httpServletResponse.setContentType("text/calendar");
		httpServletResponse.setCharacterEncoding("UTF8");
		httpServletResponse.setHeader("Content-Disposition",
				"attachment; filename=releases.ics");
		IOUtils.copy(new StringReader(CALENDAR_EVENT),
				httpServletResponse.getOutputStream());

	}
}
