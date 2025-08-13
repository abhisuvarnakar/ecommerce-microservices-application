package com.cs.ecommerce.notificationservice.service;

public interface EmailSenderService {

    void sendEmail(String toMail, String subject, String body);
}
