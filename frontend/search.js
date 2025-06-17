function searchCombined(titleText, authorText) {
  const params = new URLSearchParams();
  if (titleText) params.append('title', titleText.trim());
  if (authorText) params.append('author', authorText.trim());

  return fetch(`http://localhost:3000/searchCombined?${params.toString()}`)
    .then(res => res.json());
}

function createSearchBox(callback) {
  document.getElementById('searchButton').addEventListener('click', () => {
    const title = document.getElementById('titleInput').value.trim();
    const author = document.getElementById('authorInput').value.trim();
    callback({ title, author });
  });
}
