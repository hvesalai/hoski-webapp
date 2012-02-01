package fi.hoski.web.auth;

import java.util.Map;
import javax.servlet.UnavailableException;

public interface UserDirectory {
  
  /**
   * Key of the latest activation.
   *
   * Latest activation is the last time the user was activated in the
   * form of java.util.Date object. Used to restrict the number of
   * activations per timespan.
   */
  public static final String LATEST_ACTIVATION = "latestActivation";
  /**
   * Key of the user's password digest.
   */
  public static final String PASSWORD = "password";
  /**
   * Key of the user's e-mail address.
   */
  public static final String EMAIL = "email";

  /**
   * Finds a user using e-mail address.
   *
   * @param email the e-mail address of the user
   * @return the user data as a key-value map or null if the user
   * was not found.
   */
  Map<String,Object> findUser(String email) throws UnavailableException;

  /**
   * Finds and authenticates the user using e-mail address and password.
   *
   * @param email the e-mail address of the user
   * @param password of the user (in clear text)
   * @return the user data as a key-value map or null if the user
   * was not found or the password incorrect.
   */
  Map<String,Object> authenticateUser(String email, String password) throws UnavailableException;
    
  /**
   * Sets the user's password. Also updates the user's
   * LATEST_ACTIVATION field to the value of new Date();
   */
  void setUserPassword(String email, String password) throws UnavailableException;

}