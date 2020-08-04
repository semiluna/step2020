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
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.sps.TimeRange;

public final class FindMeetingQuery {
  private final static int totalMinutes = 1440;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //remove events with no requested attendees
    HashSet<String> requested = new HashSet<>(request.getAttendees());
    HashSet<Event> eventsSet = new HashSet<>(events);

    ArrayList<TimeRange> mandatoryTimes = eventsSet
      .stream()
      .filter(event -> this.checkAttendees(event, request, false) == true)
      .map(event -> event.getWhen())
      .collect(Collectors.toCollection(ArrayList::new));

    ArrayList<TimeRange> optionalTimes = eventsSet
      .stream()
      .filter(event -> this.checkAttendees(event, request, true) == true)
      .map(event -> event.getWhen())
      .collect(Collectors.toCollection(ArrayList::new));

    System.out.println(mandatoryTimes);
    System.out.println(optionalTimes);

    //sort time ranges by start
    Collections.sort(mandatoryTimes, TimeRange.ORDER_BY_START);
    Collections.sort(optionalTimes, TimeRange.ORDER_BY_START);

    ArrayList<TimeRange> mandatoryFreeTimes = findFreeTimes(mandatoryTimes, request);
    ArrayList<TimeRange> optionalFreeTimes = findFreeTimes(optionalTimes, request);

    if (optionalFreeTimes.isEmpty()) {
      return mandatoryFreeTimes;
    }
    
    return optionalFreeTimes;
  }

  private static boolean checkAttendees(Event event, MeetingRequest request, boolean checkOptional) {
    HashSet<String> attendees = new HashSet<>(event.getAttendees());
    HashSet<String> requested = new HashSet<>(request.getAttendees());
    HashSet<String> optional = new HashSet<>(request.getOptionalAttendees());

    for (String person: requested) {
      if (attendees.contains(person)) {
        return true;
      }
    }
    
    if (checkOptional) {
      for (String person: optional) {
        if (attendees.contains(person))
          return true;
      }
    }

    return false;
  }

  private static ArrayList<TimeRange> findFreeTimes(ArrayList<TimeRange> times, MeetingRequest request) {
    //get free times
    ArrayList<TimeRange> freeTimes = new ArrayList();
    
    if (times.isEmpty() && (request.getDuration() <= totalMinutes)) {
      freeTimes.add(TimeRange.WHOLE_DAY);
      return freeTimes;
    }

    if (times.isEmpty()) {
      return freeTimes;
    }

    TimeRange intersection = times.get(0);

    for (TimeRange range: times) {
      if (intersection.contains(range)) {
        continue;
      } else {
        if (intersection.overlaps(range)) {
          intersection = new TimeRange(intersection.start(), range.end() - intersection.start());
        } else {
          //found free time
          TimeRange free = new TimeRange(intersection.end(), range.start() - intersection.end());
          intersection = new TimeRange(range.start(), range.end() - range.start());
          if (free.duration() >= request.getDuration()) {
            freeTimes.add(free);
          }
        }
      }
    }

    //check for beginning and end
    if (TimeRange.START_OF_DAY < times.get(0).start()) {
      freeTimes.add(new TimeRange(0, times.get(0).start()));
    }

    if (intersection.end() < TimeRange.END_OF_DAY) {
      int end = intersection.end();
      freeTimes.add(new TimeRange(end, totalMinutes - end));
    }
    Collections.sort(freeTimes, TimeRange.ORDER_BY_START);

    return freeTimes;
  }
}
