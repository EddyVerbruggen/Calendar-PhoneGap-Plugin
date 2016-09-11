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
  public String createEvent(Uri eventsUri, String title, long startTime, long endTime,
                          String description, String location, Long firstReminderMinutes, Long secondReminderMinutes,
                          String recurrence, int recurrenceInterval, Long recurrenceEndTime, Integer calendarId,
                          String url) {
    eventsUri = eventsUri == null ? Uri.parse(CONTENT_PROVIDER_PRE_FROYO + CONTENT_PROVIDER_PATH_EVENTS) : eventsUri;
    return super.createEvent(eventsUri, title, startTime, endTime, description, location,
        firstReminderMinutes, secondReminderMinutes, recurrence, recurrenceInterval, recurrenceEndTime, calendarId, url);
  }

}
