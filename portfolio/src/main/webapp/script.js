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

var context;
var canvas;
var myBubbleArray;
var numBubs = 20;
var animationID = null;
// Resizes bubbles when page size is changed
window.addEventListener('resize', init)

function startChat() {
  grabComments();
  init();
  grabUser();
}

class Bubble {
  constructor(color) {
    this.speed_factor = 3;
    if (canvas.width < 900) {
      this.speed_factor = 1.75;
    }
    this.color = color;
    this.size = Math.random() * canvas.width;
    this.x = Math.random() * canvas.width;
    this.y = Math.random() * canvas.height;
    this.radius = Math.floor(Math.random() * Math.sqrt(canvas.width));
    this.dx = Math.random() * this.speed_factor * this.randomDirection();
    this.dy = Math.random() * this.speed_factor * this.randomDirection();
    this.opacity = Math.random();
  }
  drawBubble() {
    context.beginPath();
    context.fillStyle = this.color;
    context.globalAlpha = this.opacity;
    context.arc(this.x, this.y, this.radius, 0, Math.PI * 2, true);
    context.closePath();
    context.fill();
  }
  randomDirection() {
    if (Math.random() > .5) {
      return -1;
    } else {
      return 1;
    }
  }
}

function init() {
  // cancels any previous animation to start a new one
  if (animationID !== null) {
    window.cancelAnimationFrame(animationID);
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  } else {
    canvas = document.getElementById('myCanvas');
    context = canvas.getContext('2d');
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  }
  myBubbleArray = [];
  for (var i = 0; i < numBubs; i++) {
    myblue = new Bubble('#4885ed');
    myred = new Bubble('#db3236');
    mygreen = new Bubble('#3cba54');
    myyellow = new Bubble('#f4c20d');
    myBubbleArray.push(myblue);
    myBubbleArray.push(myred);
    myBubbleArray.push(mygreen);
    myBubbleArray.push(myyellow);
  }
  animationID = window.requestAnimationFrame(animation);
}

var start = null;

function animation(time) {
  if (start === null) {
    start = time;
  }
  var elapsed = time - start;
  move(myBubbleArray, elapsed);
  start = time;
  animationID = window.requestAnimationFrame(animation);
}

/**
 * Moves bubbles in the background and keeps them within the size of the window.
 */
function move(myBubbleArray, timeElapsed) {
  // Clears background with a rectangle
  context.clearRect(0, 0, canvas.width, canvas.height);
  for (var i = 0; i < myBubbleArray.length; i++) {
    myBubble = myBubbleArray[i];
    myBubble.drawBubble();
    // Logic to add wall bouncing
    if (myBubble.x >= canvas.width || myBubble.x <= 0) {
      myBubble.dx *= -1;
    }
    if (myBubble.y <= 0 || myBubble.y >= canvas.height) {
      myBubble.dy *= -1;
    }
    myBubble.x += (myBubble.dx * timeElapsed / 20);
    myBubble.y += (myBubble.dy * timeElapsed / 20);
  }
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


async function grabUser() {
  const userInfo = await fetch('/chat-login', {
                     method: 'POST'
                   }).then(response => response.json());
  console.log(userInfo);
  user = 'Logged in as: ' + userInfo['email'];
  exit = userInfo['logoutURL'];
  const userContainer = document.getElementById('User');
  const logoutContainer = document.getElementById('logout');
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
  console.log(comments);

  const section = document.getElementById('comments');
  section.innerHTML = '';
  for (var i = 0; i < comments.length; i++) {
    // We want each comment to have a certain CSS styling
    myDiv = document.createElement('DIV');
    myDiv.setAttribute('class', 'comment');

    chatHeader = document.createElement('HEADER');
    commentHeader = comments[i]['user'] + ' (' + comments[i]['time'] + ')';
    chatHeader.appendChild(document.createTextNode(commentHeader));
    chatBody = document.createElement('P');
    chatBody.setAttribute('class', 'comment-body');
    comment = document.createTextNode(comments[i]['content']);
    chatBody.appendChild(comment);

    myDiv.appendChild(chatHeader);
    myDiv.appendChild(chatBody);
    section.appendChild(myDiv);
  }
}

async function deleteComments() {
  if (confirm('Are you sure you want to delete all comments')) {
    fetch('/delete-comments', {
      method: 'POST'
    }).then(() => setTimeout(grabComments, 1000));
  }
}
