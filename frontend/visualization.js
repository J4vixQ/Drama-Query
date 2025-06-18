function drawPieChart(data, onClickCallback) {
  const margin = { top: 20, right: 150, bottom: 20, left: 20 };
  const width = 500;
  const height = 400;
  const radius = Math.min(width - margin.left - margin.right, height - margin.top - margin.bottom) / 2;

  d3.select("#pieChart").selectAll("*").remove(); // remove old

  const svg = d3.select("#pieChart")
    .attr("width", width)
    .attr("height", height)
    .append("g")
    .attr("transform", `translate(${margin.left + radius}, ${margin.top + radius})`);

  const color = d3.scaleOrdinal(d3.schemeCategory10);

  const pie = d3.pie()
    .value(d => d.lineCount)
    .sort(null);

  const arc = d3.arc()
    .innerRadius(0)
    .outerRadius(radius);

  const arcs = svg.selectAll("arc")
    .data(pie(data))
    .enter()
    .append("g")
    .attr("class", "arc");

  // pie
  arcs.append("path")
    .attr("d", arc)
    .attr("fill", d => color(d.data.charId))
    .on("mouseover", function () {
      d3.selectAll("path").style("opacity", 0.3);
      d3.select(this).style("opacity", 1);
    })
    .on("mouseout", function () {
      d3.selectAll("path").style("opacity", 1);
    })
    .on("click", function (event, d) {
      if (onClickCallback) {
        onClickCallback(d.data.charId);
      }
    });

  // tooltip
  arcs.append("title")
  .text(d => `${d.data.name}: ${d.data.lineCount} lines`);

  // legend
  const legend = svg.selectAll(".legend")
    .data(data)
    .enter()
    .append("g")
    .attr("class", "legend")
    .attr("transform", (d, i) => `translate(${radius + 20}, ${-radius + i * 20})`);

  legend.append("rect")
    .attr("width", 12)
    .attr("height", 12)
    .attr("fill", d => color(d.charId));

  legend.append("text")
    .attr("x", 18)
    .attr("y", 10)
    .style("font-size", "12px")
    .text(d => `${d.name}: ${d.lineCount}`);
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

function topWords(charId, linesByCharacter) {
  const charData = linesByCharacter[charId];
  if (!charData || !Array.isArray(charData.lines)) {
    console.warn("No lines found for character:", charId);
    return;
  }

  const wordCounts = lines2words(charData.lines);
  const topWords = getTopWords(wordCounts, 100, 0.1);
  console.log("Top words for character", charId, ":", topWords);
  drawWordCloud(topWords);
}

function drawWordCloud(words) {
  d3.select("#wordCloud").selectAll("*").remove();

  const svg = d3.select("#wordCloud"),
        width = +svg.attr("width"),
        height = +svg.attr("height");

  const layout = d3.layout.cloud()
    .size([width, height])
    .words(words.map(d => ({ text: d.text, size: 10 + d.value })))
    .padding(5)
    .rotate(() => ~~(Math.random() * 2) * 90)
    .fontSize(d => d.size)
    .on("end", draw);

  layout.start();

  function draw(words) {
    svg.append("g")
      .attr("transform", `translate(${width / 2}, ${height / 2})`)
      .selectAll("text")
      .data(words)
      .enter().append("text")
      .style("font-size", d => d.size + "px")
      .style("fill", () => d3.schemeCategory10[Math.floor(Math.random() * 10)])
      .attr("text-anchor", "middle")
      .attr("transform", d => `translate(${d.x}, ${d.y})rotate(${d.rotate})`)
      .text(d => d.text);
  }
}
