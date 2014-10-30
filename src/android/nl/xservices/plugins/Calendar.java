package nl.xservices.plugins;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import nl.xservices.plugins.accessor.AbstractCalendarAccessor;
import nl.xservices.plugins.accessor.CalendarProviderAccessor;
import nl.xservices.plugins.accessor.LegacyCalendarAccessor;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Calendar extends CordovaPlugin {
  public static final String ACTION_OPEN_CALENDAR = "openCalendar";
  public static final String ACTION_CREATE_EVENT_WITH_OPTIONS = "createEventWithOptions";
  public static final String ACTION_CREATE_EVENT_INTERACTIVELY = "createEventInteractively";
  public static final String ACTION_DELETE_EVENT = "deleteEvent";
  public static final String ACTION_FIND_EVENT = "findEvent";
  public static final String ACTION_LIST_EVENTS_IN_RANGE = "listEventsInRange";
  public static final String ACTION_LIST_CALENDARS = "listCalendars";
  public static final String ACTION_CREATE_CALENDAR = "createCalendar";

  public static final Integer RESULT_CODE_CREATE = 0;
  public static final Integer RESULT_CODE_OPENCAL = 1;

  private CallbackContext callback;

  private static final String LOG_TAG = AbstractCalendarAccessor.LOG_TAG;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    callback = callbackContext;
    final boolean hasLimitedSupport = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    if (ACTION_OPEN_CALENDAR.equals(action)) {
      if (hasLimitedSupport) {
        return openCalendarLegacy(args);
      } else {
        return openCalendar(args);
      }
    } else if (ACTION_CREATE_EVENT_WITH_OPTIONS.equals(action)) {
      if (hasLimitedSupport) {
        // TODO investigate this option some day: http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
        return createEventInteractively(args);
      } else {
        return createEvent(args);
      }
    } else if (ACTION_CREATE_EVENT_INTERACTIVELY.equals(action)) {
      return createEventInteractively(args);
    } else if (ACTION_LIST_EVENTS_IN_RANGE.equals(action)) {
      return listEventsInRange(args);
    } else if (!hasLimitedSupport && ACTION_FIND_EVENT.equals(action)) {
      return findEvents(args);
    } else if (!hasLimitedSupport && ACTION_DELETE_EVENT.equals(action)) {
      return deleteEvent(args);
    } else if (ACTION_LIST_CALENDARS.equals(action)) {
      return listCalendars();
//    } else if (!hasLimitedSupport && ACTION_CREATE_CALENDAR.equals(action)) {
//      return createCalendar(args);
    }
    return false;
  }

  private boolean openCalendarLegacy(JSONArray args) throws JSONException {
    final Long millis = args.getJSONObject(0).optLong("date");

    final Intent calendarIntent = new Intent();
    calendarIntent.putExtra("beginTime", millis);
    calendarIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    calendarIntent.setClassName("com.android.calendar","com.android.calendar.AgendaActivity");
    this.cordova.startActivityForResult(this, calendarIntent, RESULT_CODE_OPENCAL);

    return true;
  }

  @TargetApi(14)
  private boolean openCalendar(JSONArray args) throws JSONException {
    final Long millis = args.getJSONObject(0).optLong("date");
    final Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time");
    ContentUris.appendId(builder, millis);

    final Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
    this.cordova.startActivityForResult(this, intent, RESULT_CODE_OPENCAL);

    return true;
  }

  private boolean listCalendars() throws JSONException {
    final JSONArray jsonObject = getCalendarAccessor().getActiveCalendars();
    PluginResult res = new PluginResult(PluginResult.Status.OK, jsonObject);
    callback.sendPluginResult(res);
    return true;
  }

  // note: not quite ready for primetime yet
  private boolean createCalendar(JSONArray args) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
    } else {
      try {
        JSONObject jsonFilter = args.getJSONObject(0);
        final String calendarName = jsonFilter.getString("calendarName");

        getCalendarAccessor().createCalendar(calendarName);

        PluginResult res = new PluginResult(PluginResult.Status.OK, "yes");
        res.setKeepCallback(true);
        callback.sendPluginResult(res);
        return true;
      } catch (JSONException e) {
        System.err.println("Exception: " + e.getMessage());
      }
    }
    return false;
  }


  private boolean createEventInteractively(JSONArray args) throws JSONException {
    final JSONObject jsonFilter = args.getJSONObject(0);

    final Intent calIntent = new Intent(Intent.ACTION_EDIT)
        .setType("vnd.android.cursor.item/event")
        .putExtra("title", jsonFilter.optString("title"))
        .putExtra("beginTime", jsonFilter.optLong("startTime"))
        .putExtra("endTime", jsonFilter.optLong("endTime"))
        .putExtra("hasAlarm", 1)
        .putExtra("allDay", AbstractCalendarAccessor.isAllDayEvent(new Date(jsonFilter.optLong("startTime")), new Date(jsonFilter.optLong("endTime"))));
    // TODO can we pass a reminder here?

    // optional fields
    if (!jsonFilter.isNull("location")) {
      calIntent.putExtra("eventLocation", jsonFilter.optString("location"));
    }
    if (!jsonFilter.isNull("notes")) {
      calIntent.putExtra("description", jsonFilter.optString("notes"));
    }

    this.cordova.startActivityForResult(this, calIntent, RESULT_CODE_CREATE);
    return true;
  }

  private AbstractCalendarAccessor calendarAccessor;

  private AbstractCalendarAccessor getCalendarAccessor() {
    if (this.calendarAccessor == null) {
      // Note: currently LegacyCalendarAccessor is never used, see the TODO at the top of this class
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        Log.d(LOG_TAG, "Initializing calendar plugin");
        this.calendarAccessor = new CalendarProviderAccessor(this.cordova);
      } else {
        Log.d(LOG_TAG, "Initializing legacy calendar plugin");
        this.calendarAccessor = new LegacyCalendarAccessor(this.cordova);
      }
    }
    return this.calendarAccessor;
  }

  private boolean deleteEvent(JSONArray args) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
    } else {
      try {
        JSONObject jsonFilter = args.getJSONObject(0);
        boolean deleteResult = getCalendarAccessor().deleteEvent(
            null,
            jsonFilter.optLong("startTime"),
            jsonFilter.optLong("endTime"),
            jsonFilter.optString("title"),
            jsonFilter.optString("location"));
        PluginResult res = new PluginResult(PluginResult.Status.OK, deleteResult);
        res.setKeepCallback(true);
        callback.sendPluginResult(res);
        return true;
      } catch (JSONException e) {
        System.err.println("Exception: " + e.getMessage());
      }
    }
    return false;
  }

  private boolean findEvents(JSONArray args) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
    }
    try {
      JSONObject jsonFilter = args.getJSONObject(0);
      JSONArray jsonEvents = getCalendarAccessor().findEvents(
          jsonFilter.isNull("title") ? null : jsonFilter.optString("title"),
          jsonFilter.isNull("location") ? null : jsonFilter.optString("location"),
          jsonFilter.optLong("startTime"),
          jsonFilter.optLong("endTime"));

      PluginResult res = new PluginResult(PluginResult.Status.OK, jsonEvents);
      res.setKeepCallback(true);
      callback.sendPluginResult(res);
      return true;

    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
    }
    return false;
  }

  private boolean createEvent(JSONArray args) {
    try {
      final JSONObject argObject = args.getJSONObject(0);
      final JSONObject argOptionsObject = argObject.getJSONObject("options");

      boolean status = getCalendarAccessor().createEvent(
          null,
          argObject.getString("title"),
          argObject.getLong("startTime"),
          argObject.getLong("endTime"),
          argObject.isNull("notes") ? null : argObject.getString("notes"),
          argObject.isNull("location") ? null : argObject.getString("location"),
          argOptionsObject.isNull("firstReminderMinutes") ? null : argOptionsObject.getLong("firstReminderMinutes"),
          argOptionsObject.isNull("secondReminderMinutes") ? null : argOptionsObject.getLong("secondReminderMinutes"),
          argOptionsObject.isNull("recurrence") ? null : argOptionsObject.getString("recurrence"),
          argOptionsObject.isNull("recurrenceEndTime") ? null : argOptionsObject.getLong("recurrenceEndTime")
      );

      callback.success("" + status);
      return true;
    } catch (Exception e) {
      System.err.println("Exception: " + e.getMessage());
    }
    return false;
  }

  private boolean listEventsInRange(JSONArray args) {
    try {
      Uri l_eventUri;
      if (Build.VERSION.SDK_INT >= 8) {
        l_eventUri = Uri.parse("content://com.android.calendar/events");
      } else {
        l_eventUri = Uri.parse("content://calendar/events");
      }
      ContentResolver contentResolver = this.cordova.getActivity().getContentResolver();
      JSONObject jsonFilter = args.getJSONObject(0);
      JSONArray result = new JSONArray();
      long input_start_date = jsonFilter.optLong("startTime");
      long input_end_date = jsonFilter.optLong("endTime");

      //prepare start date
      java.util.Calendar calendar_start = java.util.Calendar.getInstance();
      Date date_start = new Date(input_start_date);
      calendar_start.setTime(date_start);

      //prepare end date
      java.util.Calendar calendar_end = java.util.Calendar.getInstance();
      Date date_end = new Date(input_end_date);
      calendar_end.setTime(date_end);

      //projection of DB columns
      String[] l_projection = new String[]{"calendar_id", "title", "dtstart", "dtend", "eventLocation", "allDay"};

      //actual query
      Cursor cursor = contentResolver.query(l_eventUri, l_projection, "( dtstart >" + calendar_start.getTimeInMillis() + " AND dtend <" + calendar_end.getTimeInMillis() + " AND deleted = 0)", null, "dtstart ASC");

      int i = 0;
      while (cursor.moveToNext()) {
        result.put(
            i++,
            new JSONObject()
                .put("calendar_id", cursor.getString(cursor.getColumnIndex("calendar_id")))
                .put("title", cursor.getString(cursor.getColumnIndex("title")))
                .put("dtstart", cursor.getLong(cursor.getColumnIndex("dtstart")))
                .put("dtend", cursor.getLong(cursor.getColumnIndex("dtend")))
                .put("eventLocation", cursor.getString(cursor.getColumnIndex("eventLocation")) != null ? cursor.getString(cursor.getColumnIndex("eventLocation")) : "")
                .put("allDay", cursor.getInt(cursor.getColumnIndex("allDay")))
        );
      }

      PluginResult res = new PluginResult(PluginResult.Status.OK, result);
      callback.sendPluginResult(res);
      return true;
    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
    }
    return false;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RESULT_CODE_CREATE) {
      if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
        // resultCode may be 0 (RESULT_CANCELED) even when it was created, so passing nothing is the clearest option here
        callback.success();
      }
    } else if (requestCode == RESULT_CODE_OPENCAL) {
      callback.success();
    } else {
      callback.error("Unable to add event (" + resultCode + ").");
    }
  }
}
