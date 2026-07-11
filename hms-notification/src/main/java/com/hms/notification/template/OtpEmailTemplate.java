package com.hms.notification.template;

import org.springframework.stereotype.Component;

@Component
public class OtpEmailTemplate {

    public String build(

            String otp,

            long expiryMinutes) {

        return """
                Your verification code is:

                %s

                This code expires in %d minutes.

                If you did not request this code,
                please ignore this email.
                """
                .formatted(
                        otp,
                        expiryMinutes);

    }

}
