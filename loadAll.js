function loadAll() {
    return fetch('http://localhost:3000/data/test')
    .then(res => res.json())
    .then(data => {
        return data;
    })
    .catch(err => {
        console.error('Failed to load data', err);
        return null;
    });
}