package com.tradedoubler.billing.domain.parse;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.util.Map;

/**
 * @author Thomas Rambrant (thore)
 */

public class AgreementParser extends ParserBase
{
    @Override
    public String getElementName()
    {
        return "agreement";
    }

    @Override
    public Map< String, NamedNodeMap> parse( Document root)
    {
        return parseNode( root);
    }
}
