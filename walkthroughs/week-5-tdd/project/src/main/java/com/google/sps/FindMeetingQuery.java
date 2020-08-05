// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.sps.TimeRange;

public final class FindMeetingQuery {
  private final static int totalMinutes = 1440;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //remove events with no requested attendees
    Set<String> requested = new HashSet<>(request.getAttendees());
    Set<Event> eventsSet = new HashSet<>(events);

    Set<String> attendees = new HashSet<>(request.getAttendees());
    Set<String> optionalAttendees = new HashSet<>(request.getOptionalAttendees());

    List<TimeRange> mandatoryTimes = eventsSet
      .stream()
      .filter(event -> FindMeetingQuery.matchesAttendees(event, attendees))
      .map(event -> event.getWhen())
      .collect(Collectors.toList());

    List<TimeRange> optionalTimes = eventsSet
      .stream()
      .filter(event -> FindMeetingQuery.matchesAttendees(event, attendees, optionalAttendees))
      .map(event -> event.getWhen())
      .collect(Collectors.toList());

    System.out.println(mandatoryTimes);
    System.out.println(optionalTimes);

    //sort time ranges by start
    Collections.sort(mandatoryTimes, TimeRange.ORDER_BY_START);
    Collections.sort(optionalTimes, TimeRange.ORDER_BY_START);

    List<TimeRange> mandatoryFreeTimes = findFreeTimes(mandatoryTimes, request.getDuration());
    List<TimeRange> optionalFreeTimes = findFreeTimes(optionalTimes, request.getDuration());

    if (optionalFreeTimes.isEmpty()) {
      return mandatoryFreeTimes;
    }
    
    return optionalFreeTimes;
  }

  private static boolean matchesAttendees(Event event, Set<String> requestedAttendees) {
    Set<String> attendees = new HashSet<>(event.getAttendees());
    for (String person: requestedAttendees) {
      if (attendees.contains(person)) {
        return true;
      }
    }
    
    return false;
  }

  private static boolean matchesAttendees(Event event, Set<String> requestedAttendees, Set<String> optionalAttendees) {
    if (matchesAttendees(event, requestedAttendees)) {
      return true;
    }

    return matchesAttendees(event, optionalAttendees);
  }

  private static List<TimeRange> findFreeTimes(List<TimeRange> events, long meetingDuration) {
    //get free times
    List<TimeRange> freeTimes = new ArrayList();
    
    if (events.isEmpty() && (meetingDuration <= totalMinutes)) {
      freeTimes.add(TimeRange.WHOLE_DAY);
      return freeTimes;
    }

    if (events.isEmpty()) {
      return freeTimes;
    }

    TimeRange intersection = events.get(0);

    for (TimeRange range: events) {
      if (intersection.contains(range)) {
        continue;
      } else {
        if (intersection.overlaps(range)) {
          intersection = TimeRange.fromStartEnd(intersection.start(), range.end(), false);
        } else {
          //found free time
          TimeRange free = TimeRange.fromStartEnd(intersection.end(), range.start(), false);
          intersection = TimeRange.fromStartEnd(range.start(), range.end(), false);
          if (free.duration() >= meetingDuration) {
            freeTimes.add(free);
          }
        }
      }
    }

    //check for beginning and end
    if (TimeRange.START_OF_DAY < events.get(0).start()) {
      if (events.get(0).start() >= meetingDuration) {
        freeTimes.add(TimeRange.fromStartEnd(0, events.get(0).start(), false));
      }
    }

    if (intersection.end() < TimeRange.END_OF_DAY) {
      int duration = TimeRange.END_OF_DAY - intersection.end();
      if (duration >= meetingDuration) {
        freeTimes.add(TimeRange.fromStartEnd(intersection.end(), TimeRange.END_OF_DAY, true));
      }
    }

    Collections.sort(freeTimes, TimeRange.ORDER_BY_START);

    return freeTimes;
  }
}
