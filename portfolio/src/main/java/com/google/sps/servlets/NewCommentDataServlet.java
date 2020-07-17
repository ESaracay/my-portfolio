
package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.lang.Thread;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Runs when the form for a new comment is submitted. The comment
 * is then stored permanently with DatastoreService.
 */
@WebServlet("/new-comment")
public class NewCommentDataServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String user = request.getParameter("user");
    String content = request.getParameter("content");
    UserService myUser = UserServiceFactory.getUserService();
    String email = myUser.getCurrentUser().getEmail();

    // Block chat spammers.
    if (!verified(user, content)) {
      response.sendRedirect("chat.html");
      return;
    }

    long timeStamp = System.currentTimeMillis();
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("MMM d, HH:MM a");
    String displayTime = formatter.format(date);

    Entity myEntity = new Entity("Task");
    myEntity.setProperty("user", user);
    myEntity.setProperty("content", content);
    myEntity.setProperty("timeStamp", timeStamp);
    myEntity.setProperty("displayTime", displayTime);
    myEntity.setProperty("email", email);

    DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
    dataStore.put(myEntity);

    // Redirect causes html to refresh with new comments
    try {
      // If sleep is removed then page will sometimes display deleted comments
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      System.out.println("Sleep Interrupted");
    } finally {
      response.sendRedirect("/chat.html");
    }
  }

  private boolean verified(String user, String content) {
    return !user.isEmpty() && !content.isEmpty();
  }
}
