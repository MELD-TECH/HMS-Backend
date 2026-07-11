package com.hms.notification.provider;

public interface SmsProvider {

    void send(

            String phoneNumber,

            String message);

}