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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.Integer;

import com.google.sps.data.Comment;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns comments on portfolio */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {
  private int commentLimit = 25; //default set to 25
  private final static String contentType = "application/json;charset=utf-8";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String parameterName = "number";
    String number = request.getParameter(parameterName);

    if (number != null) {
      this.commentLimit = Integer.parseInt(number);
    }

    response.setContentType(contentType);

    List<Comment> comments = new ArrayList<>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Query query = new Query("Comment").addSort("date", SortDirection.DESCENDING);

    PreparedQuery preparedQuery = datastore.prepare(query);
    List<Entity> results = preparedQuery.asList(FetchOptions.Builder.withLimit(this.commentLimit));
    
    for (Entity entity : results) {
      String entityName = (String) entity.getProperty("name");
      String entityText = (String) entity.getProperty("text");
      Long entityID = (Long) entity.getProperty("id");
      Date date = (Date) entity.getProperty("date");

      Comment databaseComment = new Comment(entityName, entityText, entityID, date);

      comments.add(databaseComment);
    }
    Gson myGson = new Gson();
    String json = myGson.toJson(comments);

    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(contentType);
    String name = getParameter(request, "name", "Anonymous");
    String comment = getParameter(request, "comment", "");
    Date createDate = new Date();

    if (comment == "" || comment == null) {
      throw new IOException("Comment is blank");
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("text", comment);
    commentEntity.setProperty("date", createDate);

    datastore.put(commentEntity);

    response.sendRedirect("/");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
