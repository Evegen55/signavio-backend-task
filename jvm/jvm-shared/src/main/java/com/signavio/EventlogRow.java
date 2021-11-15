package com.signavio;

import java.time.OffsetDateTime;
import java.util.Objects;

public class EventlogRow {

	String caseId;
	String eventName;
	OffsetDateTime timestamp;

	public EventlogRow(String caseId, String eventName, OffsetDateTime timestamp) {
		this.caseId = caseId;
		this.eventName = eventName;
		this.timestamp = timestamp;
	}

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EventlogRow that = (EventlogRow) o;
        return Objects.equals(caseId, that.caseId) && Objects.equals(eventName, that.eventName) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseId, eventName, timestamp);
    }
}
