/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synyx.urlaubsverwaltung.service;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import org.synyx.urlaubsverwaltung.domain.Antrag;
import org.synyx.urlaubsverwaltung.domain.Person;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * @author  johannes
 */
public class MailServiceImpl implements MailService {

    // see here: http://static.springsource.org/spring/docs/2.0.5/reference/mail.html

    private static final String SCHLUSSZEILE = "\n\nUrlaubsverwaltung";

    private static Logger mailLogger = Logger.getLogger("mailLogger");

    private JavaMailSender mailSender;
    private String absender = EmailAdr.MANAGE.getEmail();
    private String sternchen = EmailAdr.STERN.getEmail();

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender) {

        this.mailSender = mailSender;
    }

    /**
     * @see  MailService#sendDecayNotification()
     */
    @Override
    public void sendDecayNotification(List<Person> persons) {

        for (final Person person : persons) {
            MimeMessagePreparator prep = new MimeMessagePreparator() {

                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {

                    mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(person.getEmail()));
                    mimeMessage.setFrom(new InternetAddress(absender));
                    mimeMessage.setSubject("Erinnerung Resturlaub");
                    mimeMessage.setText("Liebe/-r " + person.getFirstName() + " " + person.getLastName() + ","
                        + "\n\ndu hast aus dem letzten Kalenderjahr Resturlaub ins neue Jahr mitgenommen."
                        + "\nBitte beachte, dass dieser zum 1. April des neuen Jahres verfällt."
                        + "\nNimm' dir also rechtzeitig Urlaub, damit der Resturlaub nicht verfällt." + SCHLUSSZEILE);
                }
            };

            try {
                this.mailSender.send(prep);
            } catch (MailException ex) {
                mailLogger.error(ex.getMessage());
            }
        }
    }


    /**
     * @see  MailService#sendNewRequestsNotification(java.util.List, java.util.List)
     */
    @Override
    public void sendNewRequestsNotification(List<Person> persons, List<Antrag> requests) {

        // nehme StringBuilder, statt String immer wieder neu anzuhaengen
        // StringBuilder haengt String Ketten an, am Ende wird ein ganzer String daraus erzeugt
        StringBuilder build = new StringBuilder();

        for (Antrag antrag : requests) {
            build.append("\n").append(antrag.getPerson().getFirstName()).append(" ").append(antrag.getPerson()
                .getLastName()).append(" : ").append(antrag.getStartDate()).append(" bis ").append(antrag.getEndDate());
        }

        final String beantragungen = build.toString();

        build = new StringBuilder();

        for (Person beauftragter : persons) {
            build.append(beauftragter.getEmail()).append(", ");
        }

        final String emailAdressen = build.toString();

        MimeMessagePreparator prep = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailAdressen));
                mimeMessage.setFrom(new InternetAddress(absender));
                mimeMessage.setSubject("Es liegen neue Urlaubsanträge vor");
                mimeMessage.setText("Hallo Chef-Etage, "
                    + "\n\nes liegen neue Urlaubsanträge vor, die es zu bearbeiten gilt: "
                    + "\n" + beantragungen + SCHLUSSZEILE);
            }
        };

        try {
            this.mailSender.send(prep);
        } catch (MailException ex) {
            mailLogger.error(ex.getMessage());
        }
    }


    /**
     * @see  MailService#sendApprovedNotification(org.synyx.urlaubsverwaltung.domain.Person, org.synyx.urlaubsverwaltung.domain.Antrag)
     */
    @Override
    public void sendApprovedNotification(final Person person, final Antrag request) {

        // Email ans Office: es liegt ein neuer Antrag vor
        MimeMessagePreparator prepOffice = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress("office@synyx.de"));
                mimeMessage.setFrom(new InternetAddress(absender));
                mimeMessage.setSubject("Neuer bewilligter Antrag");
                mimeMessage.setText("Hallo Office, "
                    + "\n\nes liegt ein neuer bewilligter Antrag vor." + SCHLUSSZEILE);
            }
        };

        try {
            this.mailSender.send(prepOffice);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
        }

        MimeMessagePreparator prepUser = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(person.getEmail()));
                mimeMessage.setFrom(new InternetAddress(absender));
                mimeMessage.setSubject("Antrag bewilligt");
                mimeMessage.setText("Hallo " + person.getFirstName() + " " + person.getLastName() + ","
                    + "\n\ndein Antrag auf Urlaub für den Zeitraum von " + request.getStartDate() + " bis "
                    + request.getEndDate()
                    + " wurde bewilligt." + SCHLUSSZEILE);
            }
        };

        try {
            this.mailSender.send(prepUser);
        } catch (MailException ex) {
            mailLogger.error(ex.getMessage());
        }
    }


    /**
     * @see  MailService#sendDeclinedNotification(org.synyx.urlaubsverwaltung.domain.Antrag)
     */
    @Override
    public void sendDeclinedNotification(final Antrag request) {

        final Person person = request.getPerson();

        MimeMessagePreparator prep = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(person.getEmail()));
                mimeMessage.setFrom(new InternetAddress(absender));
                mimeMessage.setSubject("Antrag abgelehnt");
                mimeMessage.setText("Hallo " + person.getFirstName() + " " + person.getLastName() + ","
                    + "\n\ndein Antrag für Urlaub im Zeitraum vom " + request.getStartDate() + " bis "
                    + request.getEndDate() + " wurde von " + request.getBoss().getFirstName() + " "
                    + request.getBoss().getLastName() + " leider abgelehnt mit folgender Begründung: "
                    + "\n" + request.getReasonToDecline() + SCHLUSSZEILE);
            }
        };

        try {
            this.mailSender.send(prep);
        } catch (MailException ex) {
            mailLogger.error(ex.getMessage());
        }
    }


    /**
     * @see  MailService#sendConfirmation(org.synyx.urlaubsverwaltung.domain.Antrag)
     */
    @Override
    public void sendConfirmation(Antrag request) {

        final Person person = request.getPerson();

        MimeMessagePreparator prep = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(person.getEmail()));
                mimeMessage.setFrom(new InternetAddress(absender));
                mimeMessage.setSubject("Bestätigung Antragstellung");
                mimeMessage.setText("Hallo " + person.getFirstName() + " " + person.getLastName() + ","
                    + "\n\ndein Antrag wurde erfolgreich gestellt und wird in Kürze durch einen der Chefs bearbeitet werden."
                    + SCHLUSSZEILE);
            }
        };

        try {
            this.mailSender.send(prep);
        } catch (MailException ex) {
            mailLogger.error(ex.getMessage());
        }
    }


    /**
     * NOT YET IMPLEMENTED
     *
     * @see  MailService#sendBalance(java.lang.Object)
     */
    @Override
    public void sendBalance(Object balanceObject) {

        throw new UnsupportedOperationException("Not supported yet.");
    }


    /**
     * @see  MailService#sendWeeklyVacationForecast(java.util.List)
     */
    @Override
    public void sendWeeklyVacationForecast(List<Person> urlauber) {

        List<String> names = new ArrayList<String>();

        for (Person person : urlauber) {
            names.add("\n" + person.getFirstName() + " " + person.getLastName());
        }

        final String urlaub = names.toString();

        MimeMessagePreparator prep = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(sternchen));
                mimeMessage.setFrom(new InternetAddress(absender));
                mimeMessage.setSubject("Diese Woche im Urlaub");
                mimeMessage.setText("Hallo Sternchen, "
                    + "\n\nfolgende Mitarbeiter haben diese Woche frei: "
                    + "\n\n" + urlaub + "\n\n" + SCHLUSSZEILE);
            }
        };

        try {
            this.mailSender.send(prep);
        } catch (MailException ex) {
            mailLogger.error(ex.getMessage());
        }
    }


    /**
     * @see  MailService#sendCanceledNotification(org.synyx.urlaubsverwaltung.domain.Antrag, java.lang.String)
     */
    @Override
    public void sendCanceledNotification(Antrag request, final String emailAddress) {

        final String name = request.getPerson().getFirstName() + " " + request.getPerson().getLastName();

        MimeMessagePreparator prep = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
                mimeMessage.setFrom(new InternetAddress(absender));
                mimeMessage.setSubject("Antrag storniert");
                mimeMessage.setText("Der Mitarbeiter " + name + " hat seinen Urlaubsantrag storniert.");
            }
        };

        try {
            this.mailSender.send(prep);
        } catch (MailException ex) {
            mailLogger.error(ex.getMessage());
        }
    }
}