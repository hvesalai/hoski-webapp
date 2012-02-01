package fi.hoski.web.google;

import fi.hoski.web.auth.UserDirectory;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.UnavailableException;

public class DatastoreUserDirectory implements UserDirectory {

  public Map<String,Object> findUser(String email) throws UnavailableException {
    // TODO: implement, return userEntity.getProperties()
    throw new UnsupportedOperationException("Not implemented");
  }
    
  /**
   * Sets the user's password. Also updates the user's
   * LATEST_ACTIVATION field to the value of new Date();
   */
  public void setUserPassword(String email, String password)
      throws UnavailableException {
    byte[] passwordDigest = digest(password);
    Date latestActivation = new Date();
    // TODO: implement, remember to set LATEST_ACTIVATION = latestActivation
    throw new UnsupportedOperationException("Not implemented");
  }

  public Map<String,Object> authenticateUser(String email, String password)
      throws UnavailableException {
    byte[] passwordDigest = digest(password);
    Map<String,Object> user = findUser(email);
    if (user != null) {
      byte[] userPasswordDigest = (byte[]) user.get(PASSWORD);
      if (userPasswordDigest != null && 
          Arrays.equals(userPasswordDigest, passwordDigest)) {
        return user;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  private byte[] digest(String password) throws UnavailableException {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      md.update("fi.hoski".getBytes()); // a grain of salt     
      return md.digest(password.getBytes());
    } catch (NoSuchAlgorithmException e) {
      throw new UnavailableException("Requirements failed");
    }
  }

}