# Drama Query – Overview

Chufan Zhang
Jianyang Gu
Ziqian Mo

## Objective

Early modern English drama holds rich literary and historical value, but it's often difficult to access and analyze due to complex formats and archaic language. Scholars and students need better tools to search, explore, and interpret these texts efficiently. By transforming and visualizing the data, we aim to make drama texts more accessible, searchable, and insightful for modern readers and researchers.

## Key Features

Search drama scripts by title, author, and publication year
Interactive pie charts showing character line distribution for selected drama
Word clouds of the most frequent words for selected character
Download entire drama data as JSON files for external use

Data Source: Drama XML files from DraCor.org

## Architecture Overview

**Data Ingestion**:
validated using XSD and RELAX NG, parsed using Java with DOM + XPath.

**Database**:
MongoDB

**Backend**:
Node.js, Express.js

**Frontend**:
HTML + CSS + D3.js

**Sample Queries**:
Title:   Caesar, Darius
Author:  William, Anon  
Year:    1607, 1599
