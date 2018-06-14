package nl.xservices.plugins.accessor;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;

import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

import static android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;

public abstract class AbstractCalendarAccessor {

    public static final String LOG_TAG = "Calendar";
    public static final String CONTENT_PROVIDER = "content://com.android.calendar";
    public static final String CONTENT_PROVIDER_PRE_FROYO = "content://calendar";

    public static final String CONTENT_PROVIDER_PATH_CALENDARS = "/calendars";
    public static final String CONTENT_PROVIDER_PATH_EVENTS = "/events";
    public static final String CONTENT_PROVIDER_PATH_REMINDERS = "/reminders";
    public static final String CONTENT_PROVIDER_PATH_INSTANCES_WHEN = "/instances/when";
    public static final String CONTENT_PROVIDER_PATH_ATTENDEES = "/attendees";

    protected static class Event {
        String id;
        String message;
        String location;
        String title;
        String startDate;
        String endDate;
        String recurrenceFreq;
        String recurrenceInterval;
        String recurrenceWeekstart;
        String recurrenceByDay;
        String recurrenceByMonthDay;
        String recurrenceUntil;
        String recurrenceCount;
        //attribute DOMString status;
        // attribute DOMString transparency;
        // attribute CalendarRepeatRule recurrence;
        // attribute DOMString reminder;

        String eventId;
        boolean recurring = false;
        boolean allDay;
        ArrayList<Attendee> attendees;

        public JSONObject toJSONObject() {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", this.eventId);
                obj.putOpt("message", this.message);
                obj.putOpt("location", this.location);
                obj.putOpt("title", this.title);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getDefault());
                if (this.startDate != null) {
                    obj.put("startDate", sdf.format(new Date(Long.parseLong(this.startDate))));
                }
                if (this.endDate != null) {
                    obj.put("endDate", sdf.format(new Date(Long.parseLong(this.endDate))));
                }
                obj.put("allday", this.allDay);
                if (this.attendees != null) {
                    JSONArray arr = new JSONArray();
                    for (Attendee attendee : this.attendees) {
                        arr.put(attendee.toJSONObject());
                    }
                    obj.put("attendees", arr);
                }
                if (this.recurring) {
                    JSONObject objRecurrence = new JSONObject();

                    objRecurrence.putOpt("freq", this.recurrenceFreq);
                    objRecurrence.putOpt("interval", this.recurrenceInterval);
                    objRecurrence.putOpt("wkst", this.recurrenceWeekstart);
                    objRecurrence.putOpt("byday", this.recurrenceByDay);
                    objRecurrence.putOpt("bymonthday", this.recurrenceByMonthDay);
                    objRecurrence.putOpt("until", this.recurrenceUntil);
                    objRecurrence.putOpt("count", this.recurrenceCount);

                    obj.put("recurrence", objRecurrence);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return obj;
        }
    }

    protected static class Attendee {
        String id;
        String name;
        String email;
        String status;

        public JSONObject toJSONObject() {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", this.id);
                obj.putOpt("name", this.name);
                obj.putOpt("email", this.email);
                obj.putOpt("status", this.status);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return obj;
        }
    }

    protected CordovaInterface cordova;

    private EnumMap<KeyIndex, String> calendarKeys;

    public AbstractCalendarAccessor(CordovaInterface cordova) {
        this.cordova = cordova;
        this.calendarKeys = initContentProviderKeys();
    }

    protected enum KeyIndex {
        CALENDARS_ID,
        IS_PRIMARY,
        CALENDARS_NAME,
        CALENDARS_VISIBLE,
        CALENDARS_DISPLAY_NAME,
        EVENTS_ID,
        EVENTS_CALENDAR_ID,
        EVENTS_DESCRIPTION,
        EVENTS_LOCATION,
        EVENTS_SUMMARY,
        EVENTS_START,
        EVENTS_END,
        EVENTS_RRULE,
        EVENTS_ALL_DAY,
        INSTANCES_ID,
        INSTANCES_EVENT_ID,
        INSTANCES_BEGIN,
        INSTANCES_END,
        ATTENDEES_ID,
        ATTENDEES_EVENT_ID,
        ATTENDEES_NAME,
        ATTENDEES_EMAIL,
        ATTENDEES_STATUS
    }

    protected abstract EnumMap<KeyIndex, String> initContentProviderKeys();

    protected String getKey(KeyIndex index) {
        return this.calendarKeys.get(index);
    }

    protected abstract Cursor queryAttendees(String[] projection,
                                             String selection, String[] selectionArgs, String sortOrder);

    protected abstract Cursor queryCalendars(String[] projection,
                                             String selection, String[] selectionArgs, String sortOrder);

    protected abstract Cursor queryEvents(String[] projection,
                                          String selection, String[] selectionArgs, String sortOrder);

    protected abstract Cursor queryEventInstances(long startFrom, long startTo,
                                                  String[] projection, String selection, String[] selectionArgs,
                                                  String sortOrder);

    private Event[] fetchEventInstances(String eventId, String title, String location, String notes, long startFrom, long startTo) {
        String[] projection = {
                this.getKey(KeyIndex.INSTANCES_ID),
                this.getKey(KeyIndex.INSTANCES_EVENT_ID),
                this.getKey(KeyIndex.INSTANCES_BEGIN),
                this.getKey(KeyIndex.INSTANCES_END)
        };

        String sortOrder = this.getKey(KeyIndex.INSTANCES_BEGIN) + " ASC, " + this.getKey(KeyIndex.INSTANCES_END) + " ASC";
        // Fetch events from instances table in ascending order by time.

        // filter
        String selection = "";
        List<String> selectionList = new ArrayList<String>();

        if (eventId != null) {
            selection += CalendarContract.Instances.EVENT_ID + " = ?";
            selectionList.add(eventId);
        } else {
            if (title != null) {
                //selection += Events.TITLE + "=?";
                selection += Events.TITLE + " LIKE ?";
                selectionList.add("%" + title + "%");
            }
            if (location != null && !location.equals("")) {
                if (!"".equals(selection)) {
                    selection += " AND ";
                }
                selection += Events.EVENT_LOCATION + " LIKE ?";
                selectionList.add("%" + location + "%");
            }
            if (notes != null && !notes.equals("")) {
                if (!"".equals(selection)) {
                    selection += " AND ";
                }
                selection += Events.DESCRIPTION + " LIKE ?";
                selectionList.add("%" + notes + "%");
            }
        }

        String[] selectionArgs = new String[selectionList.size()];
        Cursor cursor = queryEventInstances(startFrom, startTo, projection, selection, selectionList.toArray(selectionArgs), sortOrder);
        if (cursor == null) {
            return null;
        }
        Event[] instances = null;
        if (cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndex(this.getKey(KeyIndex.INSTANCES_ID));
            int eventIdCol = cursor.getColumnIndex(this.getKey(KeyIndex.INSTANCES_EVENT_ID));
            int beginCol = cursor.getColumnIndex(this.getKey(KeyIndex.INSTANCES_BEGIN));
            int endCol = cursor.getColumnIndex(this.getKey(KeyIndex.INSTANCES_END));
            int count = cursor.getCount();
            int i = 0;
            instances = new Event[count];
            do {
                // Use the startDate/endDate time from the instances table. For recurring
                // events the events table contain the startDate/endDate time for the
                // origin event (as you would expect).
                instances[i] = new Event();
                instances[i].id = cursor.getString(idCol);
                instances[i].eventId = cursor.getString(eventIdCol);
                instances[i].startDate = cursor.getString(beginCol);
                instances[i].endDate = cursor.getString(endCol);
                i += 1;
            } while (cursor.moveToNext());
        }

        // if we don't find the event by id, try again by title etc - inline with iOS logic
        if ((instances == null || instances.length == 0) && eventId != null) {
            return fetchEventInstances(null, title, location, notes, startFrom, startTo);
        } else {
            return instances;
        }
    }

    private String[] getActiveCalendarIds() {
        Cursor cursor = queryCalendars(new String[]{
                        this.getKey(KeyIndex.CALENDARS_ID)
                },
                this.getKey(KeyIndex.CALENDARS_VISIBLE) + "=1", null, null);
        String[] calendarIds = null;
        if (cursor.moveToFirst()) {
            calendarIds = new String[cursor.getCount()];
            int i = 0;
            do {
                int col = cursor.getColumnIndex(this.getKey(KeyIndex.CALENDARS_ID));
                calendarIds[i] = cursor.getString(col);
                i += 1;
            } while (cursor.moveToNext());
            cursor.close();
        }
        return calendarIds;
    }

    public final JSONArray getActiveCalendars() throws JSONException {
        Cursor cursor = queryCalendars(
                new String[]{
                        this.getKey(KeyIndex.CALENDARS_ID),
                        this.getKey(KeyIndex.CALENDARS_NAME),
                        this.getKey(KeyIndex.CALENDARS_DISPLAY_NAME),
                        this.getKey(KeyIndex.IS_PRIMARY)
                },
                this.getKey(KeyIndex.CALENDARS_VISIBLE) + "=1", null, null
        );
        if (cursor == null) {
            return null;
        }
        JSONArray calendarsWrapper = new JSONArray();
        int primaryColumnIndex;
        if (cursor.moveToFirst()) {
            do {
                JSONObject calendar = new JSONObject();
                calendar.put("id", cursor.getString(cursor.getColumnIndex(this.getKey(KeyIndex.CALENDARS_ID))));
                calendar.put("name", cursor.getString(cursor.getColumnIndex(this.getKey(KeyIndex.CALENDARS_NAME))));
                calendar.put("displayname", cursor.getString(cursor.getColumnIndex(this.getKey(KeyIndex.CALENDARS_DISPLAY_NAME))));
                primaryColumnIndex = cursor.getColumnIndex(this.getKey((KeyIndex.IS_PRIMARY)));
                if (primaryColumnIndex == -1) {
                    primaryColumnIndex = cursor.getColumnIndex("COALESCE(isPrimary, ownerAccount = account_name)");
                }
                calendar.put("isPrimary", "1".equals(cursor.getString(primaryColumnIndex)));
                calendarsWrapper.put(calendar);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return calendarsWrapper;
    }

    private Map<String, Event> fetchEventsAsMap(Event[] instances, String calendarId) {
        // Only selecting from active calendars, no active calendars = no events.
        List<String> activeCalendarIds = Arrays.asList(getActiveCalendarIds());
        if (activeCalendarIds.isEmpty()) {
            return null;
        }

        List<String> calendarsToSearch;

        if(calendarId!=null){
            calendarsToSearch = new ArrayList<String>();
            if(activeCalendarIds.contains(calendarId)){
                calendarsToSearch.add(calendarId);
            }

        }else{
            calendarsToSearch = activeCalendarIds;
        }

        if(calendarsToSearch.isEmpty()){
            return null;
        }


        String[] projection = new String[]{
                this.getKey(KeyIndex.EVENTS_ID),
                this.getKey(KeyIndex.EVENTS_DESCRIPTION),
                this.getKey(KeyIndex.EVENTS_LOCATION),
                this.getKey(KeyIndex.EVENTS_SUMMARY),
                this.getKey(KeyIndex.EVENTS_START),
                this.getKey(KeyIndex.EVENTS_END),
                this.getKey(KeyIndex.EVENTS_RRULE),
                this.getKey(KeyIndex.EVENTS_ALL_DAY)
        };
        // Get all the ids at once from active calendars.
        StringBuffer select = new StringBuffer();
        select.append(this.getKey(KeyIndex.EVENTS_ID) + " IN (");
        select.append(instances[0].eventId);
        for (int i = 1; i < instances.length; i++) {
            select.append(",");
            select.append(instances[i].eventId);
        }
        select.append(") AND " + this.getKey(KeyIndex.EVENTS_CALENDAR_ID) +
                " IN (");

        String prefix ="";
        for (String calendarToFilterId:calendarsToSearch) {
            select.append(prefix);
            prefix = ",";
            select.append(calendarToFilterId);
        }

        select.append(")");
        Cursor cursor = queryEvents(projection, select.toString(), null, null);
        Map<String, Event> eventsMap = new HashMap<String, Event>();
        if (cursor.moveToFirst()) {
            int[] cols = new int[projection.length];
            for (int i = 0; i < cols.length; i++) {
                cols[i] = cursor.getColumnIndex(projection[i]);
            }

            do {
                Event event = new Event();
                event.id = cursor.getString(cols[0]);
                event.message = cursor.getString(cols[1]);
                event.location = cursor.getString(cols[2]);
                event.title = cursor.getString(cols[3]);
                event.startDate = cursor.getString(cols[4]);
                event.endDate = cursor.getString(cols[5]);

                String rrule = cursor.getString(cols[6]);
                if (!TextUtils.isEmpty(rrule)) {
                    event.recurring = true;
                    String[] rrule_rules = cursor.getString(cols[6]).split(";");
                    for (String rule : rrule_rules) {
                        String rule_type = rule.split("=")[0];
                        if (rule_type.equals("FREQ")) {
                            event.recurrenceFreq = rule.split("=")[1];
                        } else if (rule_type.equals("INTERVAL")) {
                            event.recurrenceInterval = rule.split("=")[1];
                        } else if (rule_type.equals("WKST")) {
                            event.recurrenceWeekstart = rule.split("=")[1];
                        } else if (rule_type.equals("BYDAY")) {
                            event.recurrenceByDay = rule.split("=")[1];
                        } else if (rule_type.equals("BYMONTHDAY")) {
                            event.recurrenceByMonthDay = rule.split("=")[1];
                        } else if (rule_type.equals("UNTIL")) {
                            event.recurrenceUntil = rule.split("=")[1];
                        } else if (rule_type.equals("COUNT")) {
                            event.recurrenceCount = rule.split("=")[1];
                        } else {
                            Log.d(LOG_TAG, "Missing handler for " + rule);
                        }
                    }
                } else {
                    event.recurring = false;
                }
                event.allDay = cursor.getInt(cols[7]) != 0;
                eventsMap.put(event.id, event);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return eventsMap;
    }

    private Map<String, ArrayList<Attendee>> fetchAttendeesForEventsAsMap(
            String[] eventIds) {
        // At least one id.
        if (eventIds.length == 0) {
            return null;
        }
        String[] projection = new String[]{
                this.getKey(KeyIndex.ATTENDEES_EVENT_ID),
                this.getKey(KeyIndex.ATTENDEES_ID),
                this.getKey(KeyIndex.ATTENDEES_NAME),
                this.getKey(KeyIndex.ATTENDEES_EMAIL),
                this.getKey(KeyIndex.ATTENDEES_STATUS)
        };
        StringBuffer select = new StringBuffer();
        select.append(this.getKey(KeyIndex.ATTENDEES_EVENT_ID) + " IN (");
        select.append(eventIds[0]);
        for (int i = 1; i < eventIds.length; i++) {
            select.append(",");
            select.append(eventIds[i]);
        }
        select.append(")");
        // Group the events together for easy iteration.
        Cursor cursor = queryAttendees(projection, select.toString(), null,
                this.getKey(KeyIndex.ATTENDEES_EVENT_ID) + " ASC");
        Map<String, ArrayList<Attendee>> attendeeMap =
                new HashMap<String, ArrayList<Attendee>>();
        if (cursor.moveToFirst()) {
            int[] cols = new int[projection.length];
            for (int i = 0; i < cols.length; i++) {
                cols[i] = cursor.getColumnIndex(projection[i]);
            }
            ArrayList<Attendee> array = null;
            String currentEventId = null;
            do {
                String eventId = cursor.getString(cols[0]);
                if (currentEventId == null || !currentEventId.equals(eventId)) {
                    currentEventId = eventId;
                    array = new ArrayList<Attendee>();
                    attendeeMap.put(currentEventId, array);
                }
                Attendee attendee = new Attendee();
                attendee.id = cursor.getString(cols[1]);
                attendee.name = cursor.getString(cols[2]);
                attendee.email = cursor.getString(cols[3]);
                attendee.status = cursor.getString(cols[4]);
                array.add(attendee);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return attendeeMap;
    }

    public JSONArray findEvents(String eventId, String title, String location, String notes, long startFrom, long startTo, String calendarId) {
        JSONArray result = new JSONArray();
        // Fetch events from the instance table.
        Event[] instances = fetchEventInstances(eventId, title, location, notes, startFrom, startTo);
        if (instances == null) {
            return result;
        }
        // Fetch events from the events table for more event info.
        Map<String, Event> eventMap = fetchEventsAsMap(instances, calendarId);
        // Fetch event attendees
        Map<String, ArrayList<Attendee>> attendeeMap =
                fetchAttendeesForEventsAsMap(eventMap.keySet().toArray(new String[0]));
        // Merge the event info with the instances and turn it into a JSONArray.
        /*for (Event event : eventMap.values()) {
            result.put(event.toJSONObject());
        }*/

        for (Event instance : instances) {
            Event event = eventMap.get(instance.eventId);
            if (event != null) {
                instance.message = event.message;
                instance.location = event.location;
                instance.title = event.title;
                if (!event.recurring) {
                    instance.startDate = event.startDate;
                    instance.endDate = event.endDate;
                }

                instance.recurring = event.recurring;
                instance.recurrenceFreq = event.recurrenceFreq;
                instance.recurrenceInterval = event.recurrenceInterval;
                instance.recurrenceWeekstart = event.recurrenceWeekstart;
                instance.recurrenceByDay = event.recurrenceByDay;
                instance.recurrenceByMonthDay = event.recurrenceByMonthDay;
                instance.recurrenceUntil = event.recurrenceUntil;
                instance.recurrenceCount = event.recurrenceCount;

                instance.allDay = event.allDay;
                instance.attendees = attendeeMap.get(instance.eventId);
                result.put(instance.toJSONObject());
            }
        }

        return result;
    }

    public boolean deleteEvent(Uri eventsUri, long startFrom, long startTo, String title, String location, String notes) {
        ContentResolver resolver = this.cordova.getActivity().getApplicationContext().getContentResolver();
        Event[] events = fetchEventInstances(null, title, location, notes, startFrom, startTo);
        int nrDeletedRecords = 0;
        if (events != null) {
            for (Event event : events) {
                Uri eventUri = ContentUris.withAppendedId(eventsUri, Integer.parseInt(event.eventId));
                nrDeletedRecords = resolver.delete(eventUri, null, null);
            }
        }
        return nrDeletedRecords > 0;
    }

    public boolean deleteEventById(Uri eventsUri, long id, long fromTime) {
        if (id == -1)
            throw new IllegalArgumentException("Event id not specified.");

        // Find event
        long evDtStart = -1;
        String evRRule = null;
        {
            Cursor cur = queryEvents(new String[] { Events.DTSTART, Events.RRULE },
                                     Events._ID + " = ?",
                                     new String[] { Long.toString(id) },
                                     Events.DTSTART);
            if (cur.moveToNext()) {
                evDtStart = cur.getLong(0);
                evRRule = cur.getString(1);
            }
            cur.close();
        }
        if (evDtStart == -1)
            throw new RuntimeException("Could not find event.");

        // If targeted, delete initial event
        if (fromTime == -1 || evDtStart >= fromTime) {
            ContentResolver resolver = this.cordova.getActivity().getContentResolver();
            int deleted = this.cordova.getActivity().getContentResolver()
                              .delete(ContentUris.withAppendedId(eventsUri, id), null, null);
            return deleted > 0;
        }

        // Find target instance
        long targDtStart = -1;
        {
            // Scans just over a year.
            // Not using a wider range because it can corrupt the Calendar Storage state! https://issuetracker.google.com/issues/36980229
            Cursor cur = queryEventInstances(fromTime,
                                             fromTime + 1000L * 60L * 60L * 24L * 367L,
                                             new String[] { Instances.DTSTART },
                                             Instances.EVENT_ID + " = ?",
                                             new String[] { Long.toString(id) },
                                             Instances.DTSTART);
            if (cur.moveToNext()) {
                targDtStart = cur.getLong(0);
            }
            cur.close();
        }
        if (targDtStart == -1) {
            // Nothing to delete
            return false;
        }

        // Set UNTIL
        if (evRRule == null)
            evRRule = "";

        // Remove any existing COUNT or UNTIL
        List<String> recurRuleParts = new ArrayList<String>(Arrays.asList(evRRule.split(";")));
        Iterator<String> iter = recurRuleParts.iterator();
        while (iter.hasNext()) {
            String rulePart = iter.next();
            if (rulePart.startsWith("COUNT=") || rulePart.startsWith("UNTIL=")) {
                iter.remove();
            }
        }

        evRRule = TextUtils.join(";", recurRuleParts) + ";UNTIL=" + nl.xservices.plugins.Calendar.formatICalDateTime(new Date(fromTime - 1000));

        // Update event
        ContentValues values = new ContentValues();
        values.put(Events.RRULE, evRRule);
        int updated = this.cordova.getActivity().getContentResolver()
                          .update(ContentUris.withAppendedId(eventsUri, id), values, null, null);

        return updated > 0;
    }

    public String createEvent(Uri eventsUri, String title, long startTime, long endTime, String description,
                              String location, Long firstReminderMinutes, Long secondReminderMinutes,
                              String recurrence, int recurrenceInterval, String recurrenceWeekstart,
                              String recurrenceByDay, String recurrenceByMonthDay, Long recurrenceEndTime, Long recurrenceCount,
                              String allday,
                              Integer calendarId, String url) {
        ContentResolver cr = this.cordova.getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        final boolean allDayEvent = "true".equals(allday) && isAllDayEvent(new Date(startTime), new Date(endTime));
        if (allDayEvent) {
            //all day events must be in UTC time zone per Android specification, getOffset accounts for daylight savings time
            values.put(Events.EVENT_TIMEZONE, "UTC");
            values.put(Events.DTSTART, startTime + TimeZone.getDefault().getOffset(startTime));
            values.put(Events.DTEND, endTime + TimeZone.getDefault().getOffset(endTime));
        } else {
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            values.put(Events.DTSTART, startTime);
            values.put(Events.DTEND, endTime);
        }
        values.put(Events.ALL_DAY, allDayEvent ? 1 : 0);
        values.put(Events.TITLE, title);
        // there's no separate url field, so adding it to the notes
        if (url != null) {
            if (description == null) {
                description = url;
            } else {
                description += " " + url;
            }
        }
        values.put(Events.DESCRIPTION, description);
        values.put(Events.HAS_ALARM, firstReminderMinutes > -1 || secondReminderMinutes > -1 ? 1 : 0);
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.EVENT_LOCATION, location);

        if (recurrence != null) {
            String rrule = "FREQ=" + recurrence.toUpperCase() +
                    ((recurrenceInterval > -1) ? ";INTERVAL=" + recurrenceInterval : "") +
                    ((recurrenceWeekstart != null) ? ";WKST=" + recurrenceWeekstart : "") +
                    ((recurrenceByDay != null) ? ";BYDAY=" + recurrenceByDay : "") +
                    ((recurrenceByMonthDay != null) ? ";BYMONTHDAY=" + recurrenceByMonthDay : "") +
                    ((recurrenceEndTime > -1) ? ";UNTIL=" + nl.xservices.plugins.Calendar.formatICalDateTime(new Date(recurrenceEndTime)) : "") +
                    ((recurrenceCount > -1) ? ";COUNT=" + recurrenceCount : "");
            values.put(Events.RRULE, rrule);
        }

        String createdEventID = null;
        try {
            Uri uri = cr.insert(eventsUri, values);
            createdEventID = uri.getLastPathSegment();
            Log.d(LOG_TAG, "Created event with ID " + createdEventID);

            if (firstReminderMinutes > -1) {
                ContentValues reminderValues = new ContentValues();
                reminderValues.put("event_id", Long.parseLong(uri.getLastPathSegment()));
                reminderValues.put("minutes", firstReminderMinutes);
                reminderValues.put("method", 1);
                cr.insert(Uri.parse(CONTENT_PROVIDER + CONTENT_PROVIDER_PATH_REMINDERS), reminderValues);
            }

            if (secondReminderMinutes > -1) {
                ContentValues reminderValues = new ContentValues();
                reminderValues.put("event_id", Long.parseLong(uri.getLastPathSegment()));
                reminderValues.put("minutes", secondReminderMinutes);
                reminderValues.put("method", 1);
                cr.insert(Uri.parse(CONTENT_PROVIDER + CONTENT_PROVIDER_PATH_REMINDERS), reminderValues);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Creating reminders failed, ignoring since the event was created.", e);
        }
        return createdEventID;
    }

    @SuppressWarnings("MissingPermission") // already requested in calling method
    public String createCalendar(String calendarName, String calendarColor) {
        try {
            // don't create if it already exists
            Uri evuri = CalendarContract.Calendars.CONTENT_URI;
            final ContentResolver contentResolver = cordova.getActivity().getContentResolver();
            Cursor result = contentResolver.query(evuri, new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME}, null, null, null);
            if (result != null) {
                while (result.moveToNext()) {
                    if ((result.getString(1) != null && result.getString(1).equals(calendarName)) || (result.getString(2) != null && result.getString(2).equals(calendarName))) {
                        result.close();
                        return null;
                    }
                }
                result.close();
            }

            // doesn't exist yet, so create
            Uri calUri = CalendarContract.Calendars.CONTENT_URI;
            ContentValues cv = new ContentValues();
            cv.put(CalendarContract.Calendars.ACCOUNT_NAME, "AccountName");
            cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            cv.put(CalendarContract.Calendars.NAME, calendarName);
            cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarName);
            if (calendarColor != null) {
                cv.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.parseColor(calendarColor));
            }
            cv.put(CalendarContract.Calendars.VISIBLE, 1);
			cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
			cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, "AccountName" );
            cv.put(CalendarContract.Calendars.SYNC_EVENTS, 0);

            calUri = calUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "AccountName")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                    .build();

            Uri created = contentResolver.insert(calUri, cv);
            if (created != null) {
                return created.getLastPathSegment();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Creating calendar failed.", e);
        }
        return null;
    }

    ;

    @SuppressWarnings("MissingPermission") // already requested in calling method
    public void deleteCalendar(String calendarName) {
        try {
            Uri evuri = CalendarContract.Calendars.CONTENT_URI;
            final ContentResolver contentResolver = cordova.getActivity().getContentResolver();
            Cursor result = contentResolver.query(evuri, new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME}, null, null, null);
            if (result != null) {
                while (result.moveToNext()) {
                    if (result.getString(1) != null && result.getString(1).equals(calendarName) || result.getString(2) != null && result.getString(2).equals(calendarName)) {
                        long calid = result.getLong(0);
                        Uri deleteUri = ContentUris.withAppendedId(evuri, calid);
                        contentResolver.delete(deleteUri, null, null);
                    }
                }
                result.close();
            }

            // also delete previously crashing calendars, see https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin/issues/241
            deleteCrashingCalendars(contentResolver);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            t.printStackTrace();
        }
    }

    @SuppressWarnings("MissingPermission") // already requested in calling method
    private void deleteCrashingCalendars(ContentResolver contentResolver) {
        // first find any faulty Calendars
        final String fixingAccountName = "FixingAccountName";
        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " IS NULL";
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        uri = uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, fixingAccountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, fixingAccountName);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        int count = contentResolver.update(uri, values, selection, null);

        // now delete any faulty Calendars
        if (count > 0) {
            Uri evuri = CalendarContract.Calendars.CONTENT_URI;
            Cursor result = contentResolver.query(evuri, new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME}, null, null, null);
            if (result != null) {
                while (result.moveToNext()) {
                    if (result.getString(1) != null && result.getString(1).equals(fixingAccountName)) {
                        long calid = result.getLong(0);
                        Uri deleteUri = ContentUris.withAppendedId(evuri, calid);
                        contentResolver.delete(deleteUri, null, null);
                    }
                }
                result.close();
            }
        }
    }

    public static boolean isAllDayEvent(final Date startDate, final Date endDate) {
        return ((endDate.getTime() - startDate.getTime()) % (24 * 60 * 60 * 1000) == 0);
    }
}
