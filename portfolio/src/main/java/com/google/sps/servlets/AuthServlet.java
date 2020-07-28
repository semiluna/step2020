package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collections;
import java.lang.Exception;
import java.lang.Integer;
import java.security.GeneralSecurityException;

import com.google.sps.data.User;
import com.google.gson.Gson;

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

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
  private static final String CLIENT_ID = "764206484277-nfgom1kis6ltt2k6lbmgmb0e5hhe90o5.apps.googleusercontent.com";
  private final static String contentType = "application/json;charset=utf-8";

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(contentType);
    String idTokenString = request.getParameter("id_token");
    
    try {
      JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      NetHttpTransport transport = new NetHttpTransport();
      
      GoogleIdToken idToken = validate(idTokenString, jsonFactory, transport);

      if (idToken != null) {
        Payload payload = idToken.getPayload();
        
        String userId = payload.getSubject();
        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");

        User user = new User(userId, email, emailVerified, familyName, givenName);
        Gson gson = new Gson();
        String json = gson.toJson(user);

        response.getWriter().println(json);
      } else {
        response.getWriter().println((String) null);
      }
    } catch(Exception error) {
      response.getWriter().println(error);
    }
  }

  private GoogleIdToken validate(String idTokenString, JacksonFactory jsonFactory, NetHttpTransport transport) throws Exception {
      GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(Collections.singletonList(CLIENT_ID))
        .build();
      
      GoogleIdToken idToken = verifier.verify(idTokenString);
      return idToken;
  }
}

