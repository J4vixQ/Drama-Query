function printAll(data) {
  const list = document.getElementById('dataList');  // this is the <u1> element in HTML
  list.innerHTML = '';

  data.forEach(item => {
    const li = document.createElement('li');
    li.textContent = JSON.stringify(item);
    list.appendChild(li);
  });
}
