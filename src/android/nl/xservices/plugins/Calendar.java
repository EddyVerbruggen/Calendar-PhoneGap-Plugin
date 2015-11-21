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
import java.util.TimeZone;

public class Calendar extends CordovaPlugin {
  public static final String ACTION_OPEN_CALENDAR = "openCalendar";
  public static final String ACTION_CREATE_EVENT_WITH_OPTIONS = "createEventWithOptions";
  public static final String ACTION_CREATE_EVENT_INTERACTIVELY = "createEventInteractively";
  public static final String ACTION_DELETE_EVENT = "deleteEvent";
  public static final String ACTION_FIND_EVENT_WITH_OPTIONS = "findEventWithOptions";
  public static final String ACTION_LIST_EVENTS_IN_RANGE = "listEventsInRange";
  public static final String ACTION_LIST_CALENDARS = "listCalendars";
  public static final String ACTION_CREATE_CALENDAR = "createCalendar";

  public static final Integer RESULT_CODE_CREATE = 0;
  public static final Integer RESULT_CODE_OPENCAL = 1;

  private CallbackContext instanceCallbackContext;

  private static final String LOG_TAG = AbstractCalendarAccessor.LOG_TAG;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	// Keep a copy of the callbackContext for use with non-reentrant functions that use startActivityForResult()
	// which will cause onActivityResult() to be called asynchonously
    instanceCallbackContext = callbackContext;
    
    final boolean hasLimitedSupport = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    if (ACTION_OPEN_CALENDAR.equals(action)) {
      if (hasLimitedSupport) {
        openCalendarLegacy(args);
      } else {
        openCalendar(args);
      }
      return true;
    } else if (ACTION_CREATE_EVENT_WITH_OPTIONS.equals(action)) {
      if (hasLimitedSupport) {
        // TODO investigate this option some day: http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
        createEventInteractively(args);
      } else {
        createEvent(args, callbackContext);
      }
      return true;
    } else if (ACTION_CREATE_EVENT_INTERACTIVELY.equals(action)) {
      createEventInteractively(args);
      return true;
    } else if (ACTION_LIST_EVENTS_IN_RANGE.equals(action)) {
      listEventsInRange(args, callbackContext);
      return true;
    } else if (!hasLimitedSupport && ACTION_FIND_EVENT_WITH_OPTIONS.equals(action)) {
      findEvents(args, callbackContext);
      return true;
    } else if (!hasLimitedSupport && ACTION_DELETE_EVENT.equals(action)) {
      deleteEvent(args, callbackContext);
      return true;
    } else if (ACTION_LIST_CALENDARS.equals(action)) {
      listCalendars(callbackContext);
      return true;
    } else if (!hasLimitedSupport && ACTION_CREATE_CALENDAR.equals(action)) {
      createCalendar(args, callbackContext);
      return true;
    }
    return false;
  }

  private void openCalendarLegacy(JSONArray args) {
    try {
      final Long millis = args.getJSONObject(0).optLong("date");

      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          final Intent calendarIntent = new Intent();
          calendarIntent.putExtra("beginTime", millis);
          calendarIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
          calendarIntent.setClassName("com.android.calendar", "com.android.calendar.AgendaActivity");
          Calendar.this.cordova.startActivityForResult(Calendar.this, calendarIntent, RESULT_CODE_OPENCAL);

          instanceCallbackContext.success();
        }
      });
    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
      instanceCallbackContext.error(e.getMessage());
    }
  }

  @TargetApi(14)
  private void openCalendar(JSONArray args) {
    try {
      final Long millis = args.getJSONObject(0).optLong("date");

      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          final Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon().appendPath("time");
          ContentUris.appendId(builder, millis);

          final Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
          Calendar.this.cordova.startActivityForResult(Calendar.this, intent, RESULT_CODE_OPENCAL);

          instanceCallbackContext.success();
        }
      });
    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
      instanceCallbackContext.error(e.getMessage());
    }
  }

  private void listCalendars(final CallbackContext callbackContext) {
    cordova.getThreadPool().execute(new Runnable() {
      @Override
      public void run() {
        JSONArray jsonObject = new JSONArray();
        try {
          jsonObject = Calendar.this.getCalendarAccessor().getActiveCalendars();
        } catch (JSONException e) {
          System.err.println("Exception: " + e.getMessage());
          callbackContext.error(e.getMessage());
        }
        PluginResult res = new PluginResult(PluginResult.Status.OK, jsonObject);
        callbackContext.sendPluginResult(res);
      }
    });
  }

  // note: not quite ready for primetime yet
  private void createCalendar(JSONArray args, final CallbackContext callbackContext) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
      return;
    }

    try {
      final JSONObject jsonFilter = args.getJSONObject(0);
      final String calendarName = getPossibleNullString("calendarName", jsonFilter);
      if (calendarName == null) {
        callbackContext.error("calendarName is mandatory");
        return;
      }

      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          getCalendarAccessor().createCalendar(calendarName);

          PluginResult res = new PluginResult(PluginResult.Status.OK, "yes");
          callbackContext.sendPluginResult(res);
        }
      });
    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
      callbackContext.error(e.getMessage());
    }
  }

  private void createEventInteractively(JSONArray args) {
    try {
      final JSONObject jsonFilter = args.getJSONObject(0);
      final JSONObject argOptionsObject = jsonFilter.getJSONObject("options");

      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          final Intent calIntent = new Intent(Intent.ACTION_EDIT)
              .setType("vnd.android.cursor.item/event")
              .putExtra("title", getPossibleNullString("title", jsonFilter))
              .putExtra("beginTime", jsonFilter.optLong("startTime"))
              .putExtra("endTime", jsonFilter.optLong("endTime"))
              .putExtra("hasAlarm", 1)
              .putExtra("allDay", AbstractCalendarAccessor.isAllDayEvent(new Date(jsonFilter.optLong("startTime")), new Date(jsonFilter.optLong("endTime"))));
          // TODO can we pass a reminder here?

          // optional fields
          if (!jsonFilter.isNull("location")) {
            calIntent.putExtra("eventLocation", jsonFilter.optString("location"));
          }
          String description = null;
          if (!jsonFilter.isNull("notes")) {
            description = jsonFilter.optString("notes");
          }
          // there's no separate url field, so adding it to the notes
          if (!argOptionsObject.isNull("url")) {
            if (description == null) {
              description = argOptionsObject.optString("url");
            } else {
              description += " " + argOptionsObject.optString("url");
            }
          }
          calIntent.putExtra("description", description);
          calIntent.putExtra("calendar_id", argOptionsObject.optInt("calendarId", 1));

          Calendar.this.cordova.startActivityForResult(Calendar.this, calIntent, RESULT_CODE_CREATE);
        }
      });
    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
      instanceCallbackContext.error(e.getMessage());
    }
  }

  private AbstractCalendarAccessor calendarAccessor;

  private AbstractCalendarAccessor getCalendarAccessor() {
    if (this.calendarAccessor == null) {
      // Note: currently LegacyCalendarAccessor is never used, see the TO-DO at the top of this class
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

  private void deleteEvent(JSONArray args, final CallbackContext callbackContext) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
      return;
    }

    try {
      final JSONObject jsonFilter = args.getJSONObject(0);

      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {

          boolean deleteResult = getCalendarAccessor().deleteEvent(
              null,
              jsonFilter.optLong("startTime"),
              jsonFilter.optLong("endTime"),
              getPossibleNullString("title", jsonFilter),
              getPossibleNullString("location", jsonFilter));
          PluginResult res = new PluginResult(PluginResult.Status.OK, deleteResult);
          callbackContext.sendPluginResult(res);
        }
      });
    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
      callbackContext.error(e.getMessage());
    }
  }

  private void findEvents(JSONArray args, final CallbackContext callbackContext) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
      return;
    }

    try {
      final JSONObject jsonFilter = args.getJSONObject(0);

      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          JSONArray jsonEvents = getCalendarAccessor().findEvents(
              getPossibleNullString("title", jsonFilter),
              getPossibleNullString("location", jsonFilter),
              jsonFilter.optLong("startTime"),
              jsonFilter.optLong("endTime"));

          PluginResult res = new PluginResult(PluginResult.Status.OK, jsonEvents);
          callbackContext.sendPluginResult(res);
        }
      });
    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
      callbackContext.error(e.getMessage());
    }
  }

  private void createEvent(JSONArray args, final CallbackContext callbackContext) {
    try {
      final JSONObject argObject = args.getJSONObject(0);
      final JSONObject argOptionsObject = argObject.getJSONObject("options");

      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          try {
            final String createdEventID = getCalendarAccessor().createEvent(
                null,
                getPossibleNullString("title", argObject),
                argObject.getLong("startTime"),
                argObject.getLong("endTime"),
                getPossibleNullString("notes", argObject),
                getPossibleNullString("location", argObject),
                argOptionsObject.optLong("firstReminderMinutes"),
                argOptionsObject.optLong("secondReminderMinutes"),
                getPossibleNullString("recurrence", argOptionsObject),
                argOptionsObject.optInt("recurrenceInterval"),
                argOptionsObject.optLong("recurrenceEndTime"),
                argOptionsObject.optInt("calendarId", 1),
                getPossibleNullString("url", argOptionsObject));
            callbackContext.success(createdEventID);
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    } catch (Exception e) {
      Log.e(LOG_TAG, "Error creating event. Invoking error callback.", e);
      callbackContext.error(e.getMessage());
    }
  }

  private static String getPossibleNullString(String param, JSONObject from) {
    return from.isNull(param) ? null : from.optString(param);
  }

  private void listEventsInRange(JSONArray args, final CallbackContext callbackContext) {
    try {
      final Uri l_eventUri;
      if (Build.VERSION.SDK_INT >= 8) {
        l_eventUri = Uri.parse("content://com.android.calendar/events");
      } else {
        l_eventUri = Uri.parse("content://calendar/events");
      }

      final JSONObject jsonFilter = args.getJSONObject(0);
      cordova.getThreadPool().execute(new Runnable() {
        @Override
        public void run() {
          ContentResolver contentResolver = Calendar.this.cordova.getActivity().getContentResolver();

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
          Cursor cursor = contentResolver.query(
              l_eventUri,
              l_projection,
              "(deleted = 0 AND" +
                  "   (" +
                  // all day events are stored in UTC, others in the user's timezone
                  "     (eventTimezone  = 'UTC' AND dtstart >=" + (calendar_start.getTimeInMillis() + TimeZone.getDefault().getOffset(calendar_start.getTimeInMillis())) + " AND dtend <=" + (calendar_end.getTimeInMillis() + TimeZone.getDefault().getOffset(calendar_end.getTimeInMillis())) + ")" +
                  "     OR " +
                  "     (eventTimezone <> 'UTC' AND dtstart >=" + calendar_start.getTimeInMillis() + " AND dtend <=" + calendar_end.getTimeInMillis() + ")" +
                  "   )" +
                  ")",
              null,
              "dtstart ASC");

          int i = 0;
          while (cursor.moveToNext()) {
            try {
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
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }

          PluginResult res = new PluginResult(PluginResult.Status.OK, result);
          callbackContext.sendPluginResult(res);
        }
      });
    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
      callbackContext.error(e.getMessage());
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RESULT_CODE_CREATE) {
      if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
        // resultCode may be 0 (RESULT_CANCELED) even when it was created, so passing nothing is the clearest option here
        Log.d(LOG_TAG, "onActivityResult resultcode: " + resultCode);
        instanceCallbackContext.success();
      } else {
        // odd case
        Log.d(LOG_TAG, "onActivityResult weird resultcode: " + resultCode);
        instanceCallbackContext.success();
      }
    } else if (requestCode == RESULT_CODE_OPENCAL) {
      Log.d(LOG_TAG, "onActivityResult requestCode: " + RESULT_CODE_OPENCAL);
      instanceCallbackContext.success();
    } else {
      Log.d(LOG_TAG, "onActivityResult error, resultcode: " + resultCode);
      instanceCallbackContext.error("Unable to add event (" + resultCode + ").");
    }
  }
}
