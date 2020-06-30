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


/*This function picks a random movie from our list and displays
it when the user clicks the random movie generator button*/
function randomMovie() {
  const movies =
      [
        'Star Wars',
        'Indiana Jones',
        'Back to the Future',
        'Lord of the Rings',
        'Wall-E',
        'Robots',
        'Captain America',
        'Avengers Infinity War',
        'Avatar',
        'The Titanic',
        'Spider-Man: Into the Spider-Verse',
        'catch me if you can',
        'Forrest Gump',
        'The Aviator',
        'Inception',
        'Up',
        'Gladiator',
        'A Beautiful Mind',
        'Interstellar',
        'The Martian',
        'The Jungle Book'
      ]

      const movie = movies[Math.floor(Math.random() * movies.length)];

  const container = document.getElementById('movie');

  container.innerText = '"' + movie + '"';
}

async function grabComments(){
    const comments = await fetch('/data').then(response => response.json());
    console.log(comments);
    for (var i = 0; i < comments.length; i++) {
        console.log("User:"+comments[i]["user"]);
        console.log("Time:"+comments[i]["time"]);
        console.log("Content:"+comments[i]["content"]);
        section = document.getElementById("comments");
        chat_header = document.createElement("HEADER");
        comment_header = comments[i]["user"] + " " + comments[i]["time"];
        chat_header.appendChild(document.createTextNode(comment_header));
        chat_body = document.createElement("P");
        comment = document.createTextNode(comments[i]["content"]);
        chat_body.appendChild(comment);
        section.appendChild(chat_header);
        section.appendChild(chat_body);
    }
}
