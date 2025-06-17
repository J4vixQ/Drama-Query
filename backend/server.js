const express = require('express');
const cors = require('cors');
const app = express();
const port = 3000;

app.use(cors());

// 路由挂载
const textRoutes = require('./routes/texts');
app.use('/', textRoutes);

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
