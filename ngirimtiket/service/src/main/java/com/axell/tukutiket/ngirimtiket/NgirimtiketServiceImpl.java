package com.axell.tukutiket.ngirimtiket;

import com.axell.tukutiket.ngirimtiket.nggomlebu.NggomlebuProxy;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class NgirimtiketServiceImpl implements NgirimtiketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NgirimtiketServiceImpl.class);
    private static final String EMAIL_SUBJECT_PREFIX = "Ticket for ";
    private static final String ATTACHMENT_FILENAME_PREFIX = "Ticket.";

    @Value("${queue.email.sent}")
    private String emailSentEvent;
    @Value("${tukutiket.exchange}")
    private String tukutiketExchange;
    @Value("${spring.mail.username}")
    private String tukutiketEmail;
    @Value("${email.sendticket.template}")
    private String emailSendTicketTemplate;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private SimpleDateFormat simpleDateFormat;
    @Autowired
    private NggomlebuProxy nggomlebuProxy;

    @Override
    @RabbitListener(queues = "${queue.send.email}")
    @SendTo(value = "${queue.email.sent}")
    public EmailSentResponse sendEmail(EmailRequest emailRequest) {
        try {
            Context context = populateEmailContext(emailRequest);
            String emailBody = templateEngine.process(emailSendTicketTemplate, context);
            MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                    messageHelper.setFrom(tukutiketEmail);
                    messageHelper.setTo(emailRequest.getEmailAddress());
                    messageHelper.setSubject(EMAIL_SUBJECT_PREFIX + emailRequest.getEventName());
                    messageHelper.setText(emailBody, true);

                    Resource qrCodeImageResource = nggomlebuProxy.downloadQRCodeImage(emailRequest.getQrCodeImageName());
                    ByteArrayResource urlResource = new ByteArrayResource(IOUtils.toByteArray(qrCodeImageResource.getInputStream()));
                    messageHelper.addAttachment(ATTACHMENT_FILENAME_PREFIX + getFileExtension(emailRequest.getQrCodeImageName()), urlResource, MediaType.IMAGE_PNG_VALUE);
                }
            };
            javaMailSender.send(messagePreparator);
            return EmailSentResponse.builder()
                    .orderId(emailRequest.getOrderId())
                    .build();
        } catch (MailException ex) {
            LOGGER.error("Error send email : " + ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
    }

    private Context populateEmailContext(EmailRequest emailRequest) {
        Context context = new Context();
        context.setVariable("username", emailRequest.getUsername());
        context.setVariable("eventName", emailRequest.getEventName());
        context.setVariable("eventVenue", emailRequest.getEventVenue());
        context.setVariable("eventDate", getFormattedDateEvent(emailRequest.getEventDate()));
        return context;
    }

    private String getFormattedDateEvent(Date eventDate) {
        return simpleDateFormat.format(eventDate);
    }

    private String getFileExtension(String filename) {
        String[] splittedFilename = filename.split("\\.");
        return splittedFilename[1];
    }
}
