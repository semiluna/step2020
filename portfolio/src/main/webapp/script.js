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
  var elementId = "info-" + number;
  console.log(elementId);
  var infoDiv = document.getElementById(elementId);
  var buttonDiv = document.getElementById("div-" + number);

  console.log("Button has been pressed. Display of element is " + infoDiv.style.display);
  
  if (infoDiv.style.display === "none") {
    infoDiv.style.display = "block";
    buttonDiv.style.display = "none";
  }
}

function showGreeting() {
  var greetings = ["Hello!", "Buna ziua!", "Buenos Dias!", "Buongiorno!", "Bonjour!", "こんにちは!", "Χαίρετε!", "Dzień dobry!"];
  var greetElement = document.getElementById("greeting");

  var x = Math.floor(Math.random() * 10) % 8;
  greetElement.innerText = greetings[x];
}

window.onload = function() {
  
  /*
  function showFavourites(elementId) {
    console.log("Show element with ID " + elementId);
    var infoDiv = document.getElementById(elementId);
    infoDiv.style.display = "block";
  }

  function hideFavourites(elementId) {
    console.log("Hide element with ID " + elementId);
    var infoDiv = document.getElementById(elementId);
    infoDiv.style.display = "none";
  }*/

  for (var i = 1; i <= 3; i++) {
    var elementId = document.getElementById("scroll-" + i);

    elementId.addEventListener("mouseenter", function(event) {
      console.log("Enter the Event Listener");

      var number = event.target.id[event.target.id.length - 1];
      var childId = document.getElementById("more-" + number);
      childId.style.display = "block";
    });
    elementId.addEventListener("mouseleave", function(event) {
      var number = event.target.id[event.target.id.length - 1];
      var childId = document.getElementById("more-" + number);
      childId.style.display = "none";
    });
  }
}