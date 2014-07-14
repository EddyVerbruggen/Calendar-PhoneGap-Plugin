exports.defineAutoTests = function() {
  
  var fail = function (done) {
    expect(true).toBe(false);
    done();
  },
  succeed = function (done) {
    expect(true).toBe(true);
    done();
  };

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

  /*
  TODO extend - this is a copy-paste example of Toast
  describe('Invalid usage', function () {
    it("should fail due to an invalid position", function(done) {
     window.plugins.toast.show('hi', 'short', 'nowhere', fail.bind(null, done), succeed.bind(null, done));
    });

    it("should fail due to an invalid duration", function(done) {
     window.plugins.toast.show('hi', 'medium', 'top', fail.bind(null, done), succeed.bind(null, done));
    });
  });
  */
};
