package com.hms.identity.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record ResendAuthenticationOtpRequest(

	    @NotBlank
	    String challengeToken

	) {}
