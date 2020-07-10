package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.lang.Thread;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/chat-login")
public class ChatLoginServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService myUser = UserServiceFactory.getUserService();
    if (!myUser.isUserLoggedIn()) {
      String afterLoginURL = "/chat.html";
      String loginURL = myUser.createLoginURL(afterLoginURL);
      String loginJson = convertToJson("none", null, loginURL);
      response.setContentType("application/json;");
      response.getWriter().println(loginJson);
    } else {
      String email = myUser.getCurrentUser().getEmail();
      String logout = myUser.createLogoutURL("/index.html");
      String logoutJson = convertToJson(email, logout, null);
      response.setContentType("application/json;");
      response.getWriter().println(logoutJson);
    }
  }

  public class User { private String email, logoutURL, loginURL; }

  private String convertToJson(String email, String logoutURL, String loginURL) {
    Gson myGson = new Gson();
    User myUser = new User();
    myUser.email = email;
    myUser.logoutURL = logoutURL;
    myUser.loginURL = loginURL;
    String myJson = myGson.toJson(myUser);
    return myJson;
  }
}