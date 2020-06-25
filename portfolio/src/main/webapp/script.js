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
