/*

 Include the following snippets into the HTML.
 
 <!-- adjust the paths -->
 <script type="text/javascript" src="jquery.js" />
 <script type="text/javascript" src="hoskilogin.js" />
 <style type="text/css">
   #hoskilogin, #hoskilogout, #hoskiloginerrors, #hoskiuser { display: none; }
 </style>

 <div>
   <form method="post" action="/loginservlet" id="hoskilogin">
     <label>e-mail: <input type="text" name="email" /></label>
     <label>password: <input type="text" name="password" /></label>
     <input type="submit" name="submit" value="Log in"/>
     <div id="hoskiloginerrors">The username or password was not correct.</div>
     <a href="activation.html">No account? Click here</a>
   </form>
   <form method="post" action="/loginservlet?action=logout" id="hoskilogout">
     <span id="hoskiuser">logged in user</span>
     <input type="submit" name="submit" value="Log out" />
   </form>
 </div>

 The mark-up can be freely modified within the following limits:
 - two forms with ids login and logout must exist
 - login form must have the specified action, as well as email and 
   password input fields and a submit button
 - logout form must have the specified action and a submit button
 - logout form must have the span with id hoskiuser, whose text
   will be replaced with the name of the logged in user
*/

$(function () {
  var loginform = $("#hoskilogin");
  var logoutform = $("#hoskilogout");
  var hoskiuser = $("#hoskiuser");
  var hoskiloginerrors = $("#hoskiloginerrors");
  var loginstatusurl = loginform.attr("action");
  
  function updateStatus() {
    loginform.hide();
    logoutform.hide();
    hoskiuser.hide();
    hoskiloginerrors.hide();

    $.getJSON(loginstatusurl, function(data) {
      if (data.user) {
        // TODO: use user name instead of email
        hoskiuser.text(data.user.email).show();
        logoutform.show().submit(ajaxLogout);
      } else {
        loginform.show().submit(ajaxLogin);
      }
    });
  }
  
  function ajaxLogin() {
    hoskiloginerrors.hide();
    $.post(loginform.attr("action"), loginform.serialize())
      .done(function() {
        updateStatus();
      })
      .fail(function(jqXHR, textStatus, errorThrown) {
        // TODO: check that textStatus provides good messages
        hoskiloginerrors.show().text(textStatus);
      });
  }

  function ajaxLogout() {
    $.post(logoutform.attr("action"))
      .always(function() {
        updateStatus();
      });
  }
});

