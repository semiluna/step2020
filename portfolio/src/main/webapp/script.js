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
  
  const escapedComments = comments.map((comment) => {
    const escapedName = convertHTML(comment.name);
    const escapedText = convertHTML(comment.text);
    return {name: escapedName, text: escapedText, date: comment.date};
  });

  const formattedComments = escapedComments.map((comment) => {
    return `<div class="comment">
              <h4 class="comment-name">
                ${comment.name} ${comment.date !== undefined ? "on " + comment.date : ""}
              </h4>
              <p>${comment.text}</p>
            </div>`
  });

  const htmlElement = formattedComments.join('');

  document.getElementById("comments-container").innerHTML = htmlElement;
}

function convertHTML(str) {
  let regex = /[&|<|>|"|']/g;
  let htmlString = str.replace(regex, function(match){
    if(match === "&"){
      return "&amp;";
    }else if(match === "<"){
      return "&lt;"
    }else if(match === ">"){
      return "&gt;";
    }else if(match === '"'){
      return "&quot;";
    }else{
      return "&apos;";
    }
  });
      
  return htmlString;
}