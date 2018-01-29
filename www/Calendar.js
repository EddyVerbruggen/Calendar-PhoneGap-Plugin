"use strict";
function Calendar() {
}

Calendar.prototype.getCreateCalendarOptions = function () {
  return {
    calendarName: null,
    calendarColor: null // optional, the OS will choose one if left empty, example: pass "#FF0000" for red
  };
};

Calendar.prototype.hasReadPermission = function (callback) {
  cordova.exec(callback, null, "Calendar", "hasReadPermission", []);
};

Calendar.prototype.requestReadPermission = function (callback) {
  cordova.exec(callback, null, "Calendar", "requestReadPermission", []);
};

Calendar.prototype.hasWritePermission = function (callback) {
  cordova.exec(callback, null, "Calendar", "hasWritePermission", []);
};

Calendar.prototype.requestWritePermission = function (callback) {
  cordova.exec(callback, null, "Calendar", "requestWritePermission", []);
};

Calendar.prototype.hasReadWritePermission = function (callback) {
  cordova.exec(callback, null, "Calendar", "hasReadWritePermission", []);
};

Calendar.prototype.requestReadWritePermission = function (callback) {
  cordova.exec(callback, null, "Calendar", "requestReadWritePermission", []);
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

Calendar.prototype.openCalendar = function (date, successCallback, errorCallback) {
  // default: today
  if (!(date instanceof Date)) {
    date = new Date();
  }
  cordova.exec(successCallback, errorCallback, "Calendar", "openCalendar", [{
    "date": date.getTime()
  }]);
};

Calendar.prototype.getCalendarOptions = function () {
  return {
    firstReminderMinutes: 60,
    secondReminderMinutes: null,
    recurrence: null, // options are: 'daily', 'weekly', 'monthly', 'yearly'
    recurrenceInterval: 1, // only used when recurrence is set
    recurrenceWeekstart: "MO",
    recurrenceByDay: null,
    recurrenceByMonthDay: null,
    recurrenceEndDate: null,
    recurrenceCount: null,
    calendarName: null,
    calendarId: null,
    url: null
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
  }]);
};

/**
 * @deprecated use createEventWithOptions instead
 */
Calendar.prototype.createEventInNamedCalendar = function (title, location, notes, startDate, endDate, calendarName, successCallback, errorCallback) {
  Calendar.prototype.createEventWithOptions(title, location, notes, startDate, endDate, {calendarName:calendarName}, successCallback, errorCallback);
};

Calendar.prototype.createEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  Calendar.prototype.createEventWithOptions(title, location, notes, startDate, endDate, {}, successCallback, errorCallback);
};

Calendar.prototype.createEventInteractively = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  Calendar.prototype.createEventInteractivelyWithOptions(title, location, notes, startDate, endDate, {}, successCallback, errorCallback);
};

Calendar.prototype.createEventInteractivelyWithOptions = function (title, location, notes, startDate, endDate, options, successCallback, errorCallback) {
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
  cordova.exec(successCallback, errorCallback, "Calendar", "createEventInteractively", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null,
    "options": mergedOptions
  }])
};

Calendar.prototype.findEventWithOptions = function (title, location, notes, startDate, endDate, options, successCallback, errorCallback) {
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
  cordova.exec(successCallback, errorCallback, "Calendar", "findEventWithOptions", [{
    "title": title,
    "location": location,
    "notes": notes,
    "startTime": startDate instanceof Date ? startDate.getTime() : null,
    "endTime": endDate instanceof Date ? endDate.getTime() : null,
    "options": mergedOptions
  }])
};

Calendar.prototype.findEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  Calendar.prototype.findEventWithOptions(title, location, notes, startDate, endDate, {}, successCallback, errorCallback);
};

Calendar.prototype.findAllEventsInNamedCalendar = function (calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "findAllEventsInNamedCalendar", [{
    "calendarName": calendarName
  }]);
};

Calendar.prototype.deleteEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  if (!(startDate instanceof Date && endDate instanceof Date)) {
    errorCallback("startDate and endDate must be JavaScript Date Objects");
  }
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

Calendar.prototype.deleteEventById = function (id, fromDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteEventById", [{
    "id": id,
    "fromTime": fromDate instanceof Date ? fromDate.getTime() : null
  }]);
};

Calendar.prototype.modifyEventWithOptions = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, options, newOptions, successCallback, errorCallback) {
  if (!(newStartDate instanceof Date && newEndDate instanceof Date)) {
    errorCallback("newStartDate and newEndDate must be JavaScript Date Objects");
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
  // and also merge passed newOptions with defaults
  var newMergedOptions = Calendar.prototype.getCalendarOptions();
  for (var val2 in newOptions) {
    if (newOptions.hasOwnProperty(val2)) {
      newMergedOptions[val2] = newOptions[val2];
    }
  }
  if (newOptions.recurrenceEndDate != null) {
    newMergedOptions.recurrenceEndTime = newOptions.recurrenceEndDate.getTime();
  }
  cordova.exec(successCallback, errorCallback, "Calendar", "modifyEventWithOptions", [{
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
    "options": mergedOptions,
    "newOptions": newMergedOptions
  }])
};

Calendar.prototype.modifyEvent = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, successCallback, errorCallback) {
  Calendar.prototype.modifyEventWithOptions(title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, {}, successCallback, errorCallback);
};

Calendar.prototype.modifyEventInNamedCalendar = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, calendarName, successCallback, errorCallback) {
  var options = Calendar.prototype.getCalendarOptions();
  options.calendarName = calendarName;
  Calendar.prototype.modifyEventWithOptions(title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, options, successCallback, errorCallback);
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

Calendar.prototype.parseEventDate = function (dateStr) {
  // Handle yyyyMMddTHHmmssZ iCalendar UTC format
  var icalRegExp = /\b(\d{4})(\d{2})(\d{2}T\d{2})(\d{2})(\d{2}Z)\b/;
  if (icalRegExp.test(dateStr))
    return new Date(String(dateStr).replace(icalRegExp, '$1-$2-$3:$4:$5'));

  var spl;
  // Handle yyyy-MM-dd HH:mm:ss format returned by AbstractCalendarAccessor.java L66 and Calendar.m L378, and yyyyMMddTHHmmss iCalendar local format, and similar
  return (spl = /^\s*(\d{4})\D?(\d{2})\D?(\d{2})\D?(\d{2})\D?(\d{2})\D?(\d{2})\s*$/.exec(dateStr))
    && new Date(spl[1], spl[2] - 1, spl[3], spl[4], spl[5], spl[6])
    || new Date(dateStr);
};

Calendar.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.calendar = new Calendar();
  return window.plugins.calendar;
};

cordova.addConstructor(Calendar.install);
