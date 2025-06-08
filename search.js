function searchAuthor(authorText) {
  const query = encodeURIComponent(authorText.trim());
  return fetch(`http://localhost:3000/searchAuthor?author=${query}`)
    .then(res => res.json());
}

function searchTitle(titleText) {
  const query = encodeURIComponent(titleText.trim());
  return fetch(`http://localhost:3000/searchTitle?title=${query}`)
    .then(res => res.json());
}
