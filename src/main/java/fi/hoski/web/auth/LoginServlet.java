package fi.hoski.web.auth;

import fi.hoski.web.google.DatastoreUserDirectory;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import org.json.JSONObject;
import org.json.JSONException;

import static fi.hoski.web.auth.UserDirectory.EMAIL;
import static fi.hoski.web.auth.UserDirectory.SECRET_FIELDS;

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
    
    String action = request.getParameter("action");

    if (action == null || action.equals("login")) {
      // login

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

          writeUserJSON(user, response);
        }
      }
    } else {
      // logout

      HttpSession session = request.getSession(false);
      if (session != null) {
        session.setAttribute(USER, null);
      }
      session.invalidate();
      
      writeUserJSON(null, response);
    }
  }

  @Override
  public void doGet(HttpServletRequest request, 
                    HttpServletResponse response) 
    throws ServletException, IOException {

    HttpSession session = request.getSession(false);
    String etag = request.getHeader("If-None-Match");
    @SuppressWarnings("unchecked")
    Map<String,Object> user = (session != null) ?
      (Map<String,Object>) session.getAttribute(USER) :
      null;
    String userEtag = getEtag(user);

    if (etag != null && etag.equals(userEtag)) {
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
    } else {
      response.setHeader("ETag", userEtag);
      response.setHeader("Cache-Control", "private, max-age=0, must-revalidate");
      
      writeUserJSON(user, response);
    }
  }

  private void writeUserJSON(Map<String,Object> user, 
                             HttpServletResponse response) 
      throws ServletException, IOException {

    try {
      response.setContentType("application/json"); 
      response.setCharacterEncoding("UTF-8");
      
      JSONObject json = new JSONObject();
      json.put("user", getUserJSON(user));
      json.write(response.getWriter());
    } catch (JSONException e) {
      throw new ServletException("Could not serialize user object");
    }
  }

  private Object getUserJSON(Map<String,Object> user) 
      throws JSONException {
    if (user != null) {
      JSONObject userjson = new JSONObject();
      for (Map.Entry<String,Object> entry : user.entrySet()) {
        if (!SECRET_FIELDS.contains(entry.getKey())) {
          userjson.put(entry.getKey(), entry.getValue());
        }
      }
      return userjson;
    } else {
      return JSONObject.NULL;
    }
  }

  private String getEtag(Map<String,Object> user) {
    if (user == null) {
      return "\"null\"";
    } else {
      String tag = (String) user.get(EMAIL);
      tag = (tag != null) ? tag.replace('"', '_') : null;
      return '"' + tag + '"';
    }
  }
}