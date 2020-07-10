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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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

/** 
* Returns all comments to chat page in descending order of most recent upload.
*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  public class Comment {
    private String timeCreated, user, content, key;

    public Comment(String user, String content, String time, String key) {
      this.user = user;
      this.content = content;
      this.key = key;
      this.timeCreated = time;
    }
  }

  private int numCommentsDisplayed = 10;
  private final int defaultCommentsDisplayed = 10;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query myQuery = new Query("Task").addSort("timeStamp", SortDirection.DESCENDING);
    DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = dataStore.prepare(myQuery);
    List<Comment> commentList = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      String user = (String) entity.getProperty("user");
      String content = (String) entity.getProperty("content");
      String displayTime = (String) entity.getProperty("displayTime");
      String key = (String) entity.getProperty("key");

      Comment myComment = new Comment(user, content, displayTime, key);
      commentList.add(myComment);

      if (commentList.size() >= numCommentsDisplayed) {
        break;
      }
    }
    String myJson = convertToJson(commentList);
    response.setContentType("application/json;");
    response.getWriter().println(myJson);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    numCommentsDisplayed = setMax(request);
    response.sendRedirect("/chat.html");
  }

  private String convertToJson(List<Comment> commentList) {
    Gson myGson = new Gson();
    String myJson = myGson.toJson(commentList);
    return myJson;
  }

  private int setMax(HttpServletRequest request) {
    String mynum = request.getParameter("numComments");
    int num;
    try {
      num = Integer.parseInt(mynum);
    } catch (NumberFormatException e) {
      return defaultCommentsDisplayed;
    }

    return num;
  }
}
