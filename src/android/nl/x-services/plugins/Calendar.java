package com.tenforwardconsulting.phonegap.plugins;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;

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
//				JSONObject arg_object = args.getJSONObject(0);
				callback = callbackContext;
				Intent calIntent = new Intent(Intent.ACTION_EDIT)
					.setType("vnd.android.cursor.item/event")
          .putExtra("title", args.getString(0))
          .putExtra("eventLocation", args.getString(1))
          .putExtra("description", args.getString(2))
					.putExtra("beginTime", args.getLong(3))
					.putExtra("endTime", args.getLong(4))
//					.putExtra("allDay", args.getBoolean(5)); // TODO compute here, don't pass in
          // TODO wtf we need allDay for in Android? native code can compute this as well..
        /* old JS code:
    var allDay = (startDate.getHours() == 0
                  && startDate.getMinutes() == 0
                  && startDate.getSeconds() == 0
                  && endDate.getHours() == 0
                  && endDate.getMinutes() == 0
                  && endDate.getSeconds() == 0);
    */

				this.cordova.startActivityForResult(this, calIntent, RESULT_CODE_CREATE);
				return true;
			} else if (ACTION_DELETE_EVENT.equals(action)) {
        // TODO implement
        return false;
			} else if (ACTION_FIND_EVENT.equals(action)) {
        // TODO implement
        return false;
			} else if (ACTION_MODIFY_EVENT.equals(action)) {
        // TODO implement
        return false;
      }
		} catch(Exception e) {
			System.err.println("Exception: " + e.getMessage());
			return false;
		}     
		return false;
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
}