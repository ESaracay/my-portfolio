
package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
//Data Store
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
//Query
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/new-comment")
public class DataServlet extends HttpServlet {
  
  public class Comment {
      private String time, user, content;

      public Comment(String user,String content, String time) {
          this.user= user;
          this.content = content;
          this.time = time;
      }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Entity myEntity = new Entity("Task");
    
      String user = request.getParameter("user");
      String content = request.getParameter("content");
      long timeStamp = System.currentTimeMillis();
      Date date = new Date();
      SimpleDateFormat formatter = new SimpleDateFormat("MMM d, HH:MM a");
      String displayTime = formatter.format(date);

      myEntity.setProperty("user", user);
      myEntity.setProperty("content", content);
      myEntity.setProperty("timeStamp", timeStamp);
      myEntity.setProperty("displayTime", displayTime);

      DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
      dataStore.put(myEntity);

      // redirect to same page so that the page refreshes with new comment
      response.sendRedirect("/chat.html");
  }

  private String convertToJson(List<Comment> commentList) {
    Gson myGson = new Gson();
    String myJson = myGson.toJson(commentList);
    return myJson;
  }

  private int setMax(HttpServletRequest request){
      String mynum = request.getParameter("numComments");
      int num;
     // safeguards the program just in case the user does not enter a number
      try {
          num = Integer.parseInt(mynum);
      }catch(NumberFormatException e){
          return 1;
      }
      
      if(num <= 0 || num > 5){
          return 5;
      }
          
      return num;  
  }

}
