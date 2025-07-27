function searchCombined(titleText, authorText, dateText) {
  const params = new URLSearchParams();
  if (titleText) params.append('title', titleText.trim());
  if (authorText) params.append('author', authorText.trim());
  if (dateText) params.append('publication_year', dateText.trim());

  // Query the backend API with the combined parameters
  return fetch(`http://localhost:3000/api/getDrama?${params.toString()}`)
    .then(res => res.json());
}

function createSearchBox(callback) {
  document.getElementById('searchButton').addEventListener('click', () => {
    const title = document.getElementById('titleInput').value.trim();
    const author = document.getElementById('authorInput').value.trim();
    const publication_date = document.getElementById('dateInput').value.trim();

    // Invoke the callback function with collected search inputs
    callback({ title, author, publication_date });
  });
}

// Fetch all characters for a specific drama (by ID).
async function getCharacters(dramaId) {
  const url = `http://localhost:3000/api/getCharacterByDrama?dramaId=${encodeURIComponent(dramaId)}`;

  try {
    const res = await fetch(url);
    const characterList = await res.json();

    // Normalize the character
    const simplified = characterList.map(person => ({
      name: person.name || '[Unnamed]',
      id: person.id || person['xml:id'] || '[No ID]'
    }));

    return simplified;

  } catch (err) {
    console.error("获取角色失败：", err);
    return []; 
  }
}

// async function getText(dramaId, speakerID = null) {
//   const params = new URLSearchParams({ dramaId });
//   if (speakerID) params.append('speakerID', speakerID);

//   const url = `http://localhost:3000/api/getTextByDramaWithSpeaker?${params.toString()}`;

//   try {
//     const res = await fetch(url);
//     const textList = await res.json();
//     return textList;
//   } catch (err) {
//     console.error("获取对白失败：", err);
//     return [];
//   }
// }



// Fetch all lines (text) from a drama.
// If a specific speaker ID is given, only their lines will be fetched.
async function getText(dramaId, speakerID = null) {
  const params = new URLSearchParams({ dramaId });
  if (speakerID) params.append('speakerID', speakerID);

  const url = `http://localhost:3000/api/getTextByDramaWithSpeaker?${params.toString()}`;

  try {
    const res = await fetch(url);
    const textList = await res.json();

    // Flatten the nested structure to get a plain list of dialogue lines
    const allLines = textList
      .map(item => item.content || [])
      .flat();

    return allLines;

  } catch (err) {
    console.error("获取对白失败：", err);
    return [];
  }
}

async function getLinesByCharacter(dramaId, characters) {
  const result = {};

  for (const char of characters) {
    const lines = await getText(dramaId, char.id);
    result[char.id] = {
      name: char.name,
      lines: lines
    };
  }

  return result;
}

function lineCounts(linesByCharacter) {
  const summaryArray = [];

  for (const charId in linesByCharacter) {
    const charInfo = linesByCharacter[charId];
    const name = charInfo.name || 'Unknown';
    const lineCount = Array.isArray(charInfo.lines) ? charInfo.lines.length : 0;
    summaryArray.push({ charId, name, lineCount });
  }

  // descend
  summaryArray.sort((a, b) => b.lineCount - a.lineCount);

  return summaryArray;
}