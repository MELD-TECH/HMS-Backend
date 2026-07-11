package com.hms.notification.provider;

public interface EmailProvider {

    void send(

            String recipient,

            String subject,

            String body);

}