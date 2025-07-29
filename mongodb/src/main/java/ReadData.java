import org.bson.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ReadData {

    // Read XML file according to the given filePath and parse the content
    public static Document readXML(String filePath) {
        // Create a Document object to store the parsed results
        Document result = new Document();

        try {
            File file = new File(filePath);

            // Create a DocumentBuilderFactory Instance
            DocumentBuilderFactory dramafactory = DocumentBuilderFactory.newInstance();
            dramafactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = dramafactory.newDocumentBuilder();
            // Parse the XML file through documentBuilder to generate a Document object, DOM tree
            org.w3c.dom.Document document = documentBuilder.parse(file);

            // Standardize the DOM tree, such as removing whitespace characters, etc.
            document.getDocumentElement().normalize();

            // Get TEI ID
            String teiID = document.getDocumentElement().getAttribute("xml:id");
            // Save as id field
            result.append("id", teiID);

            // Create XPathFactory Instance
            XPathFactory xpathFactory = XPathFactory.newInstance();
            // Create XPath Instance
            XPath xpath = xpathFactory.newXPath();
            // Because the TEI namespace is used in XML file, the namespace context needs to be set
            xpath.setNamespaceContext(new NamespaceContext() {
                // If the prefix tei used in XPath expression,
                // the corresponding namespace URI is returned
                public String getNamespaceURI(String prefix) {
                    if (prefix.equals("tei")) {
                        return "http://www.tei-c.org/ns/1.0";
                    }
                    return null;
                }

                public String getPrefix(String uri) {
                    return null;
                }

                public Iterator<String> getPrefixes(String uri) {
                    return Collections.emptyIterator();
                }
            });

            // Get the title of the drama text based on the XPath expression
            String title = xpath.evaluate("//tei:teiHeader//tei:fileDesc//tei:titleStmt//tei:title", document);
            //System.out.println("Title: " + title);
            // Store as title field in the result object
            result.append("title", title);

            // Get the author information of the drama text, including first name and last name,
            // based on the XPath expression
            String authorFore = xpath.evaluate("//tei:teiHeader//tei:fileDesc//tei:titleStmt//tei:author//tei:persName//tei:forename", document);
            String authorSur = xpath.evaluate("//tei:teiHeader//tei:fileDesc//tei:titleStmt//tei:author//tei:persName//tei:surname", document);
            String authorName = null;
            if (!authorFore.isEmpty() || !authorSur.isEmpty()) {
                authorName = authorFore + " " + authorSur;
                
            } else {
                authorName = xpath.evaluate("//tei:teiHeader//tei:fileDesc//tei:titleStmt//tei:author//tei:persName", document);
            }
            //System.out.println("Author: " + authorFore + " " + authorSur);
            if (authorName != null && !authorName.isEmpty()){
                result.append("author", authorName);
            } else {
                result.append("author", null)
            }
            //Get drama text creation date

            // Get publication
            // Use DOM methods to find all publicationStmt tags from the XML document
            NodeList publicationList = document.getElementsByTagNameNS("*", "publicationStmt");
            // First, check whether publicationList is empty, that is,
            // whether the publicationStmt tag is found. If not, set it to null.
            if (publicationList.getLength() > 0) {
                // If the publicationStmt is found, get the first node and force it to be converted to Element type
                Element publicationStmt = (Element) publicationList.item(0);
                // Get all child nodes under the publicationStmt node
                NodeList children = publicationStmt.getChildNodes();
                // Create a new Doument object to store publication information
                Document publication = new Document();

                // Traverse all child nodes
                for (int i = 0; i < children.getLength(); i++) {
                    // Get child nodes
                    Node node = children.item(i);
                    // Process real label nodes and skip irrelevant nodes such as line breaks
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        // Convert the node to Element type to facilitate obtaining tag name, attributes and content
                        Element e = (Element) node;

                        // Process content based on tag name
                        switch (e.getTagName()) {
                            // If tag is publisher, extract the content and save it to the publication object
                            case "publisher":
                                publication.append("publisher", e.getTextContent());
                                break;
                            // if tag is idno, first get the type attribite
                            case "idno":
                                String type = e.getAttribute("type");
                                // If type is URL, save the URL to the publication object
                                if (type.equals("URL")) {
                                    publication.append("URL", e.getTextContent().trim());
                                } else if (type.equals("wikidata")) {
                                    // If type is wikidata, save the wikidata to the publication object
                                    publication.append("wikidata", e.getAttribute("xml:base"));
                                }
                                break;
                        }
                    }
                }
                // Add the processed publication object to the result object
                result.append("publication", publication);
            } else {
                // Otherwise set to null
                result.append("publication", null);
            }

            // Get source
            // Use DOM methods to find all sourceDesc tags from XML document
            NodeList sourceList = document.getElementsByTagNameNS("*", "sourceDesc");
            Document source = new Document();
            // First, determine whether sourceList is empty, whether the sourceDesc tag is found.
            // If it is empty, set source to null
            if (sourceList.getLength() > 0) {
                Element sourceDesc = (Element) sourceList.item(0);
                //NodeList children = sourceDesc.getChildNodes();
                //Document source = new Document();

                // Get all bibl tags under the sourceDescNode
                NodeList biblList = sourceDesc.getElementsByTagNameNS("*", "bibl");
                // Iterate over all bibl tags
                for (int i = 0; i < biblList.getLength(); i++) {
                    Element bibl = (Element) biblList.item(i);
                    // Determine whether the type attribute of the bibl tag is digitalSource
                    if (bibl.getAttribute("type").equals("digitalSource")) {
                        Document digitalSource = new Document();

                        // Get all child nodes under bibl tag
                        NodeList digitalChildren = bibl.getChildNodes();
                        // Traverse all child nodes
                        for (int j = 0; j < digitalChildren.getLength(); j++) {
                            Node node = digitalChildren.item(j);
                            // Traverse all child nodes
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element el = (Element) node;
                                // Process content based on tag name
                                switch (el.getTagName()) {
                                    case "name":
                                        digitalSource.append("name", el.getTextContent().trim());
                                        break;
                                    case "idno":
                                        if (el.getAttribute("type").equals("URL")) {
                                            digitalSource.append("url", el.getTextContent().trim());
                                        }
                                        break;
                                    case "availability":
                                        Element licence = (Element) el.getElementsByTagNameNS("*", "licence").item(0);
                                        digitalSource.append("licence", licence.getTextContent().trim());
                                        break;
                                }
                            }
                        }
                        // Add the processed digitalSource object to the source object
                        source.append("digitalSource", digitalSource);
                    }
                }

                // Get all biblFul tags under the sourceDesc node
                NodeList biblFullList = sourceDesc.getElementsByTagNameNS("*", "biblFull");
                // Iterate over all biblFull tags
                for (int i = 0; i < biblFullList.getLength(); i++) {
                    Element biblFull = (Element) biblFullList.item(i);
                    if (biblFull.getAttribute("n").equals("printed source")) {
                        Document printedSource = new Document();

                        // Get all child nodes under the biblFull tag
                        NodeList dateList = biblFull.getElementsByTagNameNS("*", "publicationStmt").item(0).getChildNodes();
                        // Traverse all child nodes
                        for (int j = 0; j < dateList.getLength(); j++) {
                            Node node = dateList.item(j);
                            // Process real label nodes and skip irrelevant nodes such as line breaks
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element dateElement = (Element) node;

                                // Process content based on tag name
                                switch (dateElement.getTagName()) {
                                    case "publisher":
                                        printedSource.append("publisher", dateElement.getTextContent().trim());
                                        break;
                                    case "pubPlace":
                                        printedSource.append("pubPlace", dateElement.getTextContent().trim());
                                        break;
                                    case "date":
                                        if (dateElement.getAttribute("type").equals("publication_date")) {
                                            printedSource.append("publication_date", dateElement.getTextContent().trim());
                                        } else if (dateElement.getAttribute("type").equals("creation_date")) {
                                            printedSource.append("creation_date", dateElement.getTextContent().trim());
                                        }
                                }
                            }
                        }
                        source.append("printedSource", printedSource);
                    }
                }
                result.append("source", source);
            } else {
                result.append("source", null);
            }

            // Get person list
            // Use DOM method to find all listPerson tag from XML document
            NodeList persons = document.getElementsByTagNameNS("*", "listPerson");
            // Create a list of Document objects to store all person information
            List<Document> personList = new ArrayList<Document>();
            //Document personList = new Document();
            // Determine whether listPerson is empty, that is, whether listPerson tag is found.
            // If it is empty, personList is set to null
            if (persons.getLength() > 0) {
                Element listPerson = (Element) persons.item(0);
                // Get all person nodes under the listPerson node
                NodeList person = listPerson.getElementsByTagNameNS("*", "person");

                // Traverse all person nodes and extract information about each role
                for (int i = 0; i < person.getLength(); i++) {
                    Element personElement = (Element) person.item(i);

                    // Get person's id, gender, and name
                    String id = personElement.getAttribute("xml:id");
                    String sex = personElement.getAttribute("sex");
                    String name = "";

                    NodeList nameList = personElement.getElementsByTagNameNS("*", "persName");
                    if (nameList.getLength() > 0) {
                        name = nameList.item(0).getTextContent().trim();
                    }

                    // Create a new Document object, store the role information, and then save it to the personList
                    Document p = new Document();
                    p.append("id", id);
                    p.append("name", name);
                    p.append("sex", sex);
                    personList.add(p);
                }

                // Get all personGrp nodes under the listPerson node
                NodeList personGrp = listPerson.getElementsByTagNameNS("*", "personGrp");

                // Traverse all personGrp nodes and extract information about each role group
                for (int i = 0; i < personGrp.getLength(); i++) {
                    Element personGrpElement = (Element) personGrp.item(i);
                    String personGrpID = personGrpElement.getAttribute("xml:id");
                    String grpSex = personGrpElement.getAttribute("sex");
                    String personGrpName = "";

                    NodeList pgName = personGrpElement.getElementsByTagNameNS("*", "name");
                    if (pgName.getLength() > 0) {
                        personGrpName = pgName.item(0).getTextContent().trim();
                    }
                    // Create a new Document object, store the role group information, then save it to personList
                    Document grp = new Document();
                    grp.append("id", personGrpID);
                    grp.append("name", personGrpName);
                    grp.append("sex", grpSex);
                    personList.add(grp);
                }
                result.append("personList", personList);
            } else {
                result.append("personList", null);
            }

            Document text = new Document();

            // Document front = new Document();
            // Get front text
            // First get the front tag in XML document
            NodeList frontList = document.getElementsByTagNameNS("*", "front");
            if (frontList.getLength() > 0) {
                Element frontElement = (Element) frontList.item(0);

                // Call frontText method to extract all paragraph contents under the front tag
                List<String> frontParagraph = frontText(frontElement);
                String fullText = String.join("\n\n", frontParagraph);

                // Extract quote tag content
                String quote = "";
                NodeList quoteList = frontElement.getElementsByTagNameNS("*", "quote");
                // If quote tag exists, get its content and remove the leading and trailing spaces
                if (quoteList.getLength() > 0) {
                    quote = quoteList.item(0).getTextContent().trim();
                }

                // Create a new Document object to store the front content
                Document front = new Document();
                front.append("content", fullText);
                front.append("quote", quote);

                text.append("front", front);
            } else {
                text.append("front", null)
            }


            Document body = new Document();
            // Get body
            NodeList bodyList = document.getElementsByTagNameNS("*", "body");
            List<Document> bodyContent = new ArrayList<>();

            if (bodyList.getLength() > 0) {
                Element bodyElement = (Element) bodyList.item(0);

                // Get all div tags under the body tag, which usually represent various parts of the script
                NodeList actList = bodyElement.getElementsByTagNameNS("*", "div");

                // Traverse all div tags
                for (int i = 0; i < actList.getLength(); i++) {
                    Element act = (Element) actList.item(i);

                    // Get all sub-div tags under div tag
                    NodeList sceneList = act.getElementsByTagNameNS("*", "div");

                    if (sceneList.getLength() > 0) {
                        for (int j = 0; j < sceneList.getLength(); j++) {
                            Element scene = (Element) sceneList.item(j);

                            // Call bodyText method to extract text content
                            List<Document> sceneEntries = bodyText(scene);
                            for (Document d: sceneEntries) {
                                bodyContent.add(d);
                            }
                        }
                    } else {
                        List<Document> sceneEntries = bodyText(act);
                        for (Document d: sceneEntries) {
                            bodyContent.add(d);
                        }
                    }

                }
                text.append("body", bodyContent);
            } else {
                text.append("body", null);
            }

            // Get all back tag in XML document
            NodeList backList = document.getElementsByTagNameNS("*", "back");
            if (backList.getLength() > 0) {
                Element backElement = (Element) backList.item(0);

                // Call backText method to extract the text content under the back tag
                List<Document> backContent = backText(backElement);
                text.append("back", backContent);
            } else {
                text.append("back", null);
            }

            result.append("text", text);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // frontText method is used to extract the text content of the front part
    public static List<String> frontText(Node node) {
        List<String> paragraphs = new ArrayList<>();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) node;

            // If the tag name is p, extract the content and save it to the paragraphs list
            if ("p".equals(e.getTagName())) {
                paragraphs.add(e.getTextContent().trim());
            }

            // Extract all child nodes of the current node
            NodeList children = e.getChildNodes();
            // Traverse all child nodes and recursively call the frontText method to find all p tags
            for (int i = 0; i < children.getLength(); i++) {
                paragraphs.addAll(frontText(children.item(i)));
            }
        }

        return paragraphs;
    }

    // bodyText method is used to extract the text content of the body part
    public static List<Document> bodyText(Element scene) {
        List<Document> list = new ArrayList<>();

        String stage = "";

        // If there is a stage tag, get the stage content and save it as stage
        NodeList stageList = scene.getElementsByTagNameNS("*", "stage");
        if (stageList.getLength() > 0) {
            stage = stageList.item(0).getTextContent().trim();
        }

        // Get sp, speaker, speakerID and content
        // Get all sp tags under scene tag
        NodeList spList = scene.getElementsByTagNameNS("*", "sp");
        for (int i = 0; i < spList.getLength(); i++) {
            Element sp = (Element) spList.item(i);

            // Get speaker's ID and name
            String speaker = "";
            String speakerID = sp.getAttribute("who").replace("#", "").trim();
            NodeList speakerTag = sp.getElementsByTagNameNS("*", "speaker");
            if (speakerTag.getLength() > 0) {
                speaker = speakerTag.item(0).getTextContent().trim();
            }

            // Get all l tags under the sp tag and find the lines
            NodeList lTags = sp.getElementsByTagNameNS("*", "l");
            List<String> lines = new ArrayList<>();
            for (int j = 0; j < lTags.getLength(); j++) {
                lines.add(lTags.item(j).getTextContent().trim());
            }

            Document b = new Document();
            b.append("speaker", speaker);
            b.append("speakerID", speakerID);
            b.append("content", lines);
            list.add(b);
        }

        // Get chorus
        NodeList divList = scene.getElementsByTagName("div");
        for (int i = 0; i < divList.getLength(); i++) {
            Element div = (Element) divList.item(i);

            if (div.getAttribute("type").equals("chorus")) {
                NodeList chorusLines = div.getElementsByTagName("l");
                List<String> chorus = new ArrayList<>();
                for (int j = 0; j < chorusLines.getLength(); j++) {
                    chorus.add(chorusLines.item(j).getTextContent().trim());
                }
                Document c = new Document();
                c.append("speaker", "chorus");
                c.append("speakerID", "all");
                c.append("content", chorus);
                list.add(c);
            }
        }

        // If sp tag is not found, then this line has no speaker and needs special processing
        if (spList.getLength() == 0) {
            NodeList lgList = scene.getElementsByTagName("lg");
            List<String> lgLines = new ArrayList<>();
            for (int i = 0; i < lgList.getLength(); i++) {
                Element lg = (Element) lgList.item(i);
                NodeList lList = lg.getElementsByTagName("l");
                for (int j = 0; j < lList.getLength(); j++) {
                    lgLines.add(lList.item(j).getTextContent().trim());
                }
            }
            if (!lgLines.isEmpty()) {
                Document g = new Document();
                g.append("speaker", stage.isEmpty() ? null : stage);
                g.append("speakerID", "unknown");
                g.append("content", lgLines);
                list.add(g);
            }
        }

        return list;
    }

    // backText method, used to extract the text content of back part
    public static List<Document> backText(Element backElement) {
        List<Document> backDivs = new ArrayList<>();

        // Get all div tags
        NodeList divs = backElement.getElementsByTagNameNS("*", "div");
        // Iterate through all div tags
        for (int i = 0; i < divs.getLength(); i++) {
            Element div = (Element) divs.item(i);
            Document back =  new Document();

            // Get the type attribute of div tag, and if it exists, save it to the back object
            String type = div.getAttribute("type");
            if (!type.isEmpty()) {
                back.append("type", type);
            }

            // Get all head tags
            NodeList headList = div.getElementsByTagNameNS("*", "head");
            // If it exists, save it as the head field and add it to the back object
            if (headList.getLength() > 0) {
                back.append("head", headList.item(0).getTextContent().trim());
            }

            // Get all lg tags
            NodeList lgList = div.getElementsByTagNameNS("*", "lg");
            List<String> s = new ArrayList<>();

            // Iterate through all lg tags
            for (int j = 0; j < lgList.getLength(); j++) {
                Element lg = (Element) lgList.item(j);
                // Get all l tags under the lg tag
                NodeList lines = lg.getElementsByTagNameNS("*", "l");

                StringBuilder builder = new StringBuilder();
                for (int a = 0; a < lines.getLength(); a++) {
                    builder.append(lines.item(a).getTextContent().trim()).append(" ");
                }
                s.add(builder.toString().trim());
            }

            back.append("content", s);
            backDivs.add(back);
        }

        return backDivs;

    }
}

