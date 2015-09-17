/*
 * Autopsy Forensic Browser
 *
 * Copyright 2013-15 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.timeline.datamodel;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import javax.annotation.concurrent.Immutable;
import org.joda.time.Interval;
import org.sleuthkit.autopsy.timeline.datamodel.eventtype.EventType;
import org.sleuthkit.autopsy.timeline.utils.IntervalUtils;
import org.sleuthkit.autopsy.timeline.zooming.DescriptionLOD;

/**
 * Represents a set of other (TimeLineEvent) events aggregated together. All the
 * sub events should have the same type and matching descriptions at the
 * designated 'zoom level'.
 */
@Immutable
public class AggregateEvent {

    /**
     * the smallest time interval containing all the aggregated events
     */
    final private Interval span;

    /**
     * the type of all the aggregted events
     */
    final private EventType type;

    /**
     * the common description of all the aggregated events
     */
    final private String description;

    /**
     * the description level of detail that the events were aggregated at.
     */
    private final DescriptionLOD lod;

    /**
     * the set of ids of the aggregated events
     */
    final private Set<Long> eventIDs;

    /**
     * the ids of the subset of aggregated events that have at least one tag
     * applied to them
     */
    private final Set<Long> tagged;

    /**
     * the ids of the subset of aggregated events that have at least one hash
     * set hit
     */
    private final Set<Long> hashHits;

    public AggregateEvent(Interval spanningInterval, EventType type, Set<Long> eventIDs, Set<Long> hashHits, Set<Long> tagged, String description, DescriptionLOD lod) {

        this.span = spanningInterval;
        this.type = type;
        this.hashHits = hashHits;
        this.tagged = tagged;
        this.description = description;
        this.eventIDs = eventIDs;
        this.lod = lod;
    }

    /**
     * @return the actual interval from the first event to the last event
     */
    public Interval getSpan() {
        return span;
    }

    public Set<Long> getEventIDs() {
        return Collections.unmodifiableSet(eventIDs);
    }

    public Set<Long> getEventIDsWithHashHits() {
        return Collections.unmodifiableSet(hashHits);
    }

    public Set<Long> getEventIDsWithTags() {
        return Collections.unmodifiableSet(tagged);
    }

    public String getDescription() {
        return description;
    }

    public EventType getType() {
        return type;
    }

    public DescriptionLOD getLOD() {
        return lod;
    }

    /**
     * merge two aggregate events into one new aggregate event.
     *
     * @param aggEvent1
     * @param aggEVent2
     *
     * @return a new aggregate event that is the result of merging the given
     *         events
     */
    public static AggregateEvent merge(AggregateEvent aggEvent1, AggregateEvent ag2) {

        if (aggEvent1.getType() != ag2.getType()) {
            throw new IllegalArgumentException("aggregate events are not compatible they have different types");
        }

        if (!aggEvent1.getDescription().equals(ag2.getDescription())) {
            throw new IllegalArgumentException("aggregate events are not compatible they have different descriptions");
        }
        Sets.SetView<Long> idsUnion = Sets.union(aggEvent1.getEventIDs(), ag2.getEventIDs());
        Sets.SetView<Long> hashHitsUnion = Sets.union(aggEvent1.getEventIDsWithHashHits(), ag2.getEventIDsWithHashHits());
        Sets.SetView<Long> taggedUnion = Sets.union(aggEvent1.getEventIDsWithTags(), ag2.getEventIDsWithTags());

        return new AggregateEvent(IntervalUtils.span(aggEvent1.span, ag2.span), aggEvent1.getType(), idsUnion, hashHitsUnion, taggedUnion, aggEvent1.getDescription(), aggEvent1.lod);
    }
}