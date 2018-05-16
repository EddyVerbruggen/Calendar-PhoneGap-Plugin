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
    EnumMap<KeyIndex, String> keys = new EnumMap<KeyIndex, String>(KeyIndex.class);
    keys.put(KeyIndex.CALENDARS_ID, Calendars._ID);
    keys.put(KeyIndex.IS_PRIMARY, Calendars.IS_PRIMARY);
    keys.put(KeyIndex.CALENDARS_NAME, Calendars.NAME);
	  keys.put(KeyIndex.CALENDARS_DISPLAY_NAME, Calendars.CALENDAR_DISPLAY_NAME);
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
  public boolean deleteEvent(Uri eventsUri, long startFrom, long startTo, String title, String location, String notes) {
    eventsUri = eventsUri == null ? Uri.parse(CONTENT_PROVIDER + CONTENT_PROVIDER_PATH_EVENTS) : eventsUri;
    return super.deleteEvent(eventsUri, startFrom, startTo, title, location, notes);
  }

  @Override
  public boolean deleteEventById(Uri eventsUri, long id, long fromDate) {
    eventsUri = eventsUri == null ? Uri.parse(CONTENT_PROVIDER + CONTENT_PROVIDER_PATH_EVENTS) : eventsUri;
    return super.deleteEventById(eventsUri, id, fromDate);
  }

  @Override
  public String createEvent(Uri eventsUri, String title, long startTime, long endTime,
                            String description, String location, Long firstReminderMinutes, Long secondReminderMinutes,
                            String recurrence, int recurrenceInterval, String recurrenceWeekstart,
                            String recurrenceByDay, String recurrenceByMonthDay, Long recurrenceEndTime, Long recurrenceCount,
                            String allday, Integer calendarId, String url) {
    eventsUri = eventsUri == null ? Uri.parse(CONTENT_PROVIDER + CONTENT_PROVIDER_PATH_EVENTS) : eventsUri;
    return super.createEvent(eventsUri, title, startTime, endTime, description, location,
            firstReminderMinutes, secondReminderMinutes, recurrence, recurrenceInterval, recurrenceWeekstart,
            recurrenceByDay, recurrenceByMonthDay, recurrenceEndTime, recurrenceCount, allday, calendarId, url);
  }
}
