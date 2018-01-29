package org.apache.cordova.calendartests;

import android.content.ContentResolver;
import android.os.Bundle;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class Utility extends CordovaPlugin {

  private CallbackContext callback;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.callback = callbackContext;

    if ("syncAndroidGoogleCalendar".equals(action)) {
      syncAndroidGoogleCalendar();
      return true;
    }
    return false;
  }

  private void syncAndroidGoogleCalendar() {
    cordova.getThreadPool().execute(new Runnable() { @Override public void run() {
      String authority = "com.android.calendar";

      Bundle settingsBundle = new Bundle();
      settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
      settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
      for (android.accounts.Account acc : android.accounts.AccountManager.get(cordova.getActivity()).getAccountsByType("com.google")) {
        ContentResolver.requestSync(acc, authority, settingsBundle);

        // Wait for completion
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        int ii = 0;
        while (++ii < 30 && ContentResolver.isSyncActive(acc, authority)) {
          try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
      }
      callback.sendPluginResult(new PluginResult(PluginResult.Status.OK));
    }});
  }
}
