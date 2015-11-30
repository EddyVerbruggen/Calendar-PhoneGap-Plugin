#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import <EventKitUI/EventKitUI.h>
#import <EventKit/EventKit.h>

@interface Calendar : CDVPlugin <EKEventEditViewDelegate>

@property (nonatomic, retain) EKEventStore* eventStore;
@property (nonatomic, copy) NSString *interactiveCallbackId;

- (void)initEventStoreWithCalendarCapabilities;

-(NSArray*)findEKEventsWithTitle: (NSString *)title
                        location: (NSString *)location
                           notes: (NSString *)notes
                       startDate: (NSDate *)startDate
                         endDate: (NSDate *)endDate
                       calendars: (NSArray *)calendars;

- (void)hasReadPermission:(CDVInvokedUrlCommand*)command;
- (void)requestReadPermission:(CDVInvokedUrlCommand*)command;

- (void)hasWritePermission:(CDVInvokedUrlCommand*)command;
- (void)requestWritePermission:(CDVInvokedUrlCommand*)command;

- (void)hasReadWritePermission:(CDVInvokedUrlCommand*)command;
- (void)requestReadWritePermission:(CDVInvokedUrlCommand*)command;

- (void)openCalendar:(CDVInvokedUrlCommand*)command;
- (void)createCalendar:(CDVInvokedUrlCommand*)command;
- (void)deleteCalendar:(CDVInvokedUrlCommand*)command;

- (void)createEventWithOptions:(CDVInvokedUrlCommand*)command;
- (void)createEventInteractively:(CDVInvokedUrlCommand*)command;
- (void)modifyEventWithOptions:(CDVInvokedUrlCommand*)command;

- (void)findEventWithOptions:(CDVInvokedUrlCommand*)command;
- (void)findAllEventsInNamedCalendar:(CDVInvokedUrlCommand*)command;

- (void)listCalendars:(CDVInvokedUrlCommand*)command;
- (void)deleteEvent:(CDVInvokedUrlCommand*)command;
- (void)deleteEventFromNamedCalendar:(CDVInvokedUrlCommand*)command;
- (void)deleteEventFromCalendar:(CDVInvokedUrlCommand*)command calendar:(EKCalendar*)calendar;
- (void)eventEditViewController:(EKEventEditViewController*)controller didCompleteWithAction:(EKEventEditViewAction) action;

@end
