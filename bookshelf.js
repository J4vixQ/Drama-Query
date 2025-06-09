function printAll(data) {
  const list = document.getElementById('dataList');  // this is the <u1> element in HTML
  list.innerHTML = '';

  data.forEach(item => {
    const li = document.createElement('li');
    const teiHeader = item.TEI?.teiHeader || {};  // only show teiHeader
    li.textContent = JSON.stringify(teiHeader, null, 2);  // for better readability
    list.appendChild(li);
  });
}

function bookshelf(data) {
  const container = document.getElementById('cardContainer');
  const infoList = document.getElementById('dataList');

  data.forEach((item, index) => {
    const card = document.createElement('div');
    card.className = 'card';

    // extract title and author from TEI structure
    const title = item.TEI?.teiHeader?.fileDesc?.titleStmt?.titleText || 'No Title';
    const authorObj = item.TEI?.teiHeader?.fileDesc?.titleStmt?.author?.persName || {};
    const author = `${authorObj.forename || ''} ${authorObj.surname || ''}`.trim() || 'Unknown Author';

    // only show the title and author in the card
    card.innerHTML = `
      <h3>${title}</h3>
      <p><strong>${author}</strong></p>
    `;

    // User click the wanted document card (only 1 card)
    card.onclick = () => {
      printAll([item]);  // Visualization functions here
    };

    container.appendChild(card);
  });
}
