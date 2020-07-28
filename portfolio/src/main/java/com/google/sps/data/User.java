package com.google.sps.data;

public class User {
  private String userId;
  private String email;
  private boolean emailVerified;
  private String familyName;
  private String givenName;

  public User(String userId, String email, boolean emailVerified, String familyName, String givenName) {
    this.userId = userId;
    this.email = email;
    this.emailVerified = emailVerified;
    this.familyName = familyName;
    this.givenName = givenName;
  }

  String getEmail() {
    return this.email;
  }

  boolean getEmailVerified() {
    return this.emailVerified; 
  }

  String getFamilyName() {
    return this.familyName;
  }

  String getGivenName() {
    return this.givenName;
  }
}