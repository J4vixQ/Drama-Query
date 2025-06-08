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
