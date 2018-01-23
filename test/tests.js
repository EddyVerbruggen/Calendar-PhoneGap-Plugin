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

  var promisifyScbEcb = function (func) {
    if (typeof(func) != 'function')
      throw 'not a function: ' + func;
    return function () {
      var args = arguments;
      return new Promise(function (resolve, reject) {
        func.apply(null, Array.prototype.slice.call(args).concat(resolve, reject));
      });
    };
  };
  var deleteEventP = promisifyScbEcb(plugins.calendar.deleteEvent);
  var findEventP = promisifyScbEcb(plugins.calendar.findEvent);
  var createEventP = promisifyScbEcb(plugins.calendar.createEvent);
  var parseEventDate = plugins.calendar.parseEventDate;

  var newDate = function (dd, hh, mm) {
    return new Date(2018, 0, 21 + dd, hh || 0, mm || 0);
  };


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
  });

};
