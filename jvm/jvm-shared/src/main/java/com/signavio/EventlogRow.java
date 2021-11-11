package com.signavio;

import java.time.OffsetDateTime;

public class EventlogRow {
	
	String caseId;
	String eventName;
	OffsetDateTime timestamp;
	
	public EventlogRow(String caseId, String eventName, OffsetDateTime timestamp) {
		this.caseId = caseId;
		this.eventName = eventName;
		this.timestamp = timestamp;
	}
}
