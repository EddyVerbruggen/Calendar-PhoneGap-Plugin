package nl.xservices.plugins;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import nl.xservices.plugins.accessor.AbstractCalendarAccessor;
import nl.xservices.plugins.accessor.CalendarProviderAccessor;
import nl.xservices.plugins.accessor.LegacyCalendarAccessor;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

public class Calendar extends CordovaPlugin {
  public static final String ACTION_CREATE_EVENT = "createEvent";
  public static final String ACTION_DELETE_EVENT = "deleteEvent";
  public static final String ACTION_FIND_EVENT   = "findEvent";
  public static final String ACTION_MODIFY_EVENT = "modifyEvent";

  public static final Integer RESULT_CODE_CREATE = 0;
  private CallbackContext callback;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    // TODO part of this plugin may work fine on 3.0 devices, but have not tested it yet, so to be sure:
    final boolean hasLimitedSupport = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    try {
      if (ACTION_CREATE_EVENT.equals(action)) {
        callback = callbackContext;

        final Intent calIntent = new Intent(Intent.ACTION_EDIT)
            .setType("vnd.android.cursor.item/event")
            .putExtra("title", args.getString(0))
            .putExtra("eventLocation", args.getString(1))
            .putExtra("description", args.getString(2))
            .putExtra("beginTime", args.getLong(3))
            .putExtra("endTime", args.getLong(4))
            .putExtra("allDay", isAllDayEvent(new Date(args.getLong(3)), new Date(args.getLong(4))));

        this.cordova.startActivityForResult(this, calIntent, RESULT_CODE_CREATE);
        return true;
      } else if (!hasLimitedSupport && ACTION_DELETE_EVENT.equals(action)) {
        return deleteEvent(args);
      } else {
        callbackContext.error("calendar." + action + " is not (yet) supported on Android.");
        return false;
      }
    } catch(Exception e) {
      System.err.println("Exception: " + e.getMessage());
      return false;
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RESULT_CODE_CREATE) {
      if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
        callback.success();
      } else {
        callback.error("Unable to add event (" + resultCode + ").");
      }
    }
  }

  private boolean deleteEvent(JSONArray args) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
    } else {
      try {
        boolean deleteResult = getCalendarAccessor().deleteEvent(
            null,
            args.getLong(3),
            args.getLong(4),
            args.getString(0),
            args.getString(1));
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

  private AbstractCalendarAccessor calendarAccessor;

  private AbstractCalendarAccessor getCalendarAccessor() {
    if (this.calendarAccessor == null) {
      // Note: currently LegacyCalendarAccessor is never used, see the TODO at the top of this class
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        this.calendarAccessor = new CalendarProviderAccessor(this.cordova);
      } else {
        this.calendarAccessor = new LegacyCalendarAccessor(this.cordova);
      }
    }
    return this.calendarAccessor;
  }

  private boolean isAllDayEvent(final Date startDate, final Date endDate) {
    return startDate.getHours() == 0 &&
        startDate.getMinutes() == 0 &&
        startDate.getSeconds() == 0 &&
        endDate.getHours() == 0 &&
        endDate.getMinutes() == 0 &&
        endDate.getSeconds() == 0;
  }
}