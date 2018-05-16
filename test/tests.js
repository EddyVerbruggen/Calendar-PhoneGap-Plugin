exports.defineAutoTests = function() {
  
  var itP = function (description, fn, timeout) {
    it(description, function (done) {
      Promise.resolve(fn())
        .catch(fail)
        .then(done, done);
    }, timeout);
  };

  var runTime = new Date();
  var runId = Math.floor((runTime - new Date(runTime).setHours(0, 0, 0, 0)) / 1000);
  var runTag = ' [cpctZQX' + runId + ']';

  var delay = function (t, v) {
    return new Promise(function (resolve) {
      setTimeout(resolve.bind(null, v), t)
    });
  };
  var promisifyScbEcb = function (func) {
    if (typeof (func) != 'function')
      throw 'not a function: ' + func;
    return function () {
      var args = Array.prototype.slice.call(arguments);
      if (args.length > func.length - 2)
        throw 'too many arguments; expected at most ' + func.length - 2;
      return new Promise(function (resolve, reject) {
        args[func.length - 2] = resolve;
        args[func.length - 1] = reject;
        func.apply(null, args);
      });
    };
  };
  var deleteEventP = promisifyScbEcb(plugins.calendar.deleteEvent);
  var findEventP = promisifyScbEcb(plugins.calendar.findEvent);
  var createEventP = promisifyScbEcb(plugins.calendar.createEvent);
  var createEventWithOptionsP = promisifyScbEcb(plugins.calendar.createEventWithOptions);
  var deleteEventByIdP = promisifyScbEcb(plugins.calendar.deleteEventById);
  var syncAndroidGoogleCalendarP = promisifyScbEcb(function(successCallback, errorCallback) {
    if (cordova.platformId == 'android') {
      cordova.exec(successCallback, errorCallback, "CalendarTestsUtility", "syncAndroidGoogleCalendar", []);
    } else {
      successCallback();
    }
  });
  var parseEventDate = plugins.calendar.parseEventDate;

  var newDate = function (dd, hh, mm) {
    return new Date(2018, 0, 21 + dd, hh || 0, mm || 0);
  };


  jasmine.DEFAULT_TIMEOUT_INTERVAL= 60000;

  beforeEach(function (done) {
    /* clean up autotest data */
    return deleteEventP('[cpctZQX', null, null, newDate(1), newDate(8))
      .then(function () {
        return findEventP(runTag, null, null, newDate(1), newDate(8));
      })
      .then(function (events) {
        expect(events.length).toBe(0, 'if test data was cleaned up');
      })
      .catch(fail)
      .then(done, done);
  });


  describe('Plugin availability', function () {
    it("window.plugins.calendar should exist", function() {
      expect(window.plugins.calendar).toBeDefined();
    });
  });

  describe('API functions', function () {
    it("should define createEventWithOptions", function() {
      expect(window.plugins.calendar.createEventWithOptions).toBeDefined();
    });
  });

  // subsequent tests cover functionality specific to iOS and android
  if (cordova.platformId != 'android' && cordova.platformId != 'ios')
    return;

  describe('createEvent / findEvent / deleteEvent', function () {
    itP('should create, find, then delete an event', function () {
      var title = 'CFD event' + runTag;

      // create
      return createEventP(title, null, null, newDate(2, 18), newDate(2, 19))
        .then(function (id) {
          // find
          return findEventP(title, null, null, newDate(2, 17), newDate(2, 20))
            .then(function (events) {
              expect(events.length).toBe(1);
              expect(events[0].title).toBe(title);
              expect(parseEventDate(events[0].startDate)).toEqual(newDate(2, 18));
              expect(parseEventDate(events[0].endDate)).toEqual(newDate(2, 19));
              expect(events[0].id).toBe(id);
            });
        })
        .then(function () {
          // delete
          return deleteEventP(title, null, null, newDate(2, 17), newDate(2, 20))
            .then(function () {
              return findEventP(title, null, null, newDate(2, 17), newDate(2, 20))
                .then(function (events) {
                  expect(events.length).toBe(0);
                });
            });
        });
    });

    itP('should support delete by title/date/location/notes', function () {
      var title = 'DF event' + runTag + ' ';
      var first, bytitle, bydate, bylocation, bynotes, last;

      var expectedIds;
      var removeExpectedId = function (id) {
        expectedIds = expectedIds.filter(function (x) { return x != id; });
      };
      var expectIds = function (events) {
        expect(events.map(function (e) { return e.id; })).toEqual(expectedIds);
      };

      // create
      return createEventP(title + 'first', null, null, newDate(1, 7), newDate(1, 8))
        .then(function (id) {
          first = id;
          return createEventP(title + 'bytitle', null, null, newDate(1, 9), newDate(1, 10));
        })
        .then(function (id) {
          bytitle = id;
          return createEventP(title + 'bydate', null, null, newDate(1, 11), newDate(1, 13));
        })
        .then(function (id) {
          bydate = id;
          return createEventP(title, 'bylocation', null, newDate(1, 14), newDate(1, 15));
        })
        .then(function (id) {
          bylocation = id;
          return createEventP(title, null, 'bynotes', newDate(1, 16), newDate(1, 17));
        })
        .then(function (id) {
          bynotes = id;
          return createEventP(title + 'last', null, null, newDate(1, 18), newDate(1, 19));
        })
        .then(function (id) {
          last = id;

          // find
          expectedIds = [first, bytitle, bydate, bylocation, bynotes, last];
          return findEventP(title, null, null, newDate(1, 0), newDate(2, 0));
        })
        .then(function (events) {
          expectIds(events);

          // delete by title
          removeExpectedId(bytitle);
          return deleteEventP(title + 'bytitle', null, null, newDate(1, 0), newDate(2, 0));
        })
        .then(function () {
          // find
          return findEventP(title, null, null, newDate(1, 0), newDate(2, 0));
        })
        .then(function (events) {
          expectIds(events);

          // delete by date
          removeExpectedId(bydate);
          return deleteEventP(null, null, null, newDate(1, 11), newDate(1, 13));
        })
        .then(function () {
          // find
          return findEventP(title, null, null, newDate(1, 0), newDate(2, 0));
        })
        .then(function (events) {
          expectIds(events);

          // delete by location
          removeExpectedId(bylocation);
          return deleteEventP(null, 'bylocation', null, newDate(1, 0), newDate(2, 0));
        })
        .then(function () {
          // find
          return findEventP(title, null, null, newDate(1, 0), newDate(2, 0));
        })
        .then(function (events) {
          expectIds(events);

          // delete by notes
          removeExpectedId(bynotes);
          return deleteEventP(null, null, 'bynotes', newDate(1, 0), newDate(2, 0));
        })
        .then(function () {
          // find
          return findEventP(title, null, null, newDate(1, 0), newDate(2, 0));
        })
        .then(function (events) {
          expectIds(events);

          // delete the rest
          return deleteEventP(title, null, null, newDate(1, 0), newDate(2, 0));
        });
    });
  });

  describe('deleteEventById', function () {

    var createRecurring = function (title, withCount) {
      // create
      var createOpts = withCount
        ? { recurrence: "daily", recurrenceCount: 4 }
        : { recurrence: "daily", recurrenceEndDate: newDate(6, 0) };
      return createEventWithOptionsP(title, null, null, newDate(2, 18), newDate(2, 19), createOpts)
        .then(function (id) {
          // find
          return findEventP(title, null, null, newDate(2, 0), newDate(8, 0))
            .then(function (events) {
              expect(events.length).toBe(4);
              expect(events.every(function (x) { return x.id == id; })).toBe(true);

              // pedantic checks
              expect(parseEventDate(events[2].startDate)).toEqual(newDate(4, 18));
              if (!withCount) {
                var ev = events[0];
                var until = ev.recurrence ? ev.recurrence.until : ev.rrule.until.date;
                expect(parseEventDate(until)).toEqual(newDate(6, 0));
              }

              return id;
            });
        });
    };

    itP('should support removing all instances', function () {
      var title = 'delIdAll event' + runTag;

      return createRecurring(title)
        .then(function (id) {
          return deleteEventByIdP(id);
        })
        .then(function () {
          return findEventP(title, null, null, newDate(2, 0), newDate(8, 0));
        })
        .then(function (events) {
          expect(events.length).toBe(0);
        });
    });

    itP('should support truncating series', function () {
      var title = 'delIdDate event' + runTag;

      return createRecurring(title)
        .then(function (id) {
          return deleteEventByIdP(id, newDate(4, 18));
        })
        .then(function () {
          return syncAndroidGoogleCalendarP();
        })
        .then(function () {
          return findEventP(title, null, null, newDate(2, 0), newDate(8, 0));
        })
        .then(function (events) {
          expect(events.length).toBe(2);
          expect(parseEventDate(events[1].startDate)).toEqual(newDate(3, 18));
        });
    });

    itP('should fail on invalid id', function () {
      var failed = false;
      return deleteEventByIdP('3826806B-1678-46DE-96B5-0748014AD920')
        .catch(function () {
          failed = true;
        })
        .then(function () {
          expect(failed).toBe(true);
        });
    });

    itP('should succeed if already truncated', function () {
      var title = 'delIdAgain event' + runTag;

      return createRecurring(title)
        .then(function (id) {
          return deleteEventByIdP(id, newDate(5, 19));
        })
        .then(function () {
          return findEventP(title, null, null, newDate(2, 0), newDate(8, 0));
        })
        .then(function (events) {
          expect(events.length).toBe(4);
        });
    });

    if (cordova.platformId == 'android') {
      itP('should support truncating recurrences defined by a count in android', function() {
        var title = 'delIdCtDate event' + runTag;

        return createRecurring(title, true)
          .then(function (id) {
            return deleteEventByIdP(id, newDate(4, 18));
          })
          .then(function () {
            return syncAndroidGoogleCalendarP();
          })
          .then(function () {
            return findEventP(title, null, null, newDate(2, 0), newDate(8, 0));
          })
          .then(function (events) {
            expect(events.length).toBe(2);
            expect(parseEventDate(events[1].startDate)).toEqual(newDate(3, 18));
          });
      });

      itP('should succeed if already truncated by a count in android', function () {
        var title = 'delIdCtAgain event' + runTag;

        return createRecurring(title, true)
          .then(function (id) {
            return deleteEventByIdP(id, newDate(5, 19));
          })
          .then(function () {
            return findEventP(title, null, null, newDate(2, 0), newDate(8, 0));
          })
          .then(function (events) {
            expect(events.length).toBe(4);
          });
      });
    };
  });

};
