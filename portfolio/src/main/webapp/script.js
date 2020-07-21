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

async function getGreeting() {
    const response = await fetch("/data");
    const quote = await response.text();
    console.log(quote);
    document.getElementById("quote-container").innerHTML = quote;
}

//add hard-coded elements to the page
function getComments() {
  const comments = "<ul><li>Antonia said: Hope you like my portfolio!</li> \
              <li>Alex said: I like the color palette.</li> \
              <li>Jo said: What's your favourite pizza? </li> </ul>";  
  document.getElementById("comments-container").innerHTML = comments;
}