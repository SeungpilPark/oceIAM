package org.opencloudengine.garuda.web.contactus;

import org.opencloudengine.garuda.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class ContactUsServiceImpl implements ContactUsService {

    @Autowired
    @Qualifier("config")
    private Properties config;

    @Autowired
    private ContactUsRepository repository;

    @Autowired
    private MailService mailService;

    @Override
    public void sendContactMail(ContactUs contactUs) {
        repository.insert(contactUs);
        String toUser = config.getProperty("mail.contacts.address");
        mailService.sendBySmtp(contactUs.getSubject(), contactUs.getMessage(), contactUs.getEmail(), contactUs.getName(), toUser, contactUs.getTelephone(), null);
    }
}
