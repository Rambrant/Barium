package com.tradedoubler.billing.domain.parse;

import com.tradedoubler.billing.exception.XmlFileDataException;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Thomas Rambrant (thore)
 */

public class ParserBaseTest
{
    public class TestParser extends ParserBase
    {
        @Override
        public String getElementName()
        {
            return "entity";
        }

        @Override
        public Map< String, NamedNodeMap> parse( Document root)
        {
            return parseNode( root);
        }
    }

    @Test
    public void parseEntity() throws ParserConfigurationException, IOException, SAXException
    {
        String xml =
            "<entities>" +
            "    <entity scenarioName = \"Simple\"" +
            "        marketId = \"Nisse\""          +
            "    />"                                +
            "    <entity scenarioName = \"Double\"" +
            "        marketId = \"Pelle\""          +
            "    />" +
            "</entities>";

	    //
	    // convert String into InputStream
        //
        InputStream is = new ByteArrayInputStream( xml.getBytes());

        //
        // Create the XML document
        //
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        Document doc = dbf.newDocumentBuilder().parse( is);

        //
        // Parse the XML document
        //
        TestParser parser = new TestParser();

        Map< String, NamedNodeMap> map = parser.parse( doc);

        Assert.assertNotNull( "There should be nodes", map);
        Assert.assertEquals(  "There should be 2 nodes", 2, map.size());

        CheckNode( "Simple", "marketId", "Nisse", map);
        CheckNode( "Double", "marketId", "Pelle", map);
    }

    @Test
    public void parseEntityNoEntities() throws ParserConfigurationException, IOException, SAXException
    {
        String xml =
            "<entities>" +
            "</entities>";

	    //
	    // convert String into InputStream
        //
        InputStream is = new ByteArrayInputStream( xml.getBytes());

        //
        // Create the XML document
        //
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        Document doc = dbf.newDocumentBuilder().parse( is);

        //
        // Parse the XML document
        //
        TestParser parser = new TestParser();

        Map< String, NamedNodeMap> map = parser.parse( doc);

        Assert.assertNotNull( "There should be nodes", map);
        Assert.assertEquals(  "There should not be any nodes", 0, map.size());
    }

    @Test( expected = XmlFileDataException.class)
    public void parseEntityNoScenario() throws ParserConfigurationException, IOException, SAXException
    {
        String xml =
            "<entities>" +
            "    <entity" +
            "        marketId = \"Nisse\""          +
            "    />"                                +
            "</entities>";

	    //
	    // convert String into InputStream
        //
        InputStream is = new ByteArrayInputStream( xml.getBytes());

        //
        // Create the XML document
        //
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        Document doc = dbf.newDocumentBuilder().parse( is);

        //
        // Parse the XML document
        //
        TestParser parser = new TestParser();

        parser.parse( doc);
    }

    private void CheckNode( String key, String attributeName, String value, Map<String, NamedNodeMap> map)
    {
        NamedNodeMap nodeMap = map.get( key);

        Assert.assertNotNull( "Scenario '" + key + "' should exist", nodeMap);
        Assert.assertEquals(  "There should be just one attribute", 1, nodeMap.getLength());
        Assert.assertEquals(  "Wrong attribute name", attributeName, nodeMap.item( 0).getNodeName());
        Assert.assertEquals(  "Wrong attribute value", value, nodeMap.item(  0).getNodeValue());
    }
}
