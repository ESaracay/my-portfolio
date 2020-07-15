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

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> verifiedTimes = new ArrayList<TimeRange>();
    ArrayList<Event> listEvents = new ArrayList<>(events);
    HashSet<String> meetingGroup = new HashSet<>(request.getAttendees());
    ArrayList<Event> sortedListEvents = mySort(listEvents, meetingGroup);

    int slotStart = 0;
    TimeRange previousSlot = null;
    for(Event event: sortedListEvents) {
        TimeRange usedSlot = event.getWhen();
        // checking for overlapping events
        if(previousSlot == null || !usedSlot.overlaps(previousSlot)){
          verifiedTimes.add(TimeRange.fromStartEnd(slotStart, usedSlot.start(), false));
          slotStart = usedSlot.end();
          previousSlot = usedSlot;
        }else if(previousSlot != null && previousSlot.contains(usedSlot)) {
            //checking for nested events
            slotStart = previousSlot.end();
        } else {
            slotStart = usedSlot.end();
            previousSlot = usedSlot;
        }
    }
     
    // if the last event is not at the end of the day then we want to include
    // the timerange from end of last event to the end of the day. 
    if (slotStart != 60 * 24){
      verifiedTimes.add(TimeRange.fromStartEnd(slotStart, 60 * 24, false));
    }

    // Get rid of short time slots
    for(int i = 0; i < verifiedTimes.size(); i++ ) {
        if (verifiedTimes.get(i).duration() < request.getDuration()) {
          verifiedTimes.remove(i);
          i--;
        }
    }

    ArrayList<TimeRange> updatedTimes = new ArrayList<TimeRange>();
    HashSet<String> optionalGroup = new HashSet<>(request.getOptionalAttendees());
    if(optionalGroup.size() > 0) {
     updatedTimes = optimizeOptionalAttendees(verifiedTimes, request, optionalGroup, events);
    }
    //Have to change .size() == 0 in future
    return (updatedTimes.size() == 0) ? verifiedTimes : updatedTimes;
  }

  public ArrayList<Event> mySort(ArrayList<Event> list, HashSet<String> meetingGroup) {
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
          // Checks to make sure that a meetingGroup member is actually in this event
          HashSet<String> attendees = new HashSet<>(closestEvent.getAttendees());
          attendees.retainAll(meetingGroup);
          if(attendees.size() <= 0) {
              continue;
          } else {
              newList.add(closestEvent);
          }
      }
      return newList;
    }

    public ArrayList<TimeRange> optimizeOptionalAttendees(List<TimeRange> possibleRanges, MeetingRequest request, HashSet<String> optionalGroup, Collection<Event> events) {
        //What if I get the list of ranges that attendees are free by using recursion
        ArrayList<TimeRange> possibleOptionalTimes = new ArrayList<>();
        for(String member: optionalGroup){
            MeetingRequest myRequest = new MeetingRequest(Arrays.asList(member), request.getDuration());
            ArrayList<TimeRange> availability = new ArrayList<>(query(events, myRequest));
            if(availability.size() > 0) {
                possibleOptionalTimes.addAll(availability);
            }
        }
        //Here we cycle through the time ranges we know already work for mandatory people & keep track of max optionals seen
        int maxNumOpt = 0;
        ArrayList<TimeRange> maxOptionalSlots = new ArrayList<>();
        for(TimeRange verifiedTime: possibleRanges) {
            //we go minute by minute seeing how many people can attend
            int bucketStart = verifiedTime.start();
            int bucketEnd = bucketStart;
            int previousNumOpt = 0;
            for(int i = verifiedTime.start(); i < verifiedTime.end(); i++ ) {
                TimeRange currentSlot = TimeRange.fromStartEnd(i, i + 1, false);
                int updatedNumOpt = checkOptionalsAvailable(possibleOptionalTimes, currentSlot);

                //possiblity one: we switched into a higher num of optionals ( also maybe do or updatedTime > maxNumOpt)
                if (previousNumOpt != updatedNumOpt && previousNumOpt > maxNumOpt) {
                    System.out.println("Switch");
                    //Check if TimeRange is actually big enough
                    TimeRange possibleSlot = TimeRange.fromStartEnd(bucketStart, bucketEnd, false);
                    bucketStart = bucketEnd - 1;
                    if (possibleSlot.duration() >= request.getDuration()) {
                        //Clear out all other times that had less optionals
                        maxOptionalSlots.clear();
                        maxOptionalSlots.add(possibleSlot);
                        maxNumOpt = previousNumOpt;
                    }
                } else if (previousNumOpt != updatedNumOpt &&  previousNumOpt == maxNumOpt ){ //possibility two we end up with another one that switches but equal to our max
                TimeRange possibleSlot = TimeRange.fromStartEnd(bucketStart, bucketEnd, false);
                bucketStart = bucketEnd - 1;
                System.out.println("Switch2");
                    if (possibleSlot.duration() >= request.getDuration()) {
                        maxOptionalSlots.add(possibleSlot);
                    }
                } 
                bucketEnd = i + 1;
                previousNumOpt = updatedNumOpt;
            }
        }

        return maxOptionalSlots;
         
    }

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