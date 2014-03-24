"use strict";
function Calendar() {
}

Calendar.prototype.createCalendar = function (calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "createCalendar", [{
    "calendarName": calendarName
  }]);
};

Calendar.prototype.deleteCalendar = function (calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteCalendar", [{
    "calendarName": calendarName
  }]);
};

Calendar.prototype.getCalendarOptions = function () {
  return {
    firstReminderMinutes: 60
//  secondReminderMinutes: null, // TODO: future support
//  calendarName: null, // TODO: future support
//  recurs: null // TODO: future support
  };
};

/**
 * This method can be used if you want more control over the event details.
 * Pass in an options object which you can easily override as follow:
 *   var otions = window.plugins.calendar.getCalendarOptions();
 *   options.firstReminderMinutes = 150;
 */
Calendar.prototype.createEventWithOptions = function (title, location, notes, startDate, endDate, options, successCallback, errorCallback) {
  // merge passed options with defaults
  var mergedOptions = Calendar.prototype.getCalendarOptions();
  for (var val in options) {
    if (options.hasOwnProperty(val)) {
      mergedOptions[val] = options[val];
    }
  }
  cordova.exec(successCallback, errorCallback, "Calendar", "createEventWithOptions", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null,
    "options": mergedOptions
  }])
};

Calendar.prototype.createEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  Calendar.prototype.createEventWithOptions(title, location, notes, startDate, endDate, {}, successCallback, errorCallback);
};

Calendar.prototype.createEventInteractively = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "createEventInteractively", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null,
    "options": Calendar.prototype.getCalendarOptions()
  }])
};

// TODO add calendarname to options and call that method, like we did with createEvent
Calendar.prototype.createEventInNamedCalendar = function (title, location, notes, startDate, endDate, calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "createEventInNamedCalendar", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null,
    "calendarName": calendarName
  }])
};

Calendar.prototype.deleteEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteEvent", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null
  }])
};

Calendar.prototype.deleteEventFromNamedCalendar = function (title, location, notes, startDate, endDate, calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteEventFromNamedCalendar", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null,
    "calendarName": calendarName
  }])
};

Calendar.prototype.findEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "findEvent", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null
  }])
};

Calendar.prototype.findAllEventsInNamedCalendar = function (calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "findAllEventsInNamedCalendar", [{
    "calendarName": calendarName
  }]);
};

Calendar.prototype.modifyEvent = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "modifyEvent", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null,
    "newTitle": newTitle,
    "newLocation": newLocation,
    "newNotes": newNotes,
    "newStartTime": newStartDate instanceof Date ? newStartDate.getTime() : null,
    "newEndTime": newEndDate instanceof Date ? newEndDate.getTime() : null
  }])
};

Calendar.prototype.modifyEventInNamedCalendar = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "modifyEventInNamedCalendar", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null,
    "newTitle": newTitle,
    "newLocation": newLocation,
    "newNotes": newNotes,
    "newStartTime": newStartDate instanceof Date ? newStartDate.getTime() : null,
    "newEndTime": newEndDate instanceof Date ? newEndDate.getTime() : null,
    "calendarName": calendarName
  }])
};

Calendar.prototype.listEventsInRange = function (startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "listEventsInRange", [{
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null
  }])
};

Calendar.prototype.listCalendars = function (successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "listCalendars", []);
};

Calendar.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.calendar = new Calendar();
  return window.plugins.calendar;
};

cordova.addConstructor(Calendar.install);
