package com.hms.identity.security.event;

import com.hms.identity.audit.enums.AuditAction;

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
