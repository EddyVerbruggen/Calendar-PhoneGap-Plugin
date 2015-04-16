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

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import org.apache.cordova.CordovaInterface;

import java.util.EnumMap;

public class LegacyCalendarAccessor extends AbstractCalendarAccessor {

  public LegacyCalendarAccessor(CordovaInterface cordova) {
    super(cordova);
  }

  @Override
  protected EnumMap<KeyIndex, String> initContentProviderKeys() {
    EnumMap<KeyIndex, String> keys = new EnumMap<KeyIndex, String>(KeyIndex.class);
    keys.put(KeyIndex.CALENDARS_ID, "_id");
    keys.put(KeyIndex.CALENDARS_NAME, "name");
    keys.put(KeyIndex.CALENDARS_VISIBLE, "selected");
    keys.put(KeyIndex.EVENTS_ID, "_id");
    keys.put(KeyIndex.EVENTS_CALENDAR_ID, "calendar_id");
    keys.put(KeyIndex.EVENTS_DESCRIPTION, "message");
    keys.put(KeyIndex.EVENTS_LOCATION, "eventLocation");
    keys.put(KeyIndex.EVENTS_SUMMARY, "title");
    keys.put(KeyIndex.EVENTS_START, "dtstart");
    keys.put(KeyIndex.EVENTS_END, "dtend");
    keys.put(KeyIndex.EVENTS_RRULE, "rrule");
    keys.put(KeyIndex.EVENTS_ALL_DAY, "allDay");
    keys.put(KeyIndex.INSTANCES_ID, "_id");
    keys.put(KeyIndex.INSTANCES_EVENT_ID, "event_id");
    keys.put(KeyIndex.INSTANCES_BEGIN, "begin");
    keys.put(KeyIndex.INSTANCES_END, "endDate");
    keys.put(KeyIndex.ATTENDEES_ID, "_id");
    keys.put(KeyIndex.ATTENDEES_EVENT_ID, "event_id");
    keys.put(KeyIndex.ATTENDEES_NAME, "attendeeName");
    keys.put(KeyIndex.ATTENDEES_EMAIL, "attendeeEmail");
    keys.put(KeyIndex.ATTENDEES_STATUS, "attendeeStatus");
    return keys;
  }

  private String getContentProviderUri(String path) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
      return CONTENT_PROVIDER + path;
    } else {
      return CONTENT_PROVIDER_PRE_FROYO + path;
    }
  }

  @Override
  protected Cursor queryAttendees(String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
    String uri = getContentProviderUri(CONTENT_PROVIDER_PATH_ATTENDEES);
    return this.cordova.getActivity().managedQuery(Uri.parse(uri), projection,
        selection, selectionArgs, sortOrder);
  }

  @Override
  protected Cursor queryCalendars(String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
    String uri = getContentProviderUri(CONTENT_PROVIDER_PATH_CALENDARS);
    return this.cordova.getActivity().managedQuery(Uri.parse(uri), projection,
        selection, selectionArgs, sortOrder);
  }

  @Override
  protected Cursor queryEvents(String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {
    String uri = getContentProviderUri(CONTENT_PROVIDER_PATH_EVENTS);
    return this.cordova.getActivity().managedQuery(Uri.parse(uri), projection,
        selection, selectionArgs, sortOrder);
  }

  @Override
  protected Cursor queryEventInstances(long startFrom, long startTo,
                                       String[] projection, String selection, String[] selectionArgs,
                                       String sortOrder) {
    String uri = getContentProviderUri(CONTENT_PROVIDER_PATH_INSTANCES_WHEN) +
        "/" + Long.toString(startFrom) + "/" + Long.toString(startTo);
    return this.cordova.getActivity().managedQuery(Uri.parse(uri), projection,
        selection, selectionArgs, sortOrder);
  }

  @Override
  public boolean deleteEvent(Uri eventsUri, long startFrom, long startTo, String title, String location) {
    eventsUri = eventsUri == null ? Uri.parse(CONTENT_PROVIDER_PRE_FROYO + CONTENT_PROVIDER_PATH_EVENTS) : eventsUri;
    return super.deleteEvent(eventsUri, startFrom, startTo, title, location);
  }

  @Override
  public void createEvent(Uri eventsUri, String title, long startTime, long endTime,
                          String description, String location, Long firstReminderMinutes, Long secondReminderMinutes,
                          String recurrence, Long recurrenceEndTime, Integer calendarId,
                          String url) {
    eventsUri = eventsUri == null ? Uri.parse(CONTENT_PROVIDER_PRE_FROYO + CONTENT_PROVIDER_PATH_EVENTS) : eventsUri;
    super.createEvent(eventsUri, title, startTime, endTime, description, location,
        firstReminderMinutes, secondReminderMinutes, recurrence, recurrenceEndTime, calendarId, url);
  }

}
