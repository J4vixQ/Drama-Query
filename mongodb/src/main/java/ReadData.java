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

    public static Document readXML(String filePath) {
        Document result = new Document();

        try {
            File file = new File(filePath);

            DocumentBuilderFactory dramafactory = DocumentBuilderFactory.newInstance();
            dramafactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = dramafactory.newDocumentBuilder();
            org.w3c.dom.Document document = documentBuilder.parse(file);

            document.getDocumentElement().normalize();

            // Get TEI ID
            String teiID = document.getDocumentElement().getAttribute("xml:id");
            result.append("id", teiID);

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(new NamespaceContext() {
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

            // Get drama text title
            String title = xpath.evaluate("//tei:teiHeader//tei:fileDesc//tei:titleStmt//tei:title", document);
            //System.out.println("Title: " + title);
            result.append("title", title);

            // Get drama text author
            String authorFore = xpath.evaluate("//tei:teiHeader//tei:fileDesc//tei:titleStmt//tei:author//tei:persName//tei:forename", document);
            String authorSur = xpath.evaluate("//tei:teiHeader//tei:fileDesc//tei:titleStmt//tei:author//tei:persName//tei:surname", document);
            //System.out.println("Author: " + authorFore + " " + authorSur);
            result.append("author", authorFore + " " + authorSur);

            //Get drama text creation date

            // Get publication
            NodeList publicationList = document.getElementsByTagNameNS("*", "publicationStmt");
            if (publicationList.getLength() > 0) {
                Element publicationStmt = (Element) publicationList.item(0);
                NodeList children = publicationStmt.getChildNodes();
                Document publication = new Document();

                for (int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) node;

                        switch (e.getTagName()) {
                            case "publisher":
                                publication.append("publisher", e.getTextContent());
                                break;
                            case "idno":
                                String type = e.getAttribute("type");
                                if (type.equals("URL")) {
                                    publication.append("URL", e.getTextContent().trim());
                                } else if (type.equals("wikidata")) {
                                    publication.append("wikidata", e.getAttribute("xml:base"));
                                }
                                break;
                        }
                    }
                }
                result.append("publication", publication);
            } else {
                result.append("publication", null);
            }

            // Get source
            NodeList sourceList = document.getElementsByTagNameNS("*", "sourceDesc");
            Document source = new Document();
            if (sourceList.getLength() > 0) {
                Element sourceDesc = (Element) sourceList.item(0);
                //NodeList children = sourceDesc.getChildNodes();
                //Document source = new Document();

                NodeList biblList = sourceDesc.getElementsByTagNameNS("*", "bibl");
                for (int i = 0; i < biblList.getLength(); i++) {
                    Element bibl = (Element) biblList.item(i);
                    if (bibl.getAttribute("type").equals("digitalSource")) {
                        Document digitalSource = new Document();

                        NodeList digitalChildren = bibl.getChildNodes();
                        for (int j = 0; j < digitalChildren.getLength(); j++) {
                            Node node = digitalChildren.item(j);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element el = (Element) node;
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
                        source.append("digitalSource", digitalSource);
                    }
                }

                NodeList biblFullList = sourceDesc.getElementsByTagNameNS("*", "biblFull");
                for (int i = 0; i < biblFullList.getLength(); i++) {
                    Element biblFull = (Element) biblFullList.item(i);
                    if (biblFull.getAttribute("n").equals("printed source")) {
                        Document printedSource = new Document();

                        NodeList dateList = biblFull.getElementsByTagNameNS("*", "publicationStmt").item(0).getChildNodes();
                        for (int j = 0; j < dateList.getLength(); j++) {
                            Node node = dateList.item(j);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element dateElement = (Element) node;

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
            NodeList persons = document.getElementsByTagNameNS("*", "listPerson");
            List<Document> personList = new ArrayList<Document>();
            //Document personList = new Document();
            if (persons.getLength() > 0) {
                Element listPerson = (Element) persons.item(0);
                NodeList person = listPerson.getElementsByTagNameNS("*", "person");

                for (int i = 0; i < person.getLength(); i++) {
                    Element personElement = (Element) person.item(i);

                    String id = personElement.getAttribute("xml:id");
                    String sex = personElement.getAttribute("sex");
                    String name = "";

                    NodeList nameList = personElement.getElementsByTagNameNS("*", "persName");
                    if (nameList.getLength() > 0) {
                        name = nameList.item(0).getTextContent().trim();
                    }

                    Document p = new Document();
                    p.append("id", id);
                    p.append("name", name);
                    p.append("sex", sex);
                    personList.add(p);
                }

                NodeList personGrp = listPerson.getElementsByTagNameNS("*", "personGrp");

                for (int i = 0; i < personGrp.getLength(); i++) {
                    Element personGrpElement = (Element) personGrp.item(i);
                    String personGrpID = personGrpElement.getAttribute("xml:id");
                    String grpSex = personGrpElement.getAttribute("sex");
                    String personGrpName = "";

                    NodeList pgName = personGrpElement.getElementsByTagNameNS("*", "name");
                    if (pgName.getLength() > 0) {
                        personGrpName = pgName.item(0).getTextContent().trim();
                    }
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

            // Get front text
            NodeList frontList = document.getElementsByTagName("front");
            if (frontList.getLength() > 0) {
                Element frontElement = (Element) frontList.item(0);

                List<String> frontParagraph = frontText(frontElement);
                String fullText = String.join("\n\n", frontParagraph);

                String quote = "";
                NodeList quoteList = frontElement.getElementsByTagName("quote");
                if (quoteList.getLength() > 0) {
                    quote = quoteList.item(0).getTextContent().trim();
                }

                Document front = new Document();
                front.append("content", fullText);
                front.append("quote", quote);

                text.append("front", front);

            }

            Document body = new Document();
            // Get body
            NodeList bodyList = document.getElementsByTagNameNS("*", "body");
            List<Document> bodyContent = new ArrayList<>();

            if (bodyList.getLength() > 0) {
                Element bodyElement = (Element) bodyList.item(0);

                NodeList actList = bodyElement.getElementsByTagNameNS("*", "div");
                for (int i = 0; i < actList.getLength(); i++) {
                    Element act = (Element) actList.item(i);

                    NodeList sceneList = act.getElementsByTagNameNS("*", "div");
                    for (int j = 0; j < sceneList.getLength(); j++) {
                        Element scene = (Element) sceneList.item(j);

                        List<Document> sceneEntries = bodyText(scene);
                        for (Document d: sceneEntries) {
                            bodyContent.add(d);
                        }
                    }
                }
                text.append("body", bodyContent);
            } else {
                text.append("body", null);
            }

            NodeList backList = document.getElementsByTagNameNS("*", "back");
            if (backList.getLength() > 0) {
                Element backElement = (Element) backList.item(0);

                List<Document> backContent = backText(backElement);
                text.append("back", backContent);
            } else {
                text.append("back", null);
            }

            /*
            NodeList spList = document.getElementsByTagNameNS("http://www.tei-c.org/ns/1.0", "sp");

            for (int i = 0; i < spList.getLength(); i++) {
                Element spElement = (Element) spList.item(i);

                String speaker = spElement.getElementsByTagNameNS("http://www.tei-c.org/ns/1.0", "speaker").item(0).getTextContent();

                NodeList lines = spElement.getElementsByTagNameNS("http://www.tei-c.org/ns/1.0", "l");

                //System.out.println("Speaker:" + speaker);

                for (int j = 0; j < lines.getLength(); j++) {
                    //System.out.println("    " + lines.item(j).getTextContent().trim());
                }

            }
             */
            result.append("text", text);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<String> frontText(Node node) {
        List<String> paragraphs = new ArrayList<>();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) node;

            if ("p".equals(e.getTagName())) {
                paragraphs.add(e.getTextContent().trim());
            }

            NodeList children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                paragraphs.addAll(frontText(children.item(i)));
            }
        }

        return paragraphs;
    }

    public static List<Document> bodyText(Element scene) {
        List<Document> list = new ArrayList<>();

        String stage = "";

        NodeList stageList = scene.getElementsByTagName("stage");
        if (stageList.getLength() > 0) {
            stage = stageList.item(0).getTextContent().trim();
        }

        // Get sp, speaker, speakerID and content
        NodeList spList = scene.getElementsByTagName("sp");
        for (int i = 0; i < spList.getLength(); i++) {
            Element sp = (Element) spList.item(i);

            String speaker = "";
            String speakerID = sp.getAttribute("who").replace("#", "").trim();
            NodeList speakerTag = sp.getElementsByTagName("speaker");
            if (speakerTag.getLength() > 0) {
                speaker = speakerTag.item(0).getTextContent().trim();
            }

            NodeList lTags = sp.getElementsByTagName("l");
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
                g.append("speakerID", "stage");
                g.append("content", lgLines);
                list.add(g);
            }
        }

        return list;
    }

    public static List<Document> backText(Element backElement) {
        List<Document> backDivs = new ArrayList<>();

        NodeList divs = backElement.getElementsByTagName("div");
        for (int i = 0; i < divs.getLength(); i++) {
            Element div = (Element) divs.item(i);
            Document back =  new Document();

            String type = div.getAttribute("type");
            if (!type.isEmpty()) {
                back.append("type", type);
            }

            NodeList headList = div.getElementsByTagName("head");
            if (headList.getLength() > 0) {
                back.append("head", headList.item(0).getTextContent().trim());
            }

            NodeList lgList = div.getElementsByTagName("lg");
            List<String> s = new ArrayList<>();

            for (int j = 0; j < lgList.getLength(); j++) {
                Element lg = (Element) lgList.item(j);
                NodeList lines = lg.getElementsByTagName("l");

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

