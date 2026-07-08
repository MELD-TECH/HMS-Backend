package com.hms.identity.security.event;

import com.hms.identity.audit.enums.AuditAction;

public class RoleRemovedEvent extends SecurityEvent {

	public RoleRemovedEvent(
		    String username,
		    String entityId) {

		super(username,"USER",entityId);
		}

		@Override
		public String action() {

		return AuditAction.ROLE_REMOVED.name();
		}

		@Override
		public String details() {

		return "Role removed";
		}
}
