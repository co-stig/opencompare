package org.opencompare.database;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Description;
import org.opencompare.explore.ExplorationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DescriptionsExporter {

	// TODO: Initialize Configuration
	private static final String DESCRIPTION = Description.class.getSimpleName();
	
	private final JdbcDescriptionsDatabase database;
	
	public DescriptionsExporter(JdbcDescriptionsDatabase database) throws ExplorationException {
		this.database = database;
	}

	/*
	 * TODO: We should be able to specify here what exactly we want to
	 * export (e.g. only new items). As for now -- export two items with ids
	 * 3 and 5, just as a proof of concept.
	 */
	public void exportToXml(File xml) throws ExplorationException, ParserConfigurationException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		// Simulate filtering logic
		Map<Integer, Description> toExport = new HashMap<Integer, Description>();
		toExport.put(3, database.getById(3));
		toExport.put(5, database.getById(5));
		
		/*
		 * Now in toExport there are descriptions to export. We need to include
		 * their parents as well.
		 */
		Map<Integer, Description> allToExport = appendParents(toExport);
		
		/*
		 * Now in allToExport we have all objects needed to be included in
		 * resulting XML. We don't want to export description VALUES for the
		 * parent objects that were not actually changed, that's why we keep the
		 * original export list as well. Now it's time to create the XML.
		 */
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element updateElement = doc.createElement("update");
		doc.appendChild(updateElement);
		Element rootObjectElement = exportSingleObject(doc, updateElement, database.getById(1), false);
		
		// Recursively output complete objects tree to XML
		exportToXmlRecursively(doc, rootObjectElement, 1, allToExport.values(), toExport.keySet());
		
		// Output resulting XML to file
		TransformerFactory.newInstance().newTransformer().transform(
				new DOMSource(doc), 
				new StreamResult(xml)
			);
	}
	
	private void exportToXmlRecursively(Document doc, Element parentElement, int parentId, Collection<Description> all, Set<Integer> originalIds) {
		for (Description d: all) {
			if (d.getParentId() == parentId) {
				Element child = exportSingleObject(doc, parentElement, d, originalIds.contains(d.getId()));
				exportToXmlRecursively(doc, child, d.getId(), all, originalIds);
			}
		}
	}

	private Element exportSingleObject(Document doc, Element parentElement, Description description, boolean exportDescription) {
		Element newObjectElement = doc.createElement("object");
		newObjectElement.setAttribute("relativeId", description.getRelativeId());
		if (exportDescription) {
			Element newDescriptionElement = doc.createElement("description");
			newDescriptionElement.setTextContent(description.getValue());
			newObjectElement.appendChild(newDescriptionElement);
		}
		parentElement.appendChild(newObjectElement);
		return newObjectElement;
	}

	private Map<Integer, Description> appendParents(Map<Integer, Description> toExport) throws ExplorationException {
		Map<Integer, Description> all = new HashMap<Integer, Description>(toExport);
		
		while (true) {
			Set<Integer> toAdd = new HashSet<Integer>();
			
			for (Description d: all.values()) {
				int parentId = d.getParentId();
				if (parentId != 0) {
					if (!all.containsKey(parentId)) {
						toAdd.add(parentId);
					}
				}
			}
			
			if (toAdd.isEmpty()) {
				break;
			} else {
				// There's still something to add -- do it and try again.
				for (int parentId: toAdd) {
					all.put(parentId, database.getById(parentId));
				}
			}
		}
		
		return all;
	}

	//private List<Description> getAll
	
	public void importFromXml(File xml) throws ExplorationException, SAXException, IOException, ParserConfigurationException {
		importFromXml(
				DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml)
			);
	}
	
	public void importFromXml(Document doc) throws ExplorationException {
		Element update = doc.getDocumentElement();
		
		// Process a single root object
		boolean found = false;
		NodeList nodes = update.getChildNodes();		
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) node;
				String tag = el.getTagName();
				if (tag.equals("object")) {
					if (found) {
						throw new ExplorationException("Multiple root objects encountered in descriptions XML");
					} else {
						importFromXml(el, null);
						found = true;
					}
				} else {
					throw new ExplorationException("Unsupported tag encountered in descriptions XML root: " + tag);
				}
			}
		}
	}
	
	private void importFromXml(Element element, Description parent) throws ExplorationException {
		// We are inside "object" element now
		NodeList nodes = element.getChildNodes();
		String value = getDescriptionFromElement(nodes);
		String relativeId = element.getAttribute("relativeId");
		int parentId = parent == null ? 0 : parent.getId();

		Description newDesc = 
				parent == null ? 
				(Description) Configuration.getExplorableFactory(DESCRIPTION).newExplorable(DESCRIPTION, 1, parentId, relativeId, value, value.hashCode(), null) : 
				(Description) Configuration.getExplorableFactory(DESCRIPTION).newExplorable(parent, DESCRIPTION, parentId, relativeId, value);
				
		newDesc.calculateSha(parent == null ? null : parent.getTempFullId());

		Description existingDesc = database.getBySha(newDesc.getSha());
		if (existingDesc == null) { 
			database.add(newDesc);
		} else {
			/*
			 * TODO: Check timestamps and creator flags to see if we should
			 * replace the old description with the updated one. As for now
			 * we always skip it, i.e. never overwrite existing entries.
			 */
			System.out.println("Already in the database: " + existingDesc);
		}

		// Process children objects, if any
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) node;
				String tag = el.getTagName();
				if (tag.equals("object")) {
					importFromXml(el, newDesc);
				} else if (tag.equals("description")) {
					// Ignore, already processed
				} else {
					throw new ExplorationException("Unsupported tag encountered in descriptions XML: " + tag);
				}
			}
		}
	}

	private String getDescriptionFromElement(NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element) node;
				if (el.getTagName().equals("description")) {
					return el.getTextContent();
				}
			}
		}
		return null;
	}

	public static void main(String[] args) throws ExplorationException, SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		DescriptionsExporter de = new DescriptionsExporter(DatabaseManager.newDescriptionsConnection());
		de.importFromXml(new File("c:\\db\\update.xml"));
	}
}
