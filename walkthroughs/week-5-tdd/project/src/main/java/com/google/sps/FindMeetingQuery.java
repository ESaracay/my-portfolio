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

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.Boolean;
import java.util.HashSet;
import java.util.HashMap;

public final class FindMeetingQuery {

  public Collection<TimeRange> calendarQuery(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> availableTimes = new ArrayList<TimeRange>();
    ArrayList<Event> listEvents = new ArrayList<>(events);
    HashSet<String> mandatoryAttendees = new HashSet<>(request.getAttendees());
    ArrayList<Event> sortedListEvents = mySort(listEvents, mandatoryAttendees);

    int rangeStart = 0;
    TimeRange previousSlot = null;
    for(Event event: sortedListEvents) {
      TimeRange eventTimeRange = event.getWhen();
      // checks for overlapping events
      if(previousSlot == null || !eventTimeRange.overlaps(previousSlot)){
        availableTimes.add(TimeRange.fromStartEnd(rangeStart, eventTimeRange.start(), false));
        rangeStart = eventTimeRange.end();
        previousSlot = eventTimeRange;
      }else if(previousSlot != null && previousSlot.contains(eventTimeRange)) {
        //checks for nested events
        rangeStart = previousSlot.end();
      } else {
        rangeStart = eventTimeRange.end();
        previousSlot = eventTimeRange;
      }
    }
     
    /* if the last event is not at the end of the day then arraylist adds
    * the timerange from the end of last event to the end of the day. */
    if (rangeStart != 60 * 24){
      availableTimes.add(TimeRange.fromStartEnd(rangeStart, 60 * 24, false));
    }

    // gets rid of short time slots
    for(int i = 0; i < availableTimes.size(); i++ ) {
      if (availableTimes.get(i).duration() < request.getDuration()) {
        availableTimes.remove(i);
        i--;
      }
    }

    ArrayList<TimeRange> optionalTimes = new ArrayList<TimeRange>();
    HashSet<String> optionalAttendees = new HashSet<>(request.getOptionalAttendees());
    if(optionalAttendees.size() > 0) {
      optionalTimes = optimizeOptionalAttendees(availableTimes, request, optionalAttendees, events);
    }

    //returns timeranges with optional attendess if any exist
    return (optionalTimes.size() == 0) ? availableTimes : optionalTimes;
  }

  /*
  * Sorts events by start time and filters out events that mandatoryAttendees are not apart of.
  */
  public ArrayList<Event> mySort(ArrayList<Event> list, HashSet<String> mandatoryAttendees) {
    ArrayList<Event> newList = new ArrayList<>();
    while(list.size() > 0) {
      int indexMin = 0;
      for(int i = 1; i < list.size(); i++) {
        int newTime = list.get(i).getWhen().start(); 
        if(list.get(indexMin).getWhen().start() > newTime) {
          indexMin = i;
        }
      }
      Event closestEvent = list.get(indexMin);
      list.remove(indexMin);
      // Checks if a mandatoryAttendee is attending member the event
      HashSet<String> attendees = new HashSet<>(closestEvent.getAttendees());
      attendees.retainAll(mandatoryAttendees);
      if(attendees.size() <= 0) {
        continue;
      } else {
        newList.add(closestEvent);
      }
    }

    return newList;
    }

    /*
    * Returns an ArrayList of timeranges within the available times where the most 
    * optional attendees can attend.
    */
    public ArrayList<TimeRange> optimizeOptionalAttendees(List<TimeRange> availableRanges,
        MeetingRequest request, HashSet<String> optionalAttendees, Collection<Event> events) {

      ArrayList<TimeRange> possibleOptionalTimes = new ArrayList<>();
      for(String member: optionalAttendees){
        MeetingRequest myRequest = new MeetingRequest(Arrays.asList(member), request.getDuration());
        ArrayList<TimeRange> availability = new ArrayList<>(calendarQuery(events, myRequest));
        if(availability.size() > 0) {
          possibleOptionalTimes.addAll(availability);
        }
      }
      //Cycles through times that work for mandatory attendees
      int maxNumOpt = 0;
      HashMap<TimeRange, Integer> optionalSlots = new HashMap<TimeRange, Integer>();
      for(TimeRange freeTimeRange: availableRanges) {
        int bucketStart = freeTimeRange.start();
        int endOfSlot = freeTimeRange.end() - 1;
        int previousNumOpt = 0;
        System.out.println(freeTimeRange);
        for(int i = freeTimeRange.start(); i < freeTimeRange.end(); i++ ) {
          TimeRange currentSlot = TimeRange.fromStartEnd(i, i + 1, false);
          int updatedNumOpt = checkOptionalsAvailable(possibleOptionalTimes, currentSlot);

          //if number of optionals changes then we want to record time range in hashmap
          if (previousNumOpt != updatedNumOpt || i == endOfSlot) {
            //Check if TimeRange is actually big enough for meeting
            TimeRange possibleSlot = TimeRange.fromStartEnd(bucketStart, i, true);
            bucketStart = i;
            if (possibleSlot.duration() >= request.getDuration()) {
              optionalSlots.put(possibleSlot, previousNumOpt);
              if(previousNumOpt > maxNumOpt) {
                maxNumOpt = previousNumOpt;
              }
            }
          } 
          previousNumOpt = updatedNumOpt;
        }
      }

      //adds timeRanges of the observed max number of optionals to ArrayList
      ArrayList<TimeRange> maxOptionalTimeRanges = new ArrayList<>();
      for(TimeRange range: optionalSlots.keySet()) {
        if (optionalSlots.get(range) == maxNumOpt) {
          maxOptionalTimeRanges.add(range);
        }
      }

    return maxOptionalTimeRanges;     
    }

    /* 
    * Counts how many optional people are free during the given timeRange.
    */
    public int checkOptionalsAvailable(ArrayList<TimeRange> timeSlots, TimeRange currentSlot) {
        int counter = 0;
        for (TimeRange freeSlot: timeSlots) {
            if(freeSlot.contains(currentSlot)) {
                counter++;
            }
        }
        return counter;
    }

  }