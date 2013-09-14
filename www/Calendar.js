"use strict";
function Calendar() {}

Calendar.prototype.createEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
    if (typeof errorCallback != "function") {
        console.log("Calendar.createEvent failure: errorCallback parameter must be a function");
        return
    }
    
    if (typeof successCallback != "function") {
        console.log("Calendar.createEvent failure: successCallback parameter must be a function");
        return
    }
    cordova.exec(successCallback, errorCallback, 'Calendar', 'createEvent', [title, location, notes, startDate.getTime(), endDate.getTime()]);
};

Calendar.prototype.deleteEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
    if (typeof errorCallback != "function") {
        console.log("Calendar.deleteEvent failure: errorCallback parameter must be a function");
        return
    }
    
    if (typeof successCallback != "function") {
        console.log("Calendar.deleteEvent failure: successCallback parameter must be a function");
        return
    }
    cordova.exec(successCallback, errorCallback, "Calendar", "deleteEvent", [title, location, notes, startDate.getTime(), endDate.getTime()]);
};

Calendar.prototype.findEvent = function (title, location, notes, startDate, endDate, successCallback, errorCallback) {
    if (typeof errorCallback != "function") {
        console.log("Calendar.findEvent failure: errorCallback parameter must be a function");
        return
    }
    
    if (typeof successCallback != "function") {
        console.log("Calendar.findEvent failure: successCallback parameter must be a function");
        return
    }
    cordova.exec(successCallback, errorCallback, "Calendar", "findEvent", [title, location, notes, startDate.getTime(), endDate.getTime()]);
};

Calendar.prototype.modifyEvent = function (title, location, notes, startDate, endDate, newTitle, newLocation, newNotes, newStartDate, newEndDate, successCallback, errorCallback) {
    if (typeof errorCallback != "function") {
        console.log("Calendar.modifyEvent failure: errorCallback parameter must be a function");
        return
    }
    
    if (typeof successCallback != "function") {
        console.log("Calendar.modifyEvent failure: successCallback parameter must be a function");
        return
    }
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