package com.hms.notification.provider;

import org.springframework.stereotype.Service;

@Service
public class ConsoleSmsProvider
        implements SmsProvider {

    @Override
    public void send(

            String phoneNumber,

            String message) {

        System.out.println();

        System.out.println("========== SMS ==========");

        System.out.println("To : " + phoneNumber);

        System.out.println(message);

        System.out.println("=========================");

    }

}