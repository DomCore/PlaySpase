package com.example.templates;

import java.io.IOException;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.Mail;
import org.springframework.stereotype.Component;

@Component
public class EMail {

public boolean sendmail(String emailTo, String contentText, String subject) {
  Email from = new Email("playspase.ru@gmail.com");
  Email to = new Email(emailTo);
  Content content = new Content("text/plain", contentText);
  Mail mail = new Mail(from, subject, to, content);

  SendGrid sg = new SendGrid("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");
  Request request = new Request();
  try {
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());
    sg.api(request);
    return true;
  } catch (IOException ex) {
    return false;
  }
}

}