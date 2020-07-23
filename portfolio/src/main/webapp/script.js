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

async function getComments() {
  const response = await fetch("/data");
  const text = await response.text();
  const comments = JSON.parse(text);

  document.getElementById("loading-text").remove();
  
  const fatherDiv = document.getElementById("comments-container");

  comments.forEach((comment) => {
    //create div component for comment
    const comDiv = document.createElement('div');
    comDiv.classList.add("comment");

    //create name header
    const h4 = document.createElement('h4');
    h4.classList.add('comment-name-date');
    const node1 = document.createTextNode(`${comment.name}`);
    h4.appendChild(node1);
    comDiv.appendChild(h4);

    //create date header
    const dateNode = document.createTextNode(`${comment.date !== undefined ? comment.date : ""}`);
    const dateElement = document.createElement('p');
    dateElement.classList.add("comment-name-date");
    dateElement.appendChild(dateNode);
    comDiv.appendChild(dateElement);

    //create text paragraph
    const txt = document.createElement('p');
    const node2 = document.createTextNode(`${comment.text}`);
    txt.appendChild(node2);
    comDiv.appendChild(txt);

    fatherDiv.appendChild(comDiv);
  });
}