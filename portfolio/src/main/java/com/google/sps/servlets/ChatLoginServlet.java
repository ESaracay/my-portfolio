package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.lang.Thread;

@WebServlet("/chat-login")
public class ChatLoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService myUser = UserServiceFactory.getUserService();
    if (!myUser.isUserLoggedIn()) {
        String afterLoginURL = "/chat.html";
        String loginURL = myUser.createLoginURL(afterLoginURL);
        response.sendRedirect(loginURL);
    } else {
        response.sendRedirect("/chat.html");
    }
  }

    @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService myUser = UserServiceFactory.getUserService();
    String email = myUser.getCurrentUser().getEmail();
    String logout = myUser.createLogoutURL("/index.html");
    String json = convertToJson(email, logout);
    response.setContentType("application/json;");
    response.getWriter().println(json);
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