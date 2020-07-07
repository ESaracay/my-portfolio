package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

@WebServlet("/chat-login")
public class ChatLoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    UserService myUser = UserServiceFactory.getUserService();
    if(!myUser.isUserLoggedIn()){
        String afterLoginURL = "/chat.html";
        String loginURL = myUser.createLoginURL(afterLoginURL);
        response.getWriter().println("<p>Please click the URL below to Login</p>");
        response.getWriter().println("<p>Login<a href=\"" + loginURL + "\"> here</a></p>");
    }else{
        //Printing out the user's email so chat can grab this information later to update UI
        String email = myUser.getCurrentUser().getEmail();
        String logoutURL = myUser.createLogoutURL("/index.html");
        String json = convertToJson(email, logoutURL);
        response.getWriter().println(json);
        response.getWriter().println("<p>SignOut<a href=\"" + logoutURL + "\"> here</a></p>");
       // response.sendRedirect("/chat.html");
    }
  }

  public class User{
      private String email, logoutURL;
  }

  private String convertToJson(String email, String logoutURL) {
    Gson myGson = new Gson();
    User myUser = new User();
    myUser.email = email;
    myUser.logoutURL = logoutURL;
    String myJson = myGson.toJson(myUser);
    return myJson;
  }

}