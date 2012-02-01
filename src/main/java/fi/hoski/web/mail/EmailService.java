package fi.hoski.web.mail;

import java.io.IOException;
import javax.servlet.UnavailableException;

public interface EmailService {
  /**
   * Sends an e-mail message.
   *
   * @param to recipient address
   * @param from sender address
   * @param subject message subject
   * @param body message body
   * @throws UnavailableException if the e-mail service cannot be used.
   * @throws IllegalArgumentException if bad arguments were given
   */
  void send(String to, String from, String subject, String body) 
      throws UnavailableException, IllegalArgumentException;
}