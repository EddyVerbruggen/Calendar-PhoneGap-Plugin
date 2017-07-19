var appointment = {
    createEventWithOptions: function (successCallback, errorCallback, options) {
        var o = {
            title: null,
            location: null,
            notes: null,
            startTime: null,
            endTime: null,
            options: { // Defaults:  Calendar.prototype.getCalendarOptions()
                firstReminderMinutes: null,
                secondReminderMinutes: null,
                recurrence: null,
                recurrenceInterval: null,
                recurrenceWeekstart: null,
                recurrenceByDay: null,
                recurrenceByMonthDay: null,
                recurrenceEndDate: null,
                recurrenceCount: null,
                calendarName: null,
                calendarId: null,
                url: null
            }
        };
        o = options[0];
        var appointment = new Windows.ApplicationModel.Appointments.Appointment();

        appointment.startTime = new Date(o.startTime);
        if (o.endTime)
            appointment.duration = Math.abs(o.endTime - o.startTime); //(60 * 60 * 100000) / 100; // 1 hour in 100ms units
        else
            appointment.allDay = true;
        appointment.location = o.location || '';
        appointment.subject = o.title || '';
        appointment.details = o.notes || '';
        appointment.reminder = o.options.firstReminderMinutes;
        appointment.onlineMeetingLink = o.options.url || '';

        var boundingRect = window.document; // e.srcElement.getBoundingClientRect();
        var selectionRect = {
            x: boundingRect.left,
            y: boundingRect.top,
            width: boundingRect.width,
            height: boundingRect.height
        };

        var appointmentId = Windows.ApplicationModel.Appointments.AppointmentManager.showAddAppointmentAsync(
                appointment, selectionRect, Windows.UI.Popups.Placement.default)
            .done(function (appointmentId) {
                if (appointmentId) {
                    successCallback(appointmentId);
                } else {
                    errorCallback();
                }
            });
    }
}

cordova.commandProxy.add("Calendar", appointment);