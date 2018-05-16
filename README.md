# PhoneGap Calendar plugin

for iOS and Android, by [Eddy Verbruggen](http://www.x-services.nl)


[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=eddyverbruggen%40gmail%2ecom&lc=US&item_name=cordova%2dplugin%2dcalendar&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted)
Every now and then kind folks ask me how they can give me all their money.
Of course I'm happy to receive any amount but I'm just as happy if you simply 'star' this project.

<table width="100%">
  <tr>
    <td width="100"><a href="http://plugins.telerik.com/plugin/calendar"><img src="http://www.x-services.nl/github-images/telerik-verified-plugins-marketplace.png" width="97px" height="71px" alt="Marketplace logo"/></a></td>
    <td>For a quick demo app and easy code samples, check out the plugin page at the Verified Plugins Marketplace: http://plugins.telerik.com/plugin/calendar</td>
  </tr>
</table>

1. [Description](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#1-description)
2. [Installation](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#2-installation)
	2. [Automatically](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#automatically)
	2. [Manually](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#manually)
	2. [PhoneGap Build](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#phonegap-build)
3. [Usage](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#3-usage)
4. [Promises](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#4-promises)
5. [Credits](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#5-credits)
6. [License](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#6-license)

## 1. Description

This plugin allows you to add events to the Calendar of the mobile device.

* Works with PhoneGap >= 3.0.
* For PhoneGap 2.x, see [the pre-3.0 branch](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin/tree/pre-3.0).
* Compatible with [Cordova Plugman](https://github.com/apache/cordova-plugman).
* [Officially supported by PhoneGap Build](https://build.phonegap.com/plugins).

### iOS specifics
* Supported methods: `find`, `create`, `modify`, `delete`, ..
* All methods work without showing the native calendar. Your app never loses control.
* Tested on iOS 6+.
* On iOS 10+ you need to provide a reason to the user for Calendar access. This plugin adds an empty `NSCalendarsUsageDescription` key to the /platforms/ios/*-Info.plist file which you can override with your custom string. To do so, pass the following variable when installing the plugin:

```
cordova plugin add cordova-plugin-calendar --variable CALENDAR_USAGE_DESCRIPTION="This app uses your calendar"
```

### Android specifics
* Supported methods on Android 4: `find`, `create` (silent and interactive), `delete`, ..
* Supported methods on Android 2 and 3: `create` interactive only: the user is presented a prefilled Calendar event. Pressing the hardware back button will give control back to your app.

### Windows 10 Mobile
* Supported methods: `createEvent`, `createEventWithOptions`, `createEventInteractively`, `createEventInteractivelyWithOptions` only interactively

## 2. Installation

### Automatically
Latest release on npm:
```
$ cordova plugin add cordova-plugin-calendar
```

Bleeding edge, from github:
```
$ cordova plugin add https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin.git
```

### Manually

#### iOS

1\. Add the following xml to your `config.xml`:
```xml
<!-- for iOS -->
<feature name="Calendar">
	<param name="ios-package" value="Calendar" />
</feature>
```

2\. Grab a copy of Calendar.js, add it to your project and reference it in `index.html`:
```html
<script type="text/javascript" src="js/Calendar.js"></script>
```

3\. Download the source files for iOS and copy them to your project.

Copy `Calendar.h` and `Calendar.m` to `platforms/ios/<ProjectName>/Plugins`

4\. Click your project in XCode, Build Phases, Link Binary With Libraries, search for and add `EventKit.framework` and `EventKitUI.framework`.

#### Android

1\. Add the following xml to your `config.xml`:
```xml
<!-- for Android -->
<feature name="Calendar">
  <param name="android-package" value="nl.xservices.plugins.Calendar" />
</feature>
```

2\. Grab a copy of Calendar.js, add it to your project and reference it in `index.html`:
```html
<script type="text/javascript" src="js/Calendar.js"></script>
```

3\. Download the source files for Android and copy them to your project.

Android: Copy `Calendar.java` to `platforms/android/src/nl/xservices/plugins` (create the folders/packages).
Then create a package called `accessor` and copy other 3 java Classes into it.

4\. Add these permissions to your AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.READ_CALENDAR"/>
<uses-permission android:name="android.permission.WRITE_CALENDAR"/>
```

Note that if you don't want your app to ask for these permissions, you can leave them out, but you'll only be able to
use one function of this plugin: `createEventInteractively`.


### PhoneGap Build

Add the following xml to your `config.xml` to always use the latest npm version of this plugin:
```xml
<plugin name="cordova-plugin-calendar" />
```

Also, make sure you're building with Gradle by adding this to your `config.xml` file:
```xml
<preference name="android-build-tool" value="gradle" />
```

## 3. Usage

The table gives an overview of basic operation compatibility:

Operation                           | Comment     | iOS | Android | Windows |
----------------------------------- | ----------- | --- | ------- | ------- |
createCalendar                      |             | yes | yes     |         |
deleteCalendar                      |             | yes | yes     |         |
createEvent                         | silent      | yes | yes *   | yes **  |
createEventWithOptions              | silent      | yes | yes *   | yes **  |
createEventInteractively            | interactive | yes | yes     | yes **  |
createEventInteractivelyWithOptions | interactive | yes | yes     | yes **  |
findEvent                           |             | yes | yes     |         |
findEventWithOptions                |             | yes | yes     |         |
listEventsInRange                   |             |     | yes     |         |
listCalendars                       |             | yes | yes     |         |
findAllEventsInNamedCalendars       |             | yes |         |         |
modifyEvent                         |             | yes |         |         |
modifyEventWithOptions              |             | yes |         |         |
deleteEvent                         |             | yes | yes     |         |
deleteEventFromNamedCalendar        |             | yes |         |         |
deleteEventById                     |             | yes | yes     |         |
openCalendar                        |             | yes | yes     |         |

* \* on Android < 4 dialog is shown
* \** only interactively on windows mobile

Basic operations, you'll want to copy-paste this for testing purposes:
```js
  // prep some variables
  var startDate = new Date(2015,2,15,18,30,0,0,0); // beware: month 0 = january, 11 = december
  var endDate = new Date(2015,2,15,19,30,0,0,0);
  var title = "My nice event";
  var eventLocation = "Home";
  var notes = "Some notes about this event.";
  var success = function(message) { alert("Success: " + JSON.stringify(message)); };
  var error = function(message) { alert("Error: " + message); };

  // create a calendar (iOS only for now)
  window.plugins.calendar.createCalendar(calendarName,success,error);
  // if you want to create a calendar with a specific color, pass in a JS object like this:
  var createCalOptions = window.plugins.calendar.getCreateCalendarOptions();
  createCalOptions.calendarName = "My Cal Name";
  createCalOptions.calendarColor = "#FF0000"; // an optional hex color (with the # char), default is null, so the OS picks a color
  window.plugins.calendar.createCalendar(createCalOptions,success,error);

  // delete a calendar
  window.plugins.calendar.deleteCalendar(calendarName,success,error);

  // create an event silently (on Android < 4 an interactive dialog is shown)
  window.plugins.calendar.createEvent(title,eventLocation,notes,startDate,endDate,success,error);

  // create an event silently (on Android < 4 an interactive dialog is shown which doesn't use this options) with options:
  var calOptions = window.plugins.calendar.getCalendarOptions(); // grab the defaults
  calOptions.firstReminderMinutes = 120; // default is 60, pass in null for no reminder (alarm)
  calOptions.secondReminderMinutes = 5;

  // Added these options in version 4.2.4:
  calOptions.recurrence = "monthly"; // supported are: daily, weekly, monthly, yearly
  calOptions.recurrenceEndDate = new Date(2016,10,1,0,0,0,0,0); // leave null to add events into infinity and beyond
  calOptions.calendarName = "MyCreatedCalendar"; // iOS only
  calOptions.calendarId = 1; // Android only, use id obtained from listCalendars() call which is described below. This will be ignored on iOS in favor of calendarName and vice versa. Default: 1.

  // This is new since 4.2.7:
  calOptions.recurrenceInterval = 2; // once every 2 months in this case, default: 1

  // And the URL can be passed since 4.3.2 (will be appended to the notes on Android as there doesn't seem to be a sep field)
  calOptions.url = "https://www.google.com";

  // on iOS the success handler receives the event ID (since 4.3.6)
  window.plugins.calendar.createEventWithOptions(title,eventLocation,notes,startDate,endDate,calOptions,success,error);

  // create an event interactively
  window.plugins.calendar.createEventInteractively(title,eventLocation,notes,startDate,endDate,success,error);

  // create an event interactively with the calOptions object as shown above
  window.plugins.calendar.createEventInteractivelyWithOptions(title,eventLocation,notes,startDate,endDate,calOptions,success,error);

  // create an event in a named calendar (iOS only, deprecated, use createEventWithOptions instead)
  window.plugins.calendar.createEventInNamedCalendar(title,eventLocation,notes,startDate,endDate,calendarName,success,error);

  // find events (on iOS this includes a list of attendees (if any))
  window.plugins.calendar.findEvent(title,eventLocation,notes,startDate,endDate,success,error);

  // if you need to find events in a specific calendar, use this one. All options are currently ignored when finding events, except for the calendarName.
  var calOptions = window.plugins.calendar.getCalendarOptions();
  calOptions.calendarName = "MyCreatedCalendar"; // iOS only
  calOptions.id = "D9B1D85E-1182-458D-B110-4425F17819F1"; // if not found, we try matching against title, etc
  window.plugins.calendar.findEventWithOptions(title,eventLocation,notes,startDate,endDate,calOptions,success,error);

  // list all events in a date range (only supported on Android for now)
  window.plugins.calendar.listEventsInRange(startDate,endDate,success,error);

  // list all calendar names - returns this JS Object to the success callback: [{"id":"1", "name":"first"}, ..]
  window.plugins.calendar.listCalendars(success,error);

  // find all _future_ events in the first calendar with the specified name (iOS only for now, this includes a list of attendees (if any))
  window.plugins.calendar.findAllEventsInNamedCalendar(calendarName,success,error);

  // change an event (iOS only for now)
  var newTitle = "New title!";
  window.plugins.calendar.modifyEvent(title,eventLocation,notes,startDate,endDate,newTitle,eventLocation,notes,startDate,endDate,success,error);

  // or to add a reminder, make it recurring, change the calendar, or the url, use this one:
  var filterOptions = window.plugins.calendar.getCalendarOptions(); // or {} or null for the defaults
  filterOptions.calendarName = "Bla"; // iOS only
  filterOptions.id = "D9B1D85E-1182-458D-B110-4425F17819F1"; // iOS only, get it from createEventWithOptions (if not found, we try matching against title, etc)
  var newOptions = window.plugins.calendar.getCalendarOptions();
  newOptions.calendaName = "New Bla"; // make sure this calendar exists before moving the event to it
  // not passing in reminders will wipe them from the event. To wipe the default first reminder (60), set it to null.
  newOptions.firstReminderMinutes = 120;
  window.plugins.calendar.modifyEventWithOptions(title,eventLocation,notes,startDate,endDate,newTitle,eventLocation,notes,startDate,endDate,filterOptions,newOptions,success,error);

  // delete an event (you can pass nulls for irrelevant parameters). The dates are mandatory and represent a date range to delete events in.
  // note that on iOS there is a bug where the timespan must not be larger than 4 years, see issue 102 for details.. call this method multiple times if need be
  // since 4.3.0 you can match events starting with a prefix title, so if your event title is 'My app - cool event' then 'My app -' will match.
  window.plugins.calendar.deleteEvent(newTitle,eventLocation,notes,startDate,endDate,success,error);

  // delete an event, as above, but for a specific calendar (iOS only)
  window.plugins.calendar.deleteEventFromNamedCalendar(newTitle,eventLocation,notes,startDate,endDate,calendarName,success,error);

  // delete an event by id. If the event has recurring instances, all will be deleted unless `fromDate` is specified, which will delete from that date onward. (iOS and android only)
  window.plugins.calendar.deleteEventById(id,fromDate,success,error);

  // open the calendar app (added in 4.2.8):
  // - open it at 'today'
  window.plugins.calendar.openCalendar();
  // - open at a specific date, here today + 3 days
  var d = new Date(new Date().getTime() + 3*24*60*60*1000);
  window.plugins.calendar.openCalendar(d, success, error); // callbacks are optional
```

Creating an all day event:
```js
  // set the startdate to midnight and set the enddate to midnight the next day
  var startDate = new Date(2014,2,15,0,0,0,0,0);
  var endDate = new Date(2014,2,16,0,0,0,0,0);
```

Creating an event for 3 full days
```js
  // set the startdate to midnight and set the enddate to midnight 3 days later
  var startDate = new Date(2014,2,24,0,0,0,0,0);
  var endDate = new Date(2014,2,27,0,0,0,0,0);
```

Example Response IOS getCalendarOptions
```js
{
calendarId: null,
calendarName: "calendar",
firstReminderMinutes: 60,
recurrence: null,
recurrenceEndDate: null,
recurrenceInterval: 1,
secondReminderMinutes: null,
url: null
}
```

Exmaple Response IOS Calendars
```js
{
id: "258B0D99-394C-4189-9250-9488F75B399D",
name: "standard calendar",
type: "Local"
}
```

Exmaple Response IOS Event
```js
{
calendar: "Kalender",
endDate: "2016-06-10 23:59:59",
id: "0F9990EB-05A7-40DB-B082-424A85B59F90",
lastModifiedDate: "2016-06-13 09:14:02",
location: "",
message: "my description",
startDate: "2016-06-10 00:00:00",
title: "myEvent"
}
```

### Android 6 (M) Permissions
On Android 6 you need to request permission to use the Calendar at runtime when targeting API level 23+.
Even if the `uses-permission` tags for the Calendar are present in `AndroidManifest.xml`.

Since plugin version 4.5.0 we transparently handle this for you in a just-in-time manner.
So if you call `createEvent` we will pop up the permission dialog. After the user granted access
to his calendar the event will be created.

You can also manually manage and check permissions if that's your thing.
Note that the hasPermission functions will return true when:

- You're running this on iOS, or
- You're targeting an API level lower than 23, or
- You're using Android < 6, or
- You've already granted permission.

```js
  // again, this is no longer needed with plugin version 4.5.0 and up
  function hasReadWritePermission() {
    window.plugins.calendar.hasReadWritePermission(
      function(result) {
        // if this is 'false' you probably want to call 'requestReadWritePermission' now
        alert(result);
      }
    )
  }

  function requestReadWritePermission() {
    // no callbacks required as this opens a popup which returns async
    window.plugins.calendar.requestReadWritePermission();
  }
```

There are similar methods for Read and Write access only (`hasReadPermission`, etc),
although it looks like that if you request read permission you can write as well,
so you might as well stick with the example above.

Note that backward compatibility was added by checking for read or write permission in the relevant plugins functions.
If permission is needed the plugin will now show the permission request popup.
The user will then need to allow access and invoke the same method again after doing so.

## 4. Promises
If you like to use promises instead of callbacks, or struggle to create a lot of
events asynchronously with this plugin then I encourage you to take a look at
[this awesome wrapper](https://github.com/poetic-labs/native-calender-api) for
this plugin. Kudos to [John Rodney](https://github.com/JohnRodney) for this piece of art!

## 5. Credits

This plugin was enhanced for Plugman / PhoneGap Build by [Eddy Verbruggen](http://www.x-services.nl). I fixed some issues in the native code (mainly for iOS) and changed the JS-Native functions a little in order to make a universal JS API for both platforms.
* Inspired by [this nice blog of Devgirl](http://devgirl.org/2013/07/17/tutorial-how-to-write-a-phonegap-plugin-for-android/).
* Credits for the original iOS code go to [Felix Montanez](https://github.com/felixactv8/Phonegap-Calendar-Plugin-ios).
* Credits for the original Android code go to [Ten Forward Consulting](https://github.com/tenforwardconsulting/Phonegap-Calendar-Plugin-android) and [twistandshout](https://github.com/twistandshout/phonegap-calendar-plugin).
* Special thanks to [four32c.com](http://four32c.com) for sponsoring part of the implementation, while keeping the plugin opensource.

## 6. License

[The MIT License (MIT)](http://www.opensource.org/licenses/mit-license.html)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
