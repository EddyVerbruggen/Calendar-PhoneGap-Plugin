package nl.xservices.plugins;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
  public static final String ACTION_CREATE_EVENT = "createEvent";
  public static final String ACTION_CREATE_EVENT_INTERACTIVELY = "createEventInteractively";
  public static final String ACTION_DELETE_EVENT = "deleteEvent";
  public static final String ACTION_FIND_EVENT = "findEvent";

  public static final Integer RESULT_CODE_CREATE = 0;

  private CallbackContext callback;

  private static final String LOG_TAG = AbstractCalendarAccessor.LOG_TAG;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    callback = callbackContext;
    // TODO this plugin may work fine on 3.0 devices, but have not tested it yet, so to be sure:
    final boolean hasLimitedSupport = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    if (ACTION_CREATE_EVENT.equals(action)) {
      if (hasLimitedSupport) {
        // TODO investigate this option some day: http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
        return createEventInteractively(args);
      } else {
        return createEvent(args);
      }
    } else if (ACTION_CREATE_EVENT_INTERACTIVELY.equals(action)) {
      return createEventInteractively(args);
    } else if (!hasLimitedSupport && ACTION_FIND_EVENT.equals(action)) {
      return findEvents(args);
    } else if (!hasLimitedSupport && ACTION_DELETE_EVENT.equals(action)) {
      return deleteEvent(args);
    }
    return false;
  }

  private boolean createEventInteractively(JSONArray args) throws JSONException {
    final JSONObject jsonFilter = args.getJSONObject(0);

    final Intent calIntent = new Intent(Intent.ACTION_EDIT)
        .setType("vnd.android.cursor.item/event")
        .putExtra("title", jsonFilter.optString("title"))
        .putExtra("eventLocation", jsonFilter.optString("location"))
        .putExtra("description", jsonFilter.optString("notes"))
        .putExtra("beginTime", jsonFilter.optLong("startTime"))
        .putExtra("endTime", jsonFilter.optLong("endTime"))
        .putExtra("allDay", AbstractCalendarAccessor.isAllDayEvent(new Date(jsonFilter.optLong("startTime")), new Date(jsonFilter.optLong("endTime"))));

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
          jsonFilter.optString("title"),
          jsonFilter.optString("location"),
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
      JSONObject arg_object = args.getJSONObject(0);
      boolean status = getCalendarAccessor().createEvent(null, arg_object.getString("title"),
          arg_object.getLong("startTime"), arg_object.getLong("endTime"),
          arg_object.getString("notes"), arg_object.getString("location"));

      callback.success("" + status);
      return true;
    } catch (Exception e) {
      System.err.println("Exception: " + e.getMessage());
    }
    return false;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RESULT_CODE_CREATE) {
      if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
        callback.success();
      }
    } else {
      callback.error("Unable to add event (" + resultCode + ").");
    }
  }
}