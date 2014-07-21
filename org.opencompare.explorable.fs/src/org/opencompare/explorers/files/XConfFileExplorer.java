package org.opencompare.explorers.files;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParserFactory;

import org.opencompare.explorable.Configuration;
import org.opencompare.explorable.Explorable;
import org.opencompare.explorable.ExplorableFactory;
import org.opencompare.explorable.RootFactory;
import org.opencompare.explorable.files.XConfFile;
import org.opencompare.explore.ExplorationException;
import org.opencompare.explorers.Explorer;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XConfFileExplorer implements Explorer {

    private static final SAXParserFactory parserFactory = SAXParserFactory.newInstance();     // Hopefully SAXParserFactory is multithreaded
    static {
        parserFactory.setValidating(false);   // This doesn't seem to make much difference, thus overriding resolveEntity below to handle "missing DTD" exception
    }
    
    private static class XConfParser extends DefaultHandler {
        private final XConfFile file;
        
        private String currentParent = null;
        private int configurationRefs = 0;
        private int propagationActions = 0;
        private int classPathEntries = 0;

        
        private XConfParser(XConfFile file) {
            this.file = file;
        }

        private String getAttributeValue(Attributes atts, String name) {
            for (int i = 0; i < atts.getLength(); ++i) {
                if (atts.getQName(i).equals(name)) {
                    return atts.getValue(i);
                }
            }
            return null;
        }
        
        public void startElement(String namespaceURI, String localName, String tag, Attributes atts) throws SAXException {
            try {
                if (tag.equalsIgnoreCase("Configuration")) {
                    // For Configuration tag we will output only its attributes
                    String targetFile = getAttributeValue(atts, "targetFile");
                    if (targetFile != null) {
                    	Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, "@targetFile", targetFile);
                    }
                    String serviceProvider = getAttributeValue(atts, "serviceProvider");
                    if (serviceProvider != null) {
                    	Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, "@serviceProvider", serviceProvider);
                    }
                } else if (
                        tag.equalsIgnoreCase("Property") || 
                        tag.equalsIgnoreCase("AddToProperty") || 
                        tag.equalsIgnoreCase("RemoveFromProperty") || 
                        tag.equalsIgnoreCase("ResetProperty") ||
                        tag.equalsIgnoreCase("UndefineProperty") ||
                        tag.equalsIgnoreCase("Resource") ||
                        tag.equalsIgnoreCase("Service")) {
                    // Those are named tags, copy all attributes as separate properties
                    String name = getAttributeValue(atts, "name");
                    if (name != null) {
                        String namePrefix = tag + " " + name;
                        for (int i = 0; i < atts.getLength(); ++i) {
                            String attName = atts.getQName(i);
                            if (!attName.equals("name")) {
                            	Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, namePrefix + " @" + attName, atts.getValue(i));
                            }
                        }
                        if (tag.equalsIgnoreCase("Resource") || tag.equalsIgnoreCase("Service")) {
                            currentParent = namePrefix;
                        }
                    }
                } else if (tag.equalsIgnoreCase("Option")) {
                    // This is a child of service or resource, its name is defined by selector
                    String name = getAttributeValue(atts, "selector");
                    if (name != null && currentParent != null) {
                        for (int i = 0; i < atts.getLength(); ++i) {
                            String attName = atts.getQName(i);
                            if (!attName.equals("selector")) {
                            	Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, currentParent + " Option " + name + " @" + attName, atts.getValue(i));
                            }
                        }
                    }
                } else if (tag.equalsIgnoreCase("ConfigurationRef")) {
                    String value = getAttributeValue(atts, "href");
                    if (value != null) {
                        ++configurationRefs;
                        Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, "ConfigurationRef " + configurationRefs, value);
                    }
                } else if (tag.equalsIgnoreCase("PropagationAction")) {
                    String value = getAttributeValue(atts, "className");
                    if (value != null) {
                        ++propagationActions;
                        classPathEntries = 0;
                        String name = "PropagationAction " + propagationActions;
                        Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, name, value);
                        currentParent = name;
                    }
                } else if (tag.equalsIgnoreCase("ClassPathEntry")) {
                    if (currentParent != null) {
                        ++classPathEntries;
                        String namePrefix = currentParent + " ClassPathEntry " + classPathEntries;
                        String dir = getAttributeValue(atts, "dir");
                        if (dir != null) {
                        	Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, namePrefix + " @dir", dir);
                        }
                        String fileAtt = getAttributeValue(atts, "file");
                        if (fileAtt != null) {
                        	Configuration.enqueue(file, RootFactory.TYPE_PROPERTY, namePrefix + " @file", fileAtt);
                        }
                    }
                }
            
            } catch (Exception ex) {
            	throw new SAXException(ex);
			}
        }
        
        public InputSource resolveEntity(String publicId, String systemId) {
            return new InputSource(
                    new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes())
                );
        }
    }

    @Override
    public void explore(Explorable what, ExplorableFactory factory) throws ExplorationException {
        try {
        	XConfFile file = (XConfFile) what;
            XConfParser handler = new XConfParser(file);
            parserFactory.newSAXParser().parse(file.getPath(), handler);
        } catch (Exception ex) {
            throw new ExplorationException(ex);
        }
    }
}
