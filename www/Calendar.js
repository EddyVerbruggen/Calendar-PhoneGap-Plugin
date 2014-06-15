"use strict";
function Calendar() {
}

Calendar.prototype.getCreateCalendarOptions = function () {
  return {
    calendarName: null,
    calendarColor: null // optional, the OS will choose one if left empty, example: pass "#FF0000" for red
  };
};

Calendar.prototype.createCalendar = function (calendarNameOrOptionsObject, successCallback, errorCallback) {
  var options;
  if (typeof calendarNameOrOptionsObject == "string") {
    options = {
      "calendarName": calendarNameOrOptionsObject
    };
  } else {
    options = calendarNameOrOptionsObject;
  }
  // merge passed options with defaults
  var mergedOptions = Calendar.prototype.getCreateCalendarOptions();
  for (var val in options) {
    if (options.hasOwnProperty(val)) {
      mergedOptions[val] = options[val];
    }
  }
  cordova.exec(successCallback, errorCallback, "Calendar", "createCalendar", [mergedOptions]);
};

Calendar.prototype.deleteCalendar = function (calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteCalendar", [{
    "calendarName": calendarName
  }]);
};

Calendar.prototype.getCalendarOptions = function () {
  return {
    firstReminderMinutes: 60,
    secondReminderMinutes: null,
    recurrence: null, // options are: 'daily', 'weekly', 'monthly', 'yearly'
    recurrenceEndDate: null,
    calendarName: null
  };
};

/**
 * This method can be used if you want more control over the event details.
 * Pass in an options object which you can easily override as follow:
 *   var options = window.plugins.calendar.getCalendarOptions();
 *   options.firstReminderMinutes = 150;
 */
Calendar.prototype.createEventWithOptions = function (title, location, notes, startDate, endDate, options, successCallback, errorCallback) {
  if (!(startDate instanceof Date && endDate instanceof Date)) {
    errorCallback("startDate and endDate must be JavaScript Date Objects");
    return;
  }

  // merge passed options with defaults
  var mergedOptions = Calendar.prototype.getCalendarOptions();
  for (var val in options) {
    if (options.hasOwnProperty(val)) {
      mergedOptions[val] = options[val];
    }
  }
  if (options.recurrenceEndDate != null) {
    mergedOptions.recurrenceEndTime = options.recurrenceEndDate.getTime();
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
  if (!(startDate instanceof Date && endDate instanceof Date)) {
    errorCallback("startDate and endDate must be JavaScript Date Objects");
  }
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
  if (!(newStartDate instanceof Date && newEndDate instanceof Date)) {
    errorCallback("newStartDate and newEndDate must be JavaScript Date Objects");
    return;
  }
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
  if (!(newStartDate instanceof Date && newEndDate instanceof Date)) {
    errorCallback("newStartDate and newEndDate must be JavaScript Date Objects");
    return;
  }
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
