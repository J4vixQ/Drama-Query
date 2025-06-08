import os
import json
import xml.etree.ElementTree as ET

def strip_namespace(tag):
    """remove namespace, only keep the tag names"""
    return tag.split('}')[-1] if '}' in tag else tag

def etree_to_dict(elem):
    """recursively convert an ElementTree element to a dictionary"""
    tag = strip_namespace(elem.tag)
    node = {}

    if elem.attrib:
        for k, v in elem.attrib.items():
            node[f"@{strip_namespace(k)}"] = v

    children = list(elem)
    if children:
        child_dict = {}
        for child in children:
            child_tag = strip_namespace(child.tag)
            child_content = etree_to_dict(child)
            if child_tag in child_dict:
                if not isinstance(child_dict[child_tag], list):
                    child_dict[child_tag] = [child_dict[child_tag]]
                child_dict[child_tag].append(child_content[child_tag])
            else:
                child_dict.update(child_content)
        node.update(child_dict)

    text = elem.text.strip() if elem.text and elem.text.strip() else None
    if text:
        if node:
            node["#text"] = text
        else:
            node = text

    return {tag: node}

def convert_xml_to_json(xml_folder, output_folder):
    os.makedirs(output_folder, exist_ok=True)
    for filename in os.listdir(xml_folder):
        if filename.lower().endswith(".xml"):
            xml_path = os.path.join(xml_folder, filename)
            tree = ET.parse(xml_path)
            root = tree.getroot()
            data = etree_to_dict(root)

            json_filename = os.path.splitext(filename)[0] + ".json"
            json_path = os.path.join(output_folder, json_filename)
            with open(json_path, "w", encoding="utf-8") as f:
                json.dump(data, f, indent=2, ensure_ascii=False)
            print(f"Converted: {filename} → {json_filename}")

xml_folder = "xml"
jsont_folder = "json"

convert_xml_to_json(xml_folder, jsont_folder)
