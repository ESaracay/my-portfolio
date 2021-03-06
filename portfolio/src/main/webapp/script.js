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

function startChat() {
  grabComments();
  grabUser();
  injectUploadURL();
}

/**
 * Picks a random movie from our list and displays
 * it when the user clicks the random movie generator button.
 */
function randomMovie() {
  const movies = [
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
  ];

  const movie = movies[Math.floor(Math.random() * movies.length)];

  const container = document.getElementById('movie');

  container.innerText = '"' + movie + '"';
}

/**
 * Fetches user information and adds it to chat header.
 */
async function grabUser() {
  const userInfo = await fetch('/chat-login').then(response => response.json());
  if (userInfo['email'] === 'none') {
    window.location = userInfo['loginURL'];
  }
  user = 'Logged in as: ' + userInfo['email'];
  exit = userInfo['logoutURL'];
  const userContainer = document.getElementById('User');
  const logoutContainer = document.getElementById('logout');
  // If page is refreshed then we want to reload the username
  userContainer.innerHTML = '';
  logoutContainer.setAttribute('href', exit);
  userContainer.appendChild(document.createTextNode(user));
}

/**
 * Fetches comments from server and inserts the given
 * Json into a comment div.
 */
async function grabComments() {
  const comments = await fetch('/data').then(response => response.json());

  const section = document.getElementById('comments');
  section.innerHTML = '';
  for (let i = 0; i < comments.length; i++) {
    // We want each comment to have a certain CSS styling
    myDiv = document.createElement('div');
    myDiv.setAttribute('class', 'comment');

    chatHeader = document.createElement('header');
    commentHeader =
        comments[i]['user'] + ' (' + comments[i]['timeCreated'] + ')';
    chatHeader.appendChild(document.createTextNode(commentHeader));
    chatBody = document.createElement('p');
    chatBody.setAttribute('class', 'comment-body');
    comment = document.createTextNode(comments[i]['content']);
    chatBody.appendChild(comment);

    myDiv.appendChild(chatHeader);
    if(comments[i]["imageBlobKey"] != null) {
        chatImage = document.createElement('IMG');
        chatImage.setAttribute('class', 'chat-image');
        chatImage.setAttribute('src', "/serve?key=" + comments[i]["imageBlobKey"]);
        myDiv.appendChild(chatImage);
    }
    myDiv.appendChild(chatBody);
    section.appendChild(myDiv);
  }
}

/**
 * Makes call to delete data servlet which erases all comments from dataStore.
 */
async function deleteComments() {
  if (confirm("Are you sure you want to delete all comments")) {
    fetch("/delete-comments", {
      method: 'POST'
    }).then(() => setTimeout(grabComments, 1000));
  }
}

async function injectUploadURL(){
  const blobURL = await fetch("/blobstore-upload-url").then((response) => {return response.text();});
  const myForm = document.getElementById("comment-form");
  myForm.action = blobURL;
}