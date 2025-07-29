# Data Schema

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
