package com.tradedoubler.billing.domain.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.tradedoubler.billing.domain.*;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.domain.xml.XmlAttributeWrapper;
import com.tradedoubler.billing.transform.json.gson.GsonFactory;
import com.tradedoubler.billing.validate.TdJsonObject;
import com.tradedoubler.billing.validate.TdJsonSchemaReader;
import org.eel.kitchen.jsonschema.main.JsonSchema;
import org.eel.kitchen.jsonschema.main.JsonSchemaFactory;
import org.eel.kitchen.jsonschema.report.ValidationReport;
import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author Thomas Rambrant (thore)
 */

public class InvoicingRuleBuilderTest
{
    @Test
    public void validateInvoiceRuleCreatedJSon() throws ParserConfigurationException, IOException, SAXException
    {
        //
        // Create the supporting classes
        //
        XmlAttributeWrapper wrapper     = new XmlAttributeWrapper();
        IdGenerator         idGenerator = new IdGenerator();

        //
        // Create Json string and parse it
        //
        InvoicingRuleCreated invoicingRuleCreated =
            new InvoicingRuleBuilder( wrapper, idGenerator, "Scenario").buildInvoicingRuleCreated(
                ProducerEventType.NONE,
                System.currentTimeMillis());

        Gson gson = GsonFactory.getGson();

        String   jsonString         = gson.toJson( invoicingRuleCreated);
        JsonNode jsonNodeToValidate = parseJson( jsonString);

        Assert.assertNotNull( jsonNodeToValidate);

        //
        // Validate json
        //
        JsonSchema schema = loadJsonSchema( "validation/schema/billing/invoicing-rule-created-schema.json");

        Assert.assertNotNull( schema);

        ValidationReport report = schema.validate( jsonNodeToValidate);

        String resultMessage = "";

        for( String message : report.getMessages())
        {
            resultMessage = resultMessage.concat( "\n" + message);
        }

        Assert.assertTrue( resultMessage, report.isSuccess());
    }

    @Test
    public void validateInvoiceRuleUpdatedJSon()
    {
        //
        // Create the supporting classes
        //
        XmlAttributeWrapper wrapper     = new XmlAttributeWrapper();
        IdGenerator         idGenerator = new IdGenerator();

        //
        // Create Json string and parse it
        //
        InvoicingRuleUpdated invoicingRuleUpdated =
            new InvoicingRuleBuilder( wrapper, idGenerator, "Scenario").buildInvoicingRuleUpdated(
                ProducerEventType.NONE,
                System.currentTimeMillis());

        Gson gson = GsonFactory.getGson();

        String   jsonString         = gson.toJson( invoicingRuleUpdated);
        JsonNode jsonNodeToValidate = parseJson( jsonString);

        Assert.assertNotNull( jsonNodeToValidate);

        //
        // Validate json
        //
        JsonSchema schema = loadJsonSchema( "validation/schema/billing/invoicing-rule-updated-schema.json");

        Assert.assertNotNull( schema);

        ValidationReport report = schema.validate( jsonNodeToValidate);

        String resultMessage = "";

        for( String message : report.getMessages())
        {
            resultMessage = resultMessage.concat( "\n" + message);
        }

        Assert.assertTrue( resultMessage, report.isSuccess());
    }

    @Test
    public void validateClientUpdatedJSon()
    {
        //
        // Create the supporting classes
        //
        XmlAttributeWrapper wrapper     = new XmlAttributeWrapper();
        IdGenerator         idGenerator = new IdGenerator();

        //
        // Create Json string and parse it
        //
        ClientUpdated clientUpdated = new InvoicingRuleBuilder(
            wrapper,
            idGenerator,
            "Scenario").buildClientUpdated( System.currentTimeMillis(), ProducerEventType.NONE, false);

        Gson gson = GsonFactory.getGson();

        String   jsonString         = gson.toJson( clientUpdated);
        JsonNode jsonNodeToValidate = parseJson( jsonString);

        Assert.assertNotNull( jsonNodeToValidate);

        //
        // Validate json
        //
        JsonSchema schema = loadJsonSchema( "validation/schema/billing/client-updated-schema.json");

        Assert.assertNotNull( schema);

        ValidationReport report = schema.validate( jsonNodeToValidate);

        String resultMessage = "";

        for( String message : report.getMessages())
        {
            resultMessage = resultMessage.concat( "\n" + message);
        }

        Assert.assertTrue( resultMessage, report.isSuccess());
    }

    @Test
    public void validateAgreementUpdatedJSon()
    {
        //
        // Create the supporting classes
        //
        XmlAttributeWrapper wrapper     = new XmlAttributeWrapper();
        IdGenerator         idGenerator = new IdGenerator();

        //
        // Create Json string and parse it
        //
        AgreementUpdated agreementUpdated =
            new InvoicingRuleBuilder(
                wrapper,
                idGenerator,
                "Scenario").buildAgreementUpdated( System.currentTimeMillis(), ProducerEventType.NONE, false);

        Gson gson = GsonFactory.getGson();

        String   jsonString         = gson.toJson( agreementUpdated);
        JsonNode jsonNodeToValidate = parseJson( jsonString);

        Assert.assertNotNull( jsonNodeToValidate);

        //
        // Validate json
        //
        JsonSchema schema = loadJsonSchema( "validation/schema/billing/agreement-updated-schema.json");

        Assert.assertNotNull( schema);

        ValidationReport report = schema.validate( jsonNodeToValidate);

        String resultMessage = "";

        for( String message : report.getMessages())
        {
            resultMessage = resultMessage.concat( "\n" + message);
        }

        Assert.assertTrue( resultMessage, report.isSuccess());
    }

    @Test
    public void validateMarketMessageUpdatedJSon()
    {
        //
        // Create the supporting classes
        //
        XmlAttributeWrapper wrapper     = new XmlAttributeWrapper();
        IdGenerator         idGenerator = new IdGenerator();

        //
        // Create Json string and parse it
        //
        MarketMessageUpdated marketUpdated = new InvoicingRuleBuilder(
            wrapper,
            idGenerator,
            "Scenario").buildMarketMessageUpdated( System.currentTimeMillis(), ProducerEventType.NONE, false);

        Gson gson = GsonFactory.getGson();

        String   jsonString         = gson.toJson( marketUpdated);
        JsonNode jsonNodeToValidate = parseJson( jsonString);

        Assert.assertNotNull( jsonNodeToValidate);

        //
        // Validate json
        //
        JsonSchema schema = loadJsonSchema( "validation/schema/billing/market-message-updated-schema.json");

        Assert.assertNotNull( schema);

        ValidationReport report = schema.validate( jsonNodeToValidate);

        String resultMessage = "";

        for( String message : report.getMessages())
        {
            resultMessage = resultMessage.concat( "\n" + message);
        }

        Assert.assertTrue( resultMessage, report.isSuccess());
    }

    private JsonSchema loadJsonSchema( String jsonSchemaPath)
    {
        TdJsonSchemaReader jsonSchemaReader = new TdJsonSchemaReader();
        JsonSchema         schema           = null;

        try
        {
            TdJsonObject jsonSchemaObject = jsonSchemaReader.readSchema( jsonSchemaPath);
            JsonNode     schemaNode       = JsonLoader.fromString( jsonSchemaObject.toString());

            schema = JsonSchemaFactory.defaultFactory().fromSchema( schemaNode);
        }
        catch( Exception e)
        {
            Assert.fail( "Could not load JSON schema (" + jsonSchemaPath + ") to validate against: " + e.toString());
        }

        return schema;
    }

    private JsonNode parseJson( String jsonString)
    {
        JsonNode jsonNodeToValidate = null;

        try
        {
            jsonNodeToValidate = JsonLoader.fromString( jsonString);
        }
        catch( Exception e)
        {
            Assert.fail( "Could not parse message into JSON object: " + e.toString());
        }

        return jsonNodeToValidate;
    }
}
