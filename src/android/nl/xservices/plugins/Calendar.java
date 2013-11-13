package nl.xservices.plugins;

import android.app.Activity;
import android.content.Intent;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
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
			} else  {
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
			if (resultCode == Activity.RESULT_OK) {
				callback.success();
			} else if (resultCode == Activity.RESULT_CANCELED) {
        callback.error("User cancelled");
			} else {
				callback.error("Unable to add event (" + resultCode + ").");
			}
    }
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