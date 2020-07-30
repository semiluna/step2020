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
import java.util.Collections;
import java.lang.Integer;

import java.lang.Exception;
import java.lang.NullPointerException;
import java.security.GeneralSecurityException;

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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

 
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;

/** Servlet that returns comments on portfolio */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {
  private int commentLimit = 25; //default set to 25
  private final static String contentType = "application/json;charset=utf-8";
  private static final String CLIENT_ID = "764206484277-nfgom1kis6ltt2k6lbmgmb0e5hhe90o5.apps.googleusercontent.com";
  private static final String key = "AIzaSyD95ipnFZlWNGHT_ii5eiQ_ayIdgwVF1UI";
  private static final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
      .Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
      .setAudience(Collections.singletonList(CLIENT_ID))
      .build();

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
      String entityName = "Anonymous";
      String entityText = "";
      Date date = null;
      String email = "";
      Double score = (Double) 2.0;
      
      if (entity.hasProperty("name")) {
        entityName = (String) entity.getProperty("name");
      }

      if (entity.hasProperty("text")) {
        entityText = (String) entity.getProperty("text");
      }

      if (entity.hasProperty("date")) {
        date = (Date) entity.getProperty("date");
      }
      
      if (entity.hasProperty("email")) {
        email = (String) entity.getProperty("email");
      }
      
      if (entity.hasProperty("score")) {
        score = (Double) entity.getProperty("score");
      }

      Comment databaseComment = Comment.create(entityName, entityText, date, email, score);

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
    String comment = getParameter(request, "text", "");
    Date createDate = new Date();

    try {
      if (comment == null || comment == "") {
        throw new IOException("Comment is blank");
      }
      
      String idTokenString = request.getParameter("id_token");
      if (idTokenString == null) {
        throw new GeneralSecurityException("User must be signed in.");
      }
      
      GoogleIdToken idToken = validate(idTokenString);

      Payload payload = idToken.getPayload();
        
      String userId = payload.getSubject();
      String email = payload.getEmail();
      float commentScore = sentimentAnalysis(comment);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("name", name);
      commentEntity.setProperty("text", comment);
      commentEntity.setProperty("date", createDate);
      commentEntity.setProperty("email", email);
      commentEntity.setProperty("score", commentScore);

      datastore.put(commentEntity);
    } catch(NullPointerException error) {
      System.out.println("Null Pointer Exception: " + error);
    } catch(GeneralSecurityException error) {
      System.out.println("General security exception: " + error);
    } catch(IOException error) {
      System.out.println("IOException: " + error);
    } catch(Exception error) {
      System.out.println("Error: " + error);
    } finally {
      response.sendRedirect("/");
    }
  }


  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  private GoogleIdToken validate(String idTokenString) throws Exception {
    GoogleIdToken idToken = this.verifier.verify(idTokenString);
    if (idToken == null) {
      throw new GeneralSecurityException("User validation failed.");
    }
    return idToken;
  }

  private float sentimentAnalysis(String message) throws IOException {
    Document doc = Document
      .newBuilder()
      //.setProjectId("antana-step-2020")
      .setContent(message)
      .setType(Document.Type.PLAIN_TEXT)
      .build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    AnalyzeSentimentResponse response = languageService.analyzeSentiment(doc);
    Sentiment sentiment = response.getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();
    return score;
  } 
}