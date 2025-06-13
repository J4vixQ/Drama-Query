function extractLeafLines(obj) {
  const lines = [];

  function traverse(node) {
    if (typeof node !== 'object' || node === null) return;

    for (const key in node) {
      const value = node[key];

      // find the leaf nodes like { "number": "text" }
      if (/^\d+$/.test(key) && typeof value === 'string') {
        lines.push(value);
      }

      // traverse
      if (typeof value === 'object') {
        traverse(value);
      }
    }
  }

  traverse(obj);
  return lines;
}

function lines2words(lines) {
  const wordCounts = {};

  // common stop words
  const stopWords = new Set([
    'the', 'and', 'of', 'to', 'in', 'that', 'is', 'with', 'for', 'on', 'as', 'are',
    'was', 'were', 'be', 'by', 'this', 'it', 'from', 'or', 'an', 'at', 'not', 'but',
    'which', 'so', 'a', 'i', 'you', 'we', 'he', 'she', 'they', 'me', 'my', 'your',
    'his', 'her', 'our', 'their', 'what', 'who', 'how', 'when', 'where', 'why',
    'if', 'then', 'shall', 'will', 'may', 'might', 'can', 'could', 'do', 'did',
    'has', 'have', 'had', 'would', 'should', 'all', 'no', 'yes', 'than', 'thus'
  ]);

  lines.forEach(line => {
    const clean = line
      .toLowerCase()
      .replace(/[^\w\s]/g, '')  // remove punctuation
      .replace(/\d+/g, '');  // remove numbers

    const words = clean.split(/\s+/);

    words.forEach(word => {
      if (!word || stopWords.has(word)) return;
      wordCounts[word] = (wordCounts[word] || 0) + 1;
    });
  });

  return wordCounts;
}

function getTopWords(wordCounts, maxWords = 100, percent = 0.1) {  // at most 10% or 100 words
  const sorted = Object.entries(wordCounts)
    .sort((a, b) => b[1] - a[1]); // order by frequency descending

  const limit = Math.min(
    Math.ceil(sorted.length * percent),
    maxWords
  );

  return sorted.slice(0, limit).map(([text, value]) => ({ text, value }));
}

function displayTopWords(topWords) {
  const container = document.getElementById("topWordsOutput");
  container.innerHTML = "<h3>Top Words</h3>";

  const list = document.createElement("ul");
  topWords.forEach(({ text, value }) => {
    const li = document.createElement("li");
    li.textContent = `${text} (${value})`;
    list.appendChild(li);
  });

  container.appendChild(list);
}
