package com.hms.events.security;

import com.hms.audit.security.enums.AuditAction;

public class PermissionRemovedEvent extends SecurityEvent{

	public PermissionRemovedEvent(
		    String username,
		    String entityId) {

		super(username,"ROLE",entityId);
		}

		@Override
		public String action() {

		return AuditAction.PERMISSION_REMOVED.name();
		}

		@Override
		public String details() {

		return "Permission removed";
		}
}
