/**
 * Copyright (c) 2012, Twist and Shout, Inc. http://www.twist.com/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


/**
 * @author yvonne@twist.com (Yvonne Yip)
 */

package nl.xservices.plugins.accessor;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import org.apache.cordova.CordovaInterface;

import java.lang.Integer;
import java.util.EnumMap;

public class CalendarProviderAccessor extends AbstractCalendarAccessor {

  public CalendarProviderAccessor(CordovaInterface cordova) {
    super(cordova);
  }

  @Override
  protected EnumMap<KeyIndex, String> initContentProviderKeys() {
    EnumMap<KeyIndex, String> keys = new EnumMap<KeyIndex, String>(
        KeyIndex.class);
    keys.put(KeyIndex.CALENDARS_ID, Calendars._ID);
    keys.put(KeyIndex.CALENDARS_NAME, Calendars.NAME);
    keys.put(KeyIndex.CALENDARS_VISIBLE, Calendars.VISIBLE);
    keys.put(KeyIndex.EVENTS_ID, Events._ID);
    keys.put(KeyIndex.EVENTS_CALENDAR_ID, Events.CALENDAR_ID);
    keys.put(KeyIndex.EVENTS_DESCRIPTION, Events.DESCRIPTION);
    keys.put(KeyIndex.EVENTS_LOCATION, Events.EVENT_LOCATION);
    keys.put(KeyIndex.EVENTS_SUMMARY, Events.TITLE);
    keys.put(KeyIndex.EVENTS_START, Events.DTSTART);
    keys.put(KeyIndex.EVENTS_END, Events.DTEND);
    keys.put(KeyIndex.EVENTS_RRULE, Events.RRULE);
    keys.put(KeyIndex.EVENTS_ALL_DAY, Events.ALL_DAY);
    keys.put(KeyIndex.INSTANCES_ID, Instances._ID);
    keys.put(KeyIndex.INSTANCES_EVENT_ID, Instances.EVENT_ID);
    keys.put(KeyIndex.INSTANCES_BEGIN, Instances.BEGIN);
    keys.put(KeyIndex.INSTANCES_END, Instances.END);
    keys.put(KeyIndex.ATTENDEES_ID, Attendees._ID);
    keys.put(KeyIndex.ATTENDEES_EVENT_ID, Attendees.EVENT_ID);
    keys.put(KeyIndex.ATTENDEES_NAME, Attendees.ATTENDEE_NAME);
    keys.put(KeyIndex.ATTENDEES_EMAIL, Attendees.ATTENDEE_EMAIL);
    keys.put(KeyIndex.ATTENDEES_STATUS, Attendees.ATTENDEE_STATUS);
    return keys;
  }

  @Override
  protected Cursor queryAttendees(String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
    return this.cordova.getActivity().getContentResolver().query(
        Attendees.CONTENT_URI, projection, selection, selectionArgs,
        sortOrder);
  }

  @Override
  protected Cursor queryCalendars(String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
    return this.cordova.getActivity().getContentResolver().query(
        Calendars.CONTENT_URI, projection, selection, selectionArgs,
        sortOrder);
  }

  @Override
  protected Cursor queryEvents(String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {
    return this.cordova.getActivity().getContentResolver().query(
        Events.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
  }

  @Override
  protected Cursor queryEventInstances(long startFrom, long startTo,
                                       String[] projection, String selection, String[] selectionArgs,
                                       String sortOrder) {
    Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
    ContentUris.appendId(builder, startFrom);
    ContentUris.appendId(builder, startTo);
    return this.cordova.getActivity().getContentResolver().query(
        builder.build(), projection, selection, selectionArgs, sortOrder);
  }

  @Override
  public boolean deleteEvent(Uri eventsUri, long startFrom, long startTo, String title, String location) {
    eventsUri = eventsUri == null ? Uri.parse(CONTENT_PROVIDER + CONTENT_PROVIDER_PATH_EVENTS) : eventsUri;
    return super.deleteEvent(eventsUri, startFrom, startTo, title, location);
  }

  @Override
  public String createEvent(Uri eventsUri, String title, long startTime, long endTime,
                          String description, String location, Long firstReminderMinutes, Long secondReminderMinutes,
                          String recurrence, int recurrenceInterval, Long recurrenceEndTime, Integer calendarId,
                          String url) {
    eventsUri = eventsUri == null ? Uri.parse(CONTENT_PROVIDER + CONTENT_PROVIDER_PATH_EVENTS) : eventsUri;
    return super.createEvent(eventsUri, title, startTime, endTime, description, location,
        firstReminderMinutes, secondReminderMinutes, recurrence, recurrenceInterval, recurrenceEndTime, calendarId, url);
  }
}
