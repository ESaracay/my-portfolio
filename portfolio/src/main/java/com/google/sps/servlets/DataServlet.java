// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  public class Comment {
    private String time, user, content;

    public Comment(String user, String content) {
      Date date = new Date();
      SimpleDateFormat formatter = new SimpleDateFormat("MMM d, HH:MM a");
      this.user = user;
      this.content = content;
      this.time = formatter.format(date);
    }
  }

  private List<Comment> text;

  @Override
  public void init() {
    text = new ArrayList<>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String my_json = convertToJson(text);
    response.setContentType("application/json;");
    response.getWriter().println(my_json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Entity myEntity = new Entity("Task");

    String user = request.getParameter("user");
    String content = request.getParameter("content");
    long time_stamp = System.currentTimeMillis();

    myEntity.setProperty("user", user);
    myEntity.setProperty("content", content);
    myEntity.setProperty("time_stamp", time_stamp);

    DatastoreService data_store = DatastoreServiceFactory.getDatastoreService();
    data_store.put(myEntity);

    // redirect to same page so that the page refreshes with new comment
    // response.sendRedirect("/chat.html");
  }

  private String convertToJson(List<Comment> text) {
    Gson my_gson = new Gson();
    String my_json = my_gson.toJson(text);
    return my_json;
  }
}
