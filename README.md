# PhoneGap Calendar plugin

for iOS and Android, by [Eddy Verbruggen](http://www.x-services.nl)

1. [Description](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#1-description)
2. [Installation](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#2-installation)
	2. [Automatically (CLI / Plugman)](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#automatically-cli--plugman)
	2. [Manually](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#manually)
	2. [PhoneGap Build](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#phonegap-build)
3. [Usage](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#3-usage)
4. [Credits](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#4-credits)
5. [License](https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin#5-license)

## 1. Description

This plugin allows you to add events to the Calendar of the mobile device.

* Works with PhoneGap >= 3.0.
* For pre-3.0, see [the pre-3.0 branch]( (https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin/tree/pre-3.0).
* Compatible with [Cordova Plugman](https://github.com/apache/cordova-plugman).
* [Officially supported by PhoneGap Build](https://build.phonegap.com/plugins/100).

### iOS specifics
* Supported methods: `find`, `create`, `modify`, `delete`.
* All methods work without showing the native calendar. Your app never looses control.
* Tested on iOS 6 and 7.

### Android specifics
* Only the `create` method is supported (more may come, if people request it, but creating is the most important thing, right?).
* When the `create` method is called, the use is presented a prefilled calendar item. Pressing the hardware back button will give control back to your app.
* Tested on Android 4.

## 2. Installation

### Automatically (CLI / Plugman)
Calendar is compatible with [Cordova Plugman](https://github.com/apache/cordova-plugman) and ready for the [PhoneGap 3.0 CLI](http://docs.phonegap.com/en/3.0.0/guide_cli_index.md.html#The%20Command-line%20Interface_add_features), here's how it works with the CLI:

```
$ phonegap local plugin add https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin.git
```
or
```
$ cordova plugin add https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin.git
```
don't forget to run this command afterwards:
```
$ cordova build
```

### Manually

1\. Add the following xml to your `config.xml`:
```xml
<!-- for iOS -->
<feature name="Calendar">
	<param name="ios-package" value="Calendar" />
</feature>
```

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

3\. Download the source files for iOS and/or Android and copy them to your project.

iOS: Copy `Calendar.h` and `Calendar.m` to `platforms/ios/<ProjectName>/Plugins`

Android: Copy `Calendar.java` to `platforms/android/src/nl/xservices/plugins` (create the folders)

### PhoneGap Build

Using Calendar with PhoneGap Build requires these simple steps:

1\. Add the following xml to your `config.xml` to always use the latest version of this plugin:
```xml
<gap:plugin name="nl.x-services.plugins.calendar" />
```
or to use this exact version:
```xml
<gap:plugin name="nl.x-services.plugins.calendar" version="1.0" />
```

2\. Reference the JavaScript code in your `index.html`:
```html
<!-- below <script src="phonegap.js"></script> -->
<script src="js/plugins/Calendar.js"></script>
```


## 3. Usage

Basic operations, you'll want to copy-paste this for testing purposes:

```javascript
  // prep some variables
  var startDate = new Date("September 24, 2013 13:00:00");
  var endDate = new Date("September 24, 2013 14:30:00");
  var title = "My nice event";
  var location = "Home";
  var notes = "Some notes about this event.";
  var success = function(message) { alert("Success: " + JSON.stringify(message)); };
  var error = function(message) { alert("Error: " + message); };

  // create a calendar (iOS only for now)
  window.plugins.calendar.createCalendar(calendarName,success,error);

  // delete a calendar (iOS only for now)
  window.plugins.calendar.deleteCalendar(calendarName,success,error);

  // create (the only function also supported on Android for now)
  window.plugins.calendar.createEvent(title,location,notes,startDate,endDate,success,error);

  // create in a named calendar (iOS only for now)
  window.plugins.calendar.createEventInNamedCalendar(title,location,notes,startDate,endDate,calendarName,success,error);

  // find (iOS only for now)
  window.plugins.calendar.findEvent(title,location,notes,startDate,endDate,success,error);

  // find all events in a named calendar (iOS only for now)
  window.plugins.calendar.findAllEventsInNamedCalendar(calendarName,success,error);

  // change an event (iOS only for now)
  var newTitle = "New title!";
  window.plugins.calendar.modifyEvent(title,location,notes,startDate,endDate,newTitle,location,notes,startDate,endDate,success,error);

  // delete (iOS only for now)
  // note that it deletes all matching events, which are duplicates anyway
  window.plugins.calendar.deleteEvent(newTitle,location,notes,startDate,endDate,success,error);
```

Creating an all day event:

```javascript
  // set the startdate to midnight and set the enddate to midnight the next day
  var startDate = new Date("September 24, 2013 00:00:00");
  var endDate = new Date("September 25, 2013 00:00:00");
```

Creating an event for 3 full days

```javascript
  // set the startdate to midnight and set the enddate to midnight 3 days later
  var startDate = new Date("September 24, 2013 00:00:00");
  var endDate = new Date("September 27, 2013 00:00:00");
```


## 4. CREDITS ##

This plugin was enhanced for Plugman / PhoneGap Build by [Eddy Verbruggen](http://www.x-services.nl). I fixed some issues in the native code (mainly for iOS) and changed the JS-Native functions a little in order to make a universal JS API for both platforms.
* Inspired by [this nice blog of Devgirl](http://devgirl.org/2013/07/17/tutorial-how-to-write-a-phonegap-plugin-for-android/).
* Credits for the original iOS code go to [Felix Montanez](https://github.com/felixactv8/Phonegap-Calendar-Plugin-ios).
* Credits for the original Android code go to [Ten Forward Consulting](https://github.com/tenforwardconsulting/Phonegap-Calendar-Plugin-android).
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


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/EddyVerbruggen/calendar-phonegap-plugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

