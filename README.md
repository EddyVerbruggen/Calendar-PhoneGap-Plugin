# Calendar Plugin for Phonegap #
(c) 2013 Ten Forward Consulting, Inc. released under the MIT License

Authored by Brian Samson (@samsonasu) and Ryan Behnke

This plugin is compatible with PhoneGap 2.0, and the api was based on the corresponding [iOS plugin](https://github.com/felixactv8/Phonegap-Calendar-Plugin-ios)

## Adding the Plugin to your project ##

1) To install the plugin, add `calendar.js` to your `index.html` and to your `www` folder: 

`<script type="text/javascript" src="calendar.js"></script>`

2) Add CalendarPlugin.java to your Android project in `src/com/tenforwardconsulting/phonegap/plugins`

3) Map the plugin in `res/xml/config.xml`:

   `<plugin name="CalendarPlugin" value="com.tenforwardconsulting.phonegap.plugins.CalendarPlugin"/>`

## Usage ##

Create an object to be used to call the defined plugin methods.

    var startDate = new Date("August 20, 2013 10:00:00");
    var endDate = new Date("August 20, 2013 11:00:00");
    var title = "Hack on Phonegap";
    var location = "The Basement";
    var notes = "Hacking on open source projects late at night is the best!";
    var success = function() { alert("woo hoo!"); };
    var error = function(message) { alert("Doh!"); };
    window.plugins.calendarPlugin.createEvent(title,location,notes,startDate,endDate, success, error);

That's it!

## Caveats ##

* The only API that is implemented is createEvent.  If you have a need for the other apis, feel free to implement them and send a pull request :) 

* This uses an undocumented API (that was finally documented in Jelly Bean).  As such it may not work on pre 4.0 devices, but I've had very good luck so keep your fingers crossed.


