package com.tradedoubler.billing.domain.parse;

import com.tradedoubler.billing.exception.XmlFileDataException;
import com.tradedoubler.billing.utils.ParserUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Rambrant (thore)
 */

public abstract class ParserBase
{
    protected final Map< String, NamedNodeMap> nodeMap = new HashMap< String, NamedNodeMap>();

    //
    // Return the name of the element, Eg agreement, invoiceRule...
    //
    public abstract String getElementName();

    //
    // Parse the node. A typical implementation just calls parseNode
    //
    public abstract Map< String, NamedNodeMap> parse( Document root);

    //
    // helper method, called by the public parse method
    //
    protected Map< String, NamedNodeMap> parseNode( Document root)
    {
        NodeList nodes;

        //
        // Get all nodes with the given name
        //
        nodes = root.getElementsByTagName( getElementName());

        //
        // Loop over them and add them to the map
        //
        if( ParserUtil.isNotEmpty( nodes))
        {
            for( int index = 0; index < nodes.getLength(); index++)
            {
                Node item = nodes.item( index);

                NamedNodeMap attributes = item.getAttributes();

                //
                // The node is an element and it has some attributes
                //
                if( item.getNodeType() == Node.ELEMENT_NODE &&
                    (attributes != null && attributes.getLength() != 0))
                {
                    //
                    // Extract the scenarioName and use it as a key to the map
                    //
                    Node scenarioNode = attributes.getNamedItem( "scenarioName");

                    if( scenarioNode == null)
                    {
                        throw new XmlFileDataException( "No scenarioName specified on node '" + getElementName() + "'");
                    }

                    String scenarioName = scenarioNode.getNodeValue();

                    attributes.removeNamedItem( "scenarioName");

                    //
                    // Store the attributes with the extracted scenario
                    //
                    nodeMap.put( scenarioName, attributes);
                }
            }
        }

        return nodeMap;
    }
}
