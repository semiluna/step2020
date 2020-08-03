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

let user = null;

function loader() {
  getComments();
  toggleCommentForm();
}

function showInfo(number) {
  let elementId = "info-" + number;
  let infoDiv = document.getElementById(elementId);
  let buttonDiv = document.getElementById("div-" + number);

  infoDiv.style.display = "block";
  buttonDiv.style.display = "none";
}

const hideables = document.querySelectorAll(".info-container-hideable");

hideables.forEach(hideable => {
  const h3 = hideable.querySelector('h3');
  const ul = hideable.querySelector('ul');
  h3.addEventListener('click', () => ul.classList.toggle("hidden"));
});

function validateComment(name, text) {
  const regex = "^[a-zA-Z0-9]+([_.-]?[a-zA-Z0-9])*$";

  if (user == null) {
    alert("You must sign in to submit a comment.");
    return false;
  }
  
  if (!name.match(regex)) {
    alert("Invalid name");
    return false;
  }

  if (name.length < 4) {
    alert("Name too short");
    return false;
  }

  if (name.length > 20) {
    alert("Name too long");
    return false;
  }

  if (!text.replace(/\s/g, '').length) {
    alert("Comment can't be blank");
    return false;
  }

  return true;
}

function encodeParameters(key, value) {
  return encodeURIComponent(key) + "=" + encodeURIComponent(value);
}

function sendComment() {
  const name = document.getElementById("name").value;
  const text = document.getElementById("comment").value;

  if (validateComment(name, text) === true) {
    const request = new XMLHttpRequest();
    const id_token = user.tokenId;

    const formBody = [];
    formBody.push(encodeParameters("name", name));
    formBody.push(encodeParameters("text", text));
    formBody.push(encodeParameters("id_token", id_token));
    const requestData = formBody.join("&");

    request.open("POST", "/comments", true);
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.onload = function() {
      window.location = "/";
    }
    request.send(requestData);
    
  }
}

async function getComments() {
  let formNumber = document.getElementById("number-comments").value;

  const response = await fetch(`/comments?number=${formNumber}`);
  const text = await response.text();
  const comments = JSON.parse(text);

  const commentsContainer = document.getElementById("comments-container");
  
  while (commentsContainer.hasChildNodes()) {
    commentsContainer.removeChild(commentsContainer.firstChild);
  }

  const auxContainer = document.createElement('div');

  comments.forEach((comment) => {
    const commentDiv = createCommentNode(comment);
    auxContainer.appendChild(commentDiv);
  });

  commentsContainer.appendChild(auxContainer);
}

/*create name header and text div in html for comment */
function createCommentNode(comment) {
  const comDiv = document.createElement("div");
  comDiv.classList.add("comment");

  const h4 = document.createElement("h4");
  h4.classList.add("comment-name-date");
  const nameNode = document.createTextNode(comment.name + " " + (comment.email !== undefined ? comment.email : ""));
  h4.appendChild(nameNode);
  comDiv.appendChild(h4);

  const dateNode = document.createTextNode(comment.date !== undefined ? comment.date : "");
  const dateElement = document.createElement('p');
  dateElement.classList.add("comment-name-date");
  dateElement.appendChild(dateNode);
  comDiv.appendChild(dateElement);
  
  if (comment.score !== undefined && comment.score !== 2) {
    const scoreNode = document.createTextNode(`Your comment is a more ${comment.score >= 0.0 ? "positive" : "negative"} one. The score is: ${comment.score}.`);
    const scoreElement = document.createElement("p");
    scoreElement.classList.add("comment-name-date");
    scoreElement.appendChild(scoreNode);
    comDiv.appendChild(scoreElement);
  }

  //create text paragraph
  const txt = document.createElement("p");
  const textNode = document.createTextNode(comment.text);
  txt.appendChild(textNode);
  comDiv.appendChild(txt);

  return comDiv;
}

function onSignIn(googleUser) {
  const profile = googleUser.getBasicProfile();
  const id_token = googleUser.getAuthResponse().id_token;
  
  user = { 
          name: profile.getName(),
          imageUrl: profile.getImageUrl(),
          email: profile.getEmail(),
          tokenId: id_token,
  }
  toggleCommentForm();
}

function toggleCommentForm() {
  const commentForm = document.getElementById("form");
  const signinInfo = document.getElementById("signin-info");
  const signOutButton = document.getElementById("sign-out");

  if (user == null) {
    commentForm.classList.add("hidden");
    signinInfo.classList.remove("hidden");
    signOutButton.classList.add("hidden");
  } else {
    commentForm.classList.remove("hidden");
    signinInfo.classList.add("hidden");
    signOutButton.classList.remove("hidden");
  }
}

function onSignOut() {
  user = null;

  let auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(function () {
    console.log('User signed out.');
    toggleCommentForm();
  });
}