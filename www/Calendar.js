"use strict";
function Calendar() {
}

Calendar.prototype.createCalendar = function (calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "createCalendar", [calendarName]);
};

Calendar.prototype.deleteCalendar = function (calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteCalendar", [calendarName]);
};

Calendar.prototype.createEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "createEvent", [title, location, notes, startDate.getTime(), endDate.getTime()]);
};

Calendar.prototype.createEventInNamedCalendar = function (title, location, notes, startDate, endDate, calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "createEventInNamedCalendar", [title, location, notes, startDate.getTime(), endDate.getTime(), calendarName]);
};

Calendar.prototype.deleteEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteEvent", [title, location, notes, startDate.getTime(), endDate.getTime()]);
};

Calendar.prototype.deleteEventFromNamedCalendar = function (title, location, notes, startDate, endDate, calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteEventFromNamedCalendar", [title, location, notes, startDate.getTime(), endDate.getTime(), calendarName]);
};

Calendar.prototype.findEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "findEvent", [title, location, notes, startDate.getTime(), endDate.getTime()]);
};

Calendar.prototype.findAllEventsInNamedCalendar = function (calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "findAllEventsInNamedCalendar", [calendarName]);
};

Calendar.prototype.modifyEvent = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "modifyEvent", [title, location, notes, startDate.getTime(), endDate.getTime(), newTitle, newLocation, newNotes, newStartDate.getTime(), newEndDate.getTime()]);
};

Calendar.prototype.modifyEventInNamedCalendar = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, calendarName, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "modifyEventInNamedCalendar", [title, location, notes, startDate.getTime(), endDate.getTime(), newTitle, newLocation, newNotes, newStartDate.getTime(), newEndDate.getTime(), calendarName]);
};

Calendar.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.calendar = new Calendar();
  return window.plugins.calendar;
};

cordova.addConstructor(Calendar.install);