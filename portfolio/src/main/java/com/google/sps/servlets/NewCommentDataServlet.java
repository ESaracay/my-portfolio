
package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
//Data Store
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/** 
* This servlet runs when the form for a new comment is submitted. The comment
* is then stored permenantly with DatastoreService. 
*/
@WebServlet("/new-comment")
public class NewCommentDataServlet extends HttpServlet {
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
      String user = request.getParameter("user");
      String content = request.getParameter("content");

      //This should help guard the chat from being spammed
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

      DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
      dataStore.put(myEntity);

      // redirect to same page so that the page refreshes with new comment
      response.sendRedirect("/chat.html");
  }
  
  private boolean verified(String user, String content){

      if (user.equals("Enter Name") || content.equals("Enter Comment")) {
          return false;
      }
      return true;
  }

}
