// function printAll(data) {
//   const list = document.getElementById('dataList');  // this is the <u1> element in HTML
//   list.innerHTML = '';

//   data.forEach(item => {
//     const li = document.createElement('li');
//     const teiHeader = item.TEI?.teiHeader || {};  // only show teiHeader
//     li.textContent = JSON.stringify(teiHeader, null, 2);  // for better readability
//     list.appendChild(li);
//   });
// }

function bookshelf(data) {
  const container = document.getElementById('cardContainer');
  // const infoList = document.getElementById('dataList');

  container.innerHTML = '';
  // infoList.innerHTML = '';

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

    // Visualizations here
    card.onclick = async () => {
      // clear previous visualizations
      d3.select("#pieChart").selectAll("*").remove();
      d3.select("#wordCloud").selectAll("*").remove();

      // select book
      console.log("Clicked on: ", item);
      console.log("book id: ", item.id);
      const characters = await getCharacters(item.id);
      console.log("characters: ", characters);
      const linesByCharacter = await getLinesByCharacter(item.id, characters);
      console.log("linesByCharacter: ", linesByCharacter);
      const counts = lineCounts(linesByCharacter);
      console.log("Line counts (sorted):", counts);

      // select character
      drawPieChart(counts, (charId) => {
        console.log("Clicked on: ", charId);
        topWords(charId, linesByCharacter);
      });
    };

    container.appendChild(card);
  });
}

