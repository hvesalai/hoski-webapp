package fi.hoski.web.google;

import fi.hoski.web.mail.EmailService;
import java.io.IOException;
import javax.servlet.UnavailableException;

public class GoogleMailService implements EmailService {
  public void send(String to, String from, String subject, String body) 
      throws UnavailableException, IllegalArgumentException {

    // TODO: use com.google.appengine.api.mail to send the message
    throw new UnsupportedOperationException("Not implemented");
  }
}