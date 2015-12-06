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
4. [Credits](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#4-credits)
5. [License](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#5-license)

## 1. Description

This plugin allows you to add events to the Calendar of the mobile device.

* Works with PhoneGap >= 3.0.
* For PhoneGap 2.x, see [the pre-3.0 branch](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin/tree/pre-3.0).
* Compatible with [Cordova Plugman](https://github.com/apache/cordova-plugman).
* [Officially supported by PhoneGap Build](https://build.phonegap.com/plugins).

### iOS specifics
* Supported methods: `find`, `create`, `modify`, `delete`, ..
* All methods work without showing the native calendar. Your app never loses control.
* Tested on iOS 6 and 7.

### Android specifics
* Supported methods on Android 4: `find`, `create` (silent and interactive), `delete`, ..
* Supported methods on Android 2 and 3: `create` interactive only: the user is presented a prefilled Calendar event. Pressing the hardware back button will give control back to your app.

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

Operation                           | Comment     | iOS | Android
----------------------------------- | ----------- | --- | -------
createCalendar                      |             | yes | 
deleteCalendar                      |             | yes | 
createEvent                         | silent      | yes | yes (on Android < 4 dialog is shown)
createEventWithOptions              | silent      | yes | yes (on Android < 4 dialog is shown)
createEventInteractively            | interactive | yes | yes
createEventInteractivelyWithOptions | interactive | yes | yes
findEvent                           |             | yes | yes
findEventWithOptions                |             | yes | yes
listEventsInRange                   |             |     | yes
listCalendars                       |             | yes | yes
findAllEventsInNamedCalendars       |             | yes | 
modifyEvent                         |             | yes | 
modifyEventWithOptions              |             | yes | 
deleteEvent                         |             | yes | yes
deleteEventFromNamedCalendar        |             | yes | 
openCalendar                        |             | yes | yes

Basic operations, you'll want to copy-paste this for testing purposes:

```javascript
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

  // delete a calendar (iOS only for now)
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
  calOptions.id = "D9B1D85E-1182-458D-B110-4425F17819F1"; // iOS only, get it from createEventWithOptions (if not found, we try matching against title, etc)
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

  // delete an event (you can pass nulls for irrelevant parameters, note that on Android `notes` is ignored). The dates are mandatory and represent a date range to delete events in.
  // note that on iOS there is a bug where the timespan must not be larger than 4 years, see issue 102 for details.. call this method multiple times if need be
  // since 4.3.0 you can match events starting with a prefix title, so if your event title is 'My app - cool event' then 'My app -' will match.
  window.plugins.calendar.deleteEvent(newTitle,eventLocation,notes,startDate,endDate,success,error);

  // delete an event, as above, but for a specific calendar (iOS only)
  window.plugins.calendar.deleteEventFromNamedCalendar(newTitle,eventLocation,notes,startDate,endDate,calendarName,success,error);

  // open the calendar app (added in 4.2.8):
  // - open it at 'today'
  window.plugins.calendar.openCalendar();
  // - open at a specific date, here today + 3 days
  var d = new Date(new Date().getTime() + 3*24*60*60*1000);
  window.plugins.calendar.openCalendar(d, success, error); // callbacks are optional
```

Creating an all day event:

```javascript
  // set the startdate to midnight and set the enddate to midnight the next day
  var startDate = new Date(2014,2,15,0,0,0,0,0);
  var endDate = new Date(2014,2,16,0,0,0,0,0);
```

Creating an event for 3 full days

```javascript
  // set the startdate to midnight and set the enddate to midnight 3 days later
  var startDate = new Date(2014,2,24,0,0,0,0,0);
  var endDate = new Date(2014,2,27,0,0,0,0,0);
```

### Android 6 (M) Permissions
On Android 6 you need to request permission to use the Calenda at runtime when targeting API level 23+.
Even if the uses-permission tags the Calendar are present in `AndroidManifest.xml`.

Note that the hasPermission functions will return true when:

- You're running this on iOS, or
- You're targeting an API level lower than 23, or
- You're using Android < 6, or
- You've already granted permission.

```js
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

## 4. CREDITS ##

This plugin was enhanced for Plugman / PhoneGap Build by [Eddy Verbruggen](http://www.x-services.nl). I fixed some issues in the native code (mainly for iOS) and changed the JS-Native functions a little in order to make a universal JS API for both platforms.
* Inspired by [this nice blog of Devgirl](http://devgirl.org/2013/07/17/tutorial-how-to-write-a-phonegap-plugin-for-android/).
* Credits for the original iOS code go to [Felix Montanez](https://github.com/felixactv8/Phonegap-Calendar-Plugin-ios).
* Credits for the original Android code go to [Ten Forward Consulting](https://github.com/tenforwardconsulting/Phonegap-Calendar-Plugin-android) and [twistandshout](https://github.com/twistandshout/phonegap-calendar-plugin).
* Special thanks to [four32c.com](http://four32c.com) for sponsoring part of the implementation, while keeping the plugin opensource.

## 5. License

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
