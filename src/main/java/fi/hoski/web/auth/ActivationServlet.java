package fi.hoski.web.auth;

import fi.hoski.web.mail.EmailService;
import fi.hoski.web.google.GoogleMailService;
import fi.hoski.web.google.DatastoreUserDirectory;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.text.MessageFormat;
import java.util.Random;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.ServletException;

import static fi.hoski.web.auth.UserDirectory.LATEST_ACTIVATION;

/**
 * ActivationServlet creates a password for the user and sends the
 * password to the user's e-mail address. The user must already exist
 * in the user directory.
 *
 * The ActivationServlet is designed to be used trough AJAX.
 */
public class ActivationServlet extends HttpServlet {
  private static final String PASSWORD_CHARS =
    "1234567890+!#%&/()=qwertyuiopasdfghjklzxcvbnm,.-QWERTYUIOPASDFGHJKLZXCVBNM;:_";
  private static final int ACTIVATION_INTERVAL_MIN = 60*60*1000; // 1h
  private static final int PASSWORD_LENGTH = 10;

  private Random random = new Random();

  UserDirectory userDirectory; 
  EmailService emailService;
  
  @Override
  public void init() {
    userDirectory = new DatastoreUserDirectory();
    emailService = new GoogleMailService();
  }

  @Override
  public void doPost(HttpServletRequest request, 
                     HttpServletResponse response) 
    throws ServletException, IOException {

    String email = request.getParameter("email");
    email = (email != null) ? email.trim() : null;
 
    // 1. check params
    if (email == null || email.isEmpty()) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                         "Missing parameter: email");
    } else {
      // 2. check user exists
      Map<String,Object> user = userDirectory.findUser(email);
      if (user == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN,
                           "Unknown user: " + email);
      } else {     
        // 3. check last activation
        Date latestActivation = (Date) user.get(LATEST_ACTIVATION);
        if (latestActivation != null && 
            latestActivation.after(new Date(System.currentTimeMillis() - 
                                            ACTIVATION_INTERVAL_MIN))) {
          response.sendError(HttpServletResponse.SC_FORBIDDEN,
                             "Too many activations");
        } else {
          // finally everything is ok and we can activate
          String password = randomPassword();
          try {
            userDirectory.setUserPassword(email, password);
            emailPassword(email, password, request.getLocale());
            response.getWriter().write("OK");
          } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                               "E-mail cannot be sent to: " + email);
          }
        }
      }
    }
  }

  private String randomPassword() {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < PASSWORD_LENGTH) {
      sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
    }
    return sb.toString();
  }

  private void emailPassword(String email, String password, Locale locale) 
    throws UnavailableException, IllegalArgumentException {
    ResourceBundle messages =
      ResourceBundle.getBundle("fi.hoski.web.Messages", locale);

    String from = messages.getString("passwordFromAddress");
    String subject = messages.getString("passwordMessageSubject");
    String body = messages.getString("passwordMessageBody");

    emailService.send(email, from, subject,
                      MessageFormat.format(body, password));
  }
}