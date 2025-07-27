let currentSelectedBook = null; // to keep track of the currently selected book

function bookshelf(data) {
  const container = document.getElementById('cardContainer');
  // const infoList = document.getElementById('dataList');

  container.innerHTML = '';
  // infoList.innerHTML = '';

  // show books within conditions
  data.forEach((item, index) => {
    const card = document.createElement('div');
    card.className = 'card';

    const title = item.title || 'No Title';
    const author = item.author || 'Unknown Author';
    const publication_date = item.publication_date || 'Unknown Year';


    card.innerHTML = `
      <h3>${title}</h3>
      <p><strong>${author}</strong></p>
      <p><strong>${publication_date}</strong></p>
    `;

    card.onclick = async () => {
      d3.select("#pieChart").selectAll("*").remove();  // clear previous visualizations
      d3.select("#wordCloud").selectAll("*").remove();

      // select one book from the presented ones
      console.log("Clicked on: ", item);
      console.log("book id: ", item.id);

      // for download
      currentSelectedBook = item;
      const downloadBookBtn = document.getElementById('downloadBookBtn');
      if (downloadBookBtn) {
        downloadBookBtn.style.display = "inline-block";
        // downloadBookBtn.onclick = () => downloadJSON(item, `${(item.title || 'book').replace(/\s/g,'_')}.json`);
        downloadBookBtn.onclick = async () => {
          const fullItem = await fetch(`http://localhost:3000/api/getFullDrama?id=${encodeURIComponent(item.id)}`)
            .then(res => res.json());
          downloadJSON(fullItem, `${(item.title || 'book').replace(/\s/g,'_')}.json`);
        };
      }

      const characters = await getCharacters(item.id);  // characters in book
      console.log("characters: ", characters);
      const linesByCharacter = await getLinesByCharacter(item.id, characters);  // lines by character in book
      console.log("linesByCharacter: ", linesByCharacter);
      const counts = lineCounts(linesByCharacter);  // for the pie chart
      console.log("Line counts (sorted):", counts);

      // show characters (with line counts) in the selected book
      drawPieChart(counts, item.title, (charId) => {
        console.log("Clicked on: ", charId);  // select character in the pie chart
        topWords(charId, linesByCharacter);  // count the top words and draw the word cloud
      });
    };

    container.appendChild(card);
  });
}

// download json file
function downloadJSON(obj, filename) {
  const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(obj, null, 2));
  const downloadAnchorNode = document.createElement('a');
  downloadAnchorNode.setAttribute("href", dataStr);
  downloadAnchorNode.setAttribute("download", filename);
  document.body.appendChild(downloadAnchorNode); // required for firefox
  downloadAnchorNode.click();
  downloadAnchorNode.remove();
}