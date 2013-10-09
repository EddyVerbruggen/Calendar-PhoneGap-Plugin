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

Calendar.prototype.deleteEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "deleteEvent", [title, location, notes, startDate.getTime(), endDate.getTime()]);
};

Calendar.prototype.findEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "findEvent", [title, location, notes, startDate.getTime(), endDate.getTime()]);
};

Calendar.prototype.modifyEvent = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, successCallback, errorCallback) {
  cordova.exec(successCallback, errorCallback, "Calendar", "modifyEvent", [title, location, notes, startDate.getTime(), endDate.getTime(), newTitle, newLocation, newNotes, newStartDate.getTime(), newEndDate.getTime()]);
};

Calendar.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }

  window.plugins.calendar = new Calendar();
  return window.plugins.calendar;
};

cordova.addConstructor(Calendar.install);