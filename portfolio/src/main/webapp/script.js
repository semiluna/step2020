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

window.onload = function() {
  let i = 1;
  while (document.getElementById("h-" + i) !== null) {
    let elementId = document.getElementById("h-" + i);

    elementId.addEventListener("click", function() {
      let number = this.id[this.id.length - 1];
      let childId = document.getElementById("more-" + number);
      childDisplay = childId.style.display;
      if (childDisplay !== "block") {
        childId.style.display = "block";
      } else {
        childId.style.display = "none";
      }
    });

    i++;
  }
}