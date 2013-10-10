#import "Calendar.h"
#import <Cordova/CDV.h>
#import <EventKitUI/EventKitUI.h>
#import <EventKit/EventKit.h>

#define SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)

@implementation Calendar
    @synthesize eventStore;
    
#pragma mark Initialisation functions
    
- (CDVPlugin*) initWithWebView:(UIWebView*)theWebView {
    self = (Calendar*)[super initWithWebView:theWebView];
    if (self) {
        [self initEventStoreWithCalendarCapabilities];
    }
    return self;
}
    
- (void)initEventStoreWithCalendarCapabilities {
    __block BOOL accessGranted = NO;
    eventStore= [[EKEventStore alloc] init];
    if([eventStore respondsToSelector:@selector(requestAccessToEntityType:completion:)]) {
        dispatch_semaphore_t sema = dispatch_semaphore_create(0);
        [eventStore requestAccessToEntityType:EKEntityTypeEvent completion:^(BOOL granted, NSError *error) {
            accessGranted = granted;
            dispatch_semaphore_signal(sema);
        }];
        dispatch_semaphore_wait(sema, DISPATCH_TIME_FOREVER);
    } else { // we're on iOS 5 or older
        accessGranted = YES;
    }
    
    if (accessGranted) {
        self.eventStore = eventStore;
    }
}
    
#pragma mark Helper Functions
    
- (void)createEventWithCalendar:(CDVInvokedUrlCommand*)command
                       calendar: (EKCalendar *) calendar {
    NSString *callbackId = command.callbackId;
    
    NSString* title      = [command.arguments objectAtIndex:0];
    NSString* location   = [command.arguments objectAtIndex:1];
    NSString* message    = [command.arguments objectAtIndex:2];
    NSString *startDate  = [command.arguments objectAtIndex:3];
    NSString *endDate    = [command.arguments objectAtIndex:4];
    
    NSTimeInterval _startInterval = [startDate doubleValue] / 1000; // strip millis
    NSDate *myStartDate = [NSDate dateWithTimeIntervalSince1970:_startInterval];
    
    NSTimeInterval _endInterval = [endDate doubleValue] / 1000; // strip millis
    
    EKEvent *myEvent = [EKEvent eventWithEventStore: self.eventStore];
    myEvent.title = title;
    myEvent.location = location;
    myEvent.notes = message;
    myEvent.startDate = myStartDate;
    
    int duration = _endInterval - _startInterval;
    int moduloDay = duration % (60*60*24);
    if (moduloDay == 0) {
        myEvent.allDay = YES;
        myEvent.endDate = [NSDate dateWithTimeIntervalSince1970:_endInterval-1];
    } else {
        myEvent.endDate = [NSDate dateWithTimeIntervalSince1970:_endInterval];
    }
    myEvent.calendar = calendar;
    
    EKAlarm *reminder = [EKAlarm alarmWithRelativeOffset:-2*60*60];
    
    [myEvent addAlarm:reminder];
    
    NSError *error = nil;
    [self.eventStore saveEvent:myEvent span:EKSpanThisEvent error:&error];
    
    if (error) {
        CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
        [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
    } else {
        NSLog(@"Reached Success");
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    }
}

-(void)modifyEventWithCalendar:(CDVInvokedUrlCommand*)command
                      calendar: (EKCalendar *) calendar {
    NSString *callbackId = command.callbackId;
    
    NSString* title      = [command.arguments objectAtIndex:0];
    NSString* location   = [command.arguments objectAtIndex:1];
    NSString* message    = [command.arguments objectAtIndex:2];
    NSString *startDate  = [command.arguments objectAtIndex:3];
    NSString *endDate    = [command.arguments objectAtIndex:4];
    
    NSString* ntitle      = [command.arguments objectAtIndex:5];
    NSString* nlocation   = [command.arguments objectAtIndex:6];
    NSString* nmessage    = [command.arguments objectAtIndex:7];
    NSString *nstartDate  = [command.arguments objectAtIndex:8];
    NSString *nendDate    = [command.arguments objectAtIndex:9];
    
    NSTimeInterval _startInterval = [startDate doubleValue] / 1000; // strip millis
    NSDate *myStartDate = [NSDate dateWithTimeIntervalSince1970:_startInterval];
    
    NSTimeInterval _endInterval = [endDate doubleValue] / 1000; // strip millis
    NSDate *myEndDate = [NSDate dateWithTimeIntervalSince1970:_endInterval];
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    [df setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    
    // Find matches
    NSArray *matchingEvents = [self findEKEventsWithTitle:title location:location message:message startDate:myStartDate endDate:myEndDate calendar:calendar];
    
    if (matchingEvents.count == 1) {
        // Presume we have to have an exact match to modify it!
        // Need to load this event from an EKEventStore so we can edit it
        EKEvent *theEvent = [self.eventStore eventWithIdentifier:((EKEvent*)[matchingEvents lastObject]).eventIdentifier];
        if (ntitle) {
            theEvent.title = ntitle;
        }
        if (nlocation) {
            theEvent.location = nlocation;
        }
        if (nmessage) {
            theEvent.notes = nmessage;
        }
        if (nstartDate) {
            NSTimeInterval _nstartInterval = [nstartDate doubleValue] / 1000; // strip millis
            theEvent.startDate = [NSDate dateWithTimeIntervalSince1970:_nstartInterval];
        }
        if (nendDate) {
            NSTimeInterval _nendInterval = [nendDate doubleValue] / 1000; // strip millis
            theEvent.endDate = [NSDate dateWithTimeIntervalSince1970:_nendInterval];
        }
        
        // Now save the new details back to the store
        NSError *error = nil;
        [self.eventStore saveEvent:theEvent span:EKSpanThisEvent error:&error];
        
        // Check error code + return result
        if (error) {
            CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
            [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
        } else {
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
        }
    } else {
        // Otherwise return a no result error
        CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
        [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
    }
}
    

- (void)deleteEventFromCalendar:(CDVInvokedUrlCommand*)command
                       calendar: (EKCalendar *) calendar {
    
    NSString *callbackId = command.callbackId;
    
    NSString* title      = [command.arguments objectAtIndex:0];
    NSString* location   = [command.arguments objectAtIndex:1];
    NSString* message    = [command.arguments objectAtIndex:2];
    NSString *startDate  = [command.arguments objectAtIndex:3];
    NSString *endDate    = [command.arguments objectAtIndex:4];
    
    NSTimeInterval _startInterval = [startDate doubleValue] / 1000; // strip millis
    NSDate *myStartDate = [NSDate dateWithTimeIntervalSince1970:_startInterval];
    
    NSTimeInterval _endInterval = [endDate doubleValue] / 1000; // strip millis
    NSDate *myEndDate = [NSDate dateWithTimeIntervalSince1970:_endInterval];
    
    NSArray *matchingEvents = [self findEKEventsWithTitle:title location:location message:message startDate:myStartDate endDate:myEndDate calendar:calendar];
    
    NSError *error = NULL;
    for (EKEvent * event in matchingEvents) {
        [self.eventStore removeEvent:event span:EKSpanThisEvent error:&error];
    }
    
    if (error) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
        [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
    } else {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[pluginResult toSuccessCallbackString:callbackId]];
    }
}
    
-(NSArray*)findEKEventsWithTitle: (NSString *)title
                        location: (NSString *)location
                         message: (NSString *)message
                       startDate: (NSDate *)startDate
                         endDate: (NSDate *)endDate
                        calendar: (EKCalendar *) calendar {
    
    // Build up a predicateString - this means we only query a parameter if we actually had a value in it
    NSMutableString *predicateString= [[NSMutableString alloc] initWithString:@""];
    if (title.length > 0) {
        [predicateString appendString:[NSString stringWithFormat:@"title == '%@'" , title]];
    }
    if (location.length > 0) {
        [predicateString appendString:[NSString stringWithFormat:@" AND location == '%@'" , location]];
    }
    if (message.length > 0) {
        [predicateString appendString:[NSString stringWithFormat:@" AND notes == '%@'" , message]];
    }
    
    NSPredicate *matches = [NSPredicate predicateWithFormat:predicateString];
    
    NSArray *calendarArray = [NSArray arrayWithObject:calendar];
    
    NSArray *datedEvents = [self.eventStore eventsMatchingPredicate:[eventStore predicateForEventsWithStartDate:startDate endDate:endDate calendars:calendarArray]];
    
    NSArray *matchingEvents = [datedEvents filteredArrayUsingPredicate:matches];
    
    return matchingEvents;
}
    
-(EKCalendar*)findEKCalendar: (NSString *)calendarName {
    for (EKCalendar *thisCalendar in self.eventStore.calendars){
        NSLog(@"Calendar: %@", thisCalendar.title);
        if ([thisCalendar.title isEqualToString:calendarName]) {
            return thisCalendar;
        }
    }
    NSLog(@"No match found for calendar with name: %@", calendarName);
    return nil;
}
    
-(EKSource*)findEKSource {
    // if iCloud is on, it hides the local calendars, so check for iCloud first
    for (EKSource *source in self.eventStore.sources) {
        if (source.sourceType == EKSourceTypeCalDAV && [source.title isEqualToString:@"iCloud"]) {
            return source;
        }
    }
    
    // ok, not found.. so it's a local calendar
    for (EKSource *source in self.eventStore.sources) {
        if (source.sourceType == EKSourceTypeLocal) {
            return source;
        }
    }
    return nil;
}
    
#pragma mark Cordova functions
    
- (void)createEventInNamedCalendar:(CDVInvokedUrlCommand*)command {
    NSString* calendarName = [command.arguments objectAtIndex:5];
    EKCalendar* calendar = [self findEKCalendar:calendarName];
    if (calendar == nil) {
        NSString *callbackId = command.callbackId;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Could not find calendar"];
        [self writeJavascript:[result toErrorCallbackString:callbackId]];
    } else {
        [self createEventWithCalendar:command calendar:calendar];
    }
}


- (void)createEvent:(CDVInvokedUrlCommand*)command {
    EKCalendar* calendar = self.eventStore.defaultCalendarForNewEvents;
    [self createEventWithCalendar:command calendar:calendar];
}


-(void)deleteEventFromNamedCalendar:(CDVInvokedUrlCommand*)command {
    NSString* calendarName = [command.arguments objectAtIndex:5];
    EKCalendar* calendar = [self findEKCalendar:calendarName];
    if (calendar == nil) {
        NSString *callbackId = command.callbackId;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Could not find calendar"];
        [self writeJavascript:[result toErrorCallbackString:callbackId]];
    } else {
        [self deleteEventFromCalendar:command calendar:calendar];
    }
}


-(void)deleteEvent:(CDVInvokedUrlCommand*)command {
    EKCalendar* calendar = self.eventStore.defaultCalendarForNewEvents;
    [self deleteEventFromCalendar:command calendar: calendar];
}


-(void)modifyEventInNamedCalendar:(CDVInvokedUrlCommand*)command {
    NSString* calendarName = [command.arguments objectAtIndex:10];
    EKCalendar* calendar = [self findEKCalendar:calendarName];
    if (calendar == nil) {
        NSString *callbackId = command.callbackId;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Could not find calendar"];
        [self writeJavascript:[result toErrorCallbackString:callbackId]];
    } else {
        [self modifyEventWithCalendar:command calendar:calendar];
    }
}


-(void)modifyEvent:(CDVInvokedUrlCommand*)command {
    EKCalendar* calendar = self.eventStore.defaultCalendarForNewEvents;
    [self modifyEventWithCalendar:command calendar: calendar];
}


-(void)findAllEventsInNamedCalendar:(CDVInvokedUrlCommand*)command {
    NSString *callbackId = command.callbackId;
    NSString* calendarName = [command.arguments objectAtIndex:0];
    EKCalendar* calendar = [self findEKCalendar:calendarName];
    if (calendar == nil) {
        NSString *callbackId = command.callbackId;
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Could not find calendar"];
        [self writeJavascript:[result toErrorCallbackString:callbackId]];
    } else {
        NSDate* endDate =  [NSDate dateWithTimeIntervalSinceNow:[[NSDate distantFuture] timeIntervalSinceReferenceDate]];
        NSArray *calendarArray = [NSArray arrayWithObject:calendar];
        NSPredicate *fetchCalendarEvents = [eventStore predicateForEventsWithStartDate:[NSDate date] endDate:endDate calendars:calendarArray];
        NSArray *matchingEvents = [eventStore eventsMatchingPredicate:fetchCalendarEvents];
        
        NSMutableArray *finalResults = [[NSMutableArray alloc] initWithCapacity:matchingEvents.count];
        
        NSDateFormatter *df = [[NSDateFormatter alloc] init];
        [df setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        
        // Stringify the results - Cordova can't deal with Obj-C objects
        for (EKEvent * event in matchingEvents) {
            NSMutableDictionary *entry = [[NSMutableDictionary alloc] initWithObjectsAndKeys:
                                          event.title, @"title",
                                          event.location, @"location",
                                          event.notes, @"message",
                                          [df stringFromDate:event.startDate], @"startDate",
                                          [df stringFromDate:event.endDate], @"endDate", nil];
            [finalResults addObject:entry];
        }
        
        CDVPluginResult* result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsArray:finalResults];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}


-(void)findEvent:(CDVInvokedUrlCommand*)command {
    NSString *callbackId = command.callbackId;
    
    NSString* title      = [command.arguments objectAtIndex:0];
    NSString* location   = [command.arguments objectAtIndex:1];
    NSString* message    = [command.arguments objectAtIndex:2];
    NSString *startDate  = [command.arguments objectAtIndex:3];
    NSString *endDate    = [command.arguments objectAtIndex:4];
    
    NSTimeInterval _startInterval = [startDate doubleValue] / 1000; // strip millis
    NSDate *myStartDate = [NSDate dateWithTimeIntervalSince1970:_startInterval];
    
    NSTimeInterval _endInterval = [endDate doubleValue] / 1000; // strip millis
    NSDate *myEndDate = [NSDate dateWithTimeIntervalSince1970:_endInterval];
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    [df setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    
    NSArray *matchingEvents = [self findEKEventsWithTitle:title location:location message:message startDate:myStartDate endDate:myEndDate calendar:self.eventStore.defaultCalendarForNewEvents];
    
    NSMutableArray *finalResults = [[NSMutableArray alloc] initWithCapacity:matchingEvents.count];
    
    // Stringify the results - Cordova can't deal with Obj-C objects
    for (EKEvent * event in matchingEvents) {
        NSMutableDictionary *entry = [[NSMutableDictionary alloc] initWithObjectsAndKeys:
                                      event.title, @"title",
                                      event.location, @"location",
                                      event.notes, @"message",
                                      [df stringFromDate:event.startDate], @"startDate",
                                      [df stringFromDate:event.endDate], @"endDate", nil];
        [finalResults addObject:entry];
    }
    
    CDVPluginResult* result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsArray:finalResults];
    [self writeJavascript:[result toSuccessCallbackString:callbackId]];
}

    
-(void)createCalendar:(CDVInvokedUrlCommand*)command {
    NSString *callbackId = command.callbackId;
    NSString* calendarName = [command.arguments objectAtIndex:0];
    
    EKCalendar *cal = [self findEKCalendar:calendarName];
    if (cal == nil) {
        cal = [EKCalendar calendarWithEventStore:self.eventStore];
        cal.title = calendarName;
        cal.source = [self findEKSource];
        [self.eventStore saveCalendar:cal commit:YES error:nil];
        NSLog(@"created calendar: %@", cal.title);
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    } else {
        // ok, it already exists
        CDVPluginResult* result = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString:@"OK, Calendar already exists"];
        [self writeJavascript:[result toSuccessCallbackString:callbackId]];
    }
}
    
    
-(void)deleteCalendar:(CDVInvokedUrlCommand*)command {
    NSString *callbackId = command.callbackId;
    NSString* calendarName = [command.arguments objectAtIndex:0];
    
    EKCalendar *thisCalendar = [self findEKCalendar:calendarName];
    
    if (thisCalendar == nil) {
        CDVPluginResult * pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
        [self writeJavascript:[pluginResult toErrorCallbackString:callbackId]];
    } else {
        NSError *error;
        [eventStore removeCalendar:thisCalendar commit:YES error:&error];
        if (error) {
            NSLog(@"Error in deleteCalendar: %@", error.localizedDescription);
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.userInfo.description];
            [self writeJavascript:[result toErrorCallbackString:callbackId]];
        } else {
            NSLog(@"Deleted calendar: %@", thisCalendar.title);
            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self writeJavascript:[result toSuccessCallbackString:callbackId]];
        }
    }
}
    
@end