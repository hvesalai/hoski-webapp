package fi.hoski.web.auth;

import fi.hoski.web.google.DatastoreUserDirectory;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

/**
 * LoginServlet authenticates the user using e-mail and password as
 * the login credentials. The login servlet creates a servlet session 
 * where it stores the user object.
 *
 * The Login Servlet is designed to be used through AJAX. 
 */
public class LoginServlet extends HttpServlet {
  public static final String USER = "fi.hoski.web.user";

  UserDirectory userDirectory; 
  
  @Override
  public void init() {
    userDirectory = new DatastoreUserDirectory();
  }

  @Override
  public void doPost(HttpServletRequest request, 
                     HttpServletResponse response) 
    throws ServletException, IOException {

    String email = request.getParameter("email");
    String password = request.getParameter("password");
    email = (email != null) ? email.trim() : null;
 
    // 1. check params
    if (email == null || email.isEmpty() ||
        password == null || password.isEmpty()) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
    } else {
      // 2. check user exists
      Map<String,Object> user = userDirectory.authenticateUser(email, password);
      if (user == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
      } else {     
        // 3. create session
        HttpSession session = request.getSession(true);
        session.setAttribute(USER, user);
        response.getWriter().write("OK");
      }
    }
  }
}