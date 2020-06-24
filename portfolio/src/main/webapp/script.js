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

function randomQuote() {
  const quotes = [
    'Great leaders inspire greatness in others.',
    'The best confidence builder is experience.',
    'Sometimes, accepting help is harder than offering it.',
    'A failure in planning is a plan for failure.',
    'Never give up hope, no matter how dark things seem.',
    'Who you were does not have to define who you are.'
  ];
  // Pick random quote using random for a number 0-1
  const quote = quotes[Math.floor(Math.random() * quotes.length)];
  // display in console
  const container = document.getElementById('starwars-quote');
  console.log(quote);
  container.innerText = quote;
}
