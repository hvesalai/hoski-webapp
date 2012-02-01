package fi.hoski.web.auth;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import static fi.hoski.web.auth.UserDirectory.EMAIL;

/**
 * An authorization filter that authorizes access based on the
 * existence of a HttpSession and a user object in it.
 */
public class AuthFilter implements Filter {
  public void init(FilterConfig filterConfig) { }
  public void destroy() { }

  public void doFilter(ServletRequest request, ServletResponse response, 
                       FilterChain chain)
      throws ServletException, IOException {

    if (request instanceof HttpServletRequest) {
      HttpServletRequest req = (HttpServletRequest) request;
      HttpServletResponse res = (HttpServletResponse) response;
      HttpSession session = req.getSession(false);

      // 1. check session and user exist
      @SuppressWarnings("unchecked")
      final Map<String,Object> user = (session != null) ? 
        (Map<String,Object>) session.getAttribute(LoginServlet.USER) : null;

      if (user == null) {
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      } else {
        // 2. create request wrapper
        ServletRequest wrapper = new HttpServletRequestWrapper(req) {
            public String getRemoteUser() {
              Object email = user.get(EMAIL);
              return (email != null) ? email.toString() : null;
            }
          };
        chain.doFilter(wrapper, res);   
      }
    } else {
      throw new ServletException("Unknown request type");
    }
  }
}
