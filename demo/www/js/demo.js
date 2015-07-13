'use strict';

var title = 'My Event Title';
var loc = 'My Event Location';
var notes = 'My interesting Event notes.';
var startDate = new Date();
var endDate = new Date();
var calendarName = 'MyCal';
var options = {
  url: 'https://github.com/EddyVerbruggen/Calendar-PhoneGap-Plugin',
  calendarName: calendarName, // iOS specific
  calendarId: 1 // Android specific
};

// clean up the dates a bit
startDate.setMinutes(0);
endDate.setMinutes(0);
startDate.setSeconds(0);
endDate.setSeconds(0);

// add a few hours to the dates, JS will automatically update the date (+1 day) if necessary
startDate.setHours(startDate.getHours() + 2);
endDate.setHours(endDate.getHours() + 3);

function onSuccess(msg) {
  alert('Calendar success: ' + JSON.stringify(msg));
}

function onError(msg) {
  alert('Calendar error: ' + JSON.stringify(msg));
}

function openCalendar() {
  // today + 3 days
  var d = new Date(new Date().getTime() + 3 * 24 * 60 * 60 * 1000);
  window.plugins.calendar.openCalendar(d, onSuccess, onError);
}

function listCalendars() {
  window.plugins.calendar.listCalendars(onSuccess, onError);
}

function createCalendar() {
  window.plugins.calendar.createCalendar(calendarName, onSuccess, onError);
}

function deleteEvent() {
  window.plugins.calendar.deleteEvent(title, loc, notes, startDate, endDate, onSuccess, onError);
}

function createCalendarEvent() {
  window.plugins.calendar.createEvent(title, loc, notes, startDate, endDate, onSuccess, onError);
}

function createCalendarEventInteractively() {
  window.plugins.calendar.createEventInteractively(title, loc, notes, startDate, endDate, onSuccess, onError);
}

function createCalendarEventInteractivelyWithOptions() {
  window.plugins.calendar.createEventInteractivelyWithOptions(title, loc, notes, startDate, endDate, options, onSuccess, onError);
}

function createCalendarEventWithOptions() {
  window.plugins.calendar.createEventWithOptions(title, loc, notes, startDate, endDate, options, onSuccess, onError)
}

function findEventWithFilter() {
  window.plugins.calendar.findEvent(title, loc, notes, startDate, endDate, onSuccess, onError);
}

function findEventNoFilter() {
  window.plugins.calendar.findEvent(null, null, null, startDate, endDate, onSuccess, onError);
}

window.onerror = function(msg, file, line) {
  alert(msg + '; ' + file + '; ' + line);
};
