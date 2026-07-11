package com.hms.notification.mfa.service;

import org.springframework.stereotype.Service;

import com.hms.notification.provider.EmailProvider;

@Service
public class ConsoleEmailProvider
implements EmailProvider {

    @Override
    public void send(

            String recipient,

            String subject,

            String body) {

        System.out.println("========== EMAIL ==========");

        System.out.println("To : " + recipient);

        System.out.println("Subject : " + subject);

        System.out.println(body);

        System.out.println("===========================");
    }

}