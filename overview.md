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

## Project Workflow

This section describes the pipeline of the Drama Text Query project, from XML validation to data visualization.

1. XML files are validated using XSD or RELAX NG to ensure structural integrity.
2. Read XML files from the `data` folder using DOM and XPath methods.
3. The parsed content is transformed into a custom-defined schema.
4. Then the data is stored in MongoDB database.
5. The backend provides RESTful API endpoints for querying the data.
6. The frontend visualizes the data.

## Data Schema

The following structure defines the schema of each parsed Drama Text file, which is also used as the document structure stored in MongoDB database.

```json
{
    _id: ObjectId('6888cd34a30393702714223b') // MongoDB automatically generated unique id
    id: "eng000261" // ID of the Drama Text
    title: "Caesar" // Title of the Drama Text
    author: "William Alexander" // Author of the DramaText
    publication: Object // Store publication information
    {
        publisher: "DraCor"
        URL: "https://dracor.org/"
        wikidata: "http://www.wikidata.org/entity/"
    }
    source: Object // Store digital and printed source information
    {
        digitalSource: Object // Store digital source information
        {
            name: "EarlyPrint Project"
            url: "https://texts.earlyprint.org/works/A16527_04.xml"
            licence: "Distributed under a Creative Commons Attribution-NonCommercial 3.0 Unp…"
        }
        printedSource: Object // Store printed source information
        {
            publisher: "Printed by Valentine Simmes for Ed: Blount,"
            pubPlace: "London :" // store publication place
            publication_date: "1607." // store publication date
            creation_date: "1604" // store creation date
        }
    }
    personList: Array // Store role information from Drama Text in list form
    {
        0: Object // Each role will save their id, name, and gender
        {
            id: "eng000261-marcus-brutus"
            name: "Marcus Brutus"
            sex: "MALE"
        }
        1: Object
        {
            id: "eng000261-decius-brutus"
            name: "Decius Brutus"
            sex: "MALE"
        }
    }
    text: Object // Save the contents of Drama Text, saving the front, body, and back parts separately
    {
        front: Object // The front part of Drama Text
        {
            content: "THE TRAGEDIE OF IVLIVS CAESAR."
            quote: "Carmine dij superi placantur, carmine manes."
        }
        body: Array // Save the main part of the Drama Text in list form
        {
            // Each object represents a piece of text content
            // and stores the speaker, speaker's ID, and the text content
            0: Object
            {
                speaker: "Iuno."
                speakerID: "eng000261-juno"
                content: Array // Store text content in list form
                {
                    0: "THough I a goddesse glance through th'azure round,"
                }
            }
        }
        back: Array // Store the back part of Drama Text in list form
        {
            // Each object represents a piece of back content
            // and stores the type, head, and content
            0: Object
            {
                type: "encomium"
                head: "Some verses written to his Maiestie by the Authour at the time of his …"
                content: Array // Store text content in list form
                {
                    0: "Stay tragick muse with those vntimely verses, With raging accents and …"
                }
            }
        }
    }
}
```
