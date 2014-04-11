package com.tradedoubler.billing.domain.validator;

import com.google.gson.Gson;
import com.tradedoubler.billing.derby.dao.*;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.*;
import com.tradedoubler.billing.domain.generator.IdGenerator;
import com.tradedoubler.billing.domain.parameter.ValidationParameter;
import com.tradedoubler.billing.domain.type.ValidationDepthType;
import com.tradedoubler.billing.exception.AxValidationException;
import com.tradedoubler.billing.exception.InvoicingSolutionValidationException;
import com.tradedoubler.billing.service.ax.AxComparator;
import com.tradedoubler.billing.transform.json.gson.GsonFactory;
import com.tradedoubler.billing.util.AxDbConstantsUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Rambrant (thore)
 */

public class AxValidator extends ValidatorBase
{
    private CrmCreateInvoiceRuleDao   createInvoiceDao;
    private CrmUpdateInvoiceRuleDao   updateInvoiceDao;
    private CrmUpdateAgreementDao     updateAgreementDao;
    private CrmUpdateClientDao        updateClientDao;
    private CrmUpdateMarketMessageDao updateMarketMessageDao;
    private IdGenerator               idGenerator;
    private AxComparator              axComparator;
    private Gson                      gson;


    public AxValidator( IdGenerator idGenerator) throws SQLException
    {
        //
        // CRM input "queues"
        //
        createInvoiceDao       = new CrmCreateInvoiceRuleDao();
        updateInvoiceDao       = new CrmUpdateInvoiceRuleDao();
        updateAgreementDao     = new CrmUpdateAgreementDao();
        updateClientDao        = new CrmUpdateClientDao();
        updateMarketMessageDao = new CrmUpdateMarketMessageDao();

        //
        // Helpers
        //
        gson         = GsonFactory.getGson();
        axComparator = new AxComparator();

        //
        // Remember the parameter values
        //
        this.idGenerator = idGenerator;

        ApplicationContext ctx = new ClassPathXmlApplicationContext(
            "spring/springPropertiesContext-barium.xml",
            "spring/springServicesContext-barium-realAx.xml",
            "spring/springBeansContext.xml"
        );

        ctx.getAutowireCapableBeanFactory().autowireBean( axComparator);
    }

    public void validate( ValidationParameter parameter) throws InvoicingSolutionValidationException
    {
        if( parameter.getDepth() == ValidationDepthType.DEEP)
        {
            //
            // validate the invoicing rules and all the parts, Client, Agreement and invoicingRule
            //
            validateInvoicingRules( idGenerator);

            //
            // Validate the other integrations that isn't connected to the invoicing rule
            //
            validateMarketMessage( idGenerator);
        }
    }

    private void validateInvoicingRules( IdGenerator idGenerator)
    {
        //
        // Loop through all InvoicingRules. They are the base for all updated data associated with these rules
        //
        LOGGER.info( "Validating " + idGenerator.getAllInvoiceRuleIds().size() + " InvoicingRules that has been created");

        for( String ruleId: idGenerator.getAllInvoiceRuleIds())
        {
            //
            // Read the created data (There should be only one found)
            //
            List< CrmMessageDto> createdRules = createInvoiceDao.readByEntityId( ruleId);

            validateTrue( "Every created Invoicing Rule shall have an unique id", createdRules.size() < 2);

            InvoicingRuleCreated expandedRule = null;

            if( createdRules.size() != 0)
            {
                CrmMessageDto createdRule = createdRules.get( 0);

                expandedRule = gson.fromJson( createdRule.getJsonMessage(), InvoicingRuleCreated.class);

                validateTrue( "All creations of InvoicingRules must have been treated", createdRule.isDeleted());
            }

            //
            // validate the invoicing rule part
            //
            validateInvoicingRulePart( ruleId, expandedRule);

            //
            // Validate the client part
            //
            String clientId = (expandedRule == null) ? AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID : expandedRule.getClient().getClientId();

            validateClientPart( clientId, expandedRule);

            //
            // Validate the agreement part
            //
            String agreementId = (expandedRule == null) ? AxDbConstantsUtil.EXISTING_AGREEMENT_ID : expandedRule.getAgreement().getSourceSystemAgreementId();

            validateAgreementPart( agreementId, expandedRule);
        }
    }

    private void validateMarketMessage( IdGenerator idGenerator)
    {
        Map< String, List< String>> map = idGenerator.getAllInvoiceMessageRuleForMarketsIds();

        LOGGER.info( "Validating " + idGenerator.getAllInvoiceMessageRuleForMarketsIds().size() + " MarketMessages that has been created");

        for( String key: map.keySet())
        {
            for( String marketMessageId: map.get( key))
            {
                CrmMessageDto updatedMessage = updateMarketMessageDao.readLastByEntityId( marketMessageId);

                if( updatedMessage != null)
                {
                    //
                    // There was an update of the market message. This must represent the latest values
                    //
                    validateTrue( "All updates of MarketMessages must have been treated", updatedMessage.isDeleted());

                    MarketMessageUpdated expandedMessage = gson.fromJson( updatedMessage.getJsonMessage(), MarketMessageUpdated.class);

                    validateMarketMessageInAx( expandedMessage.getInvoiceMessageRule());
                }
            }
        }
    }

    private void validateInvoicingRulePart( String ruleId, InvoicingRuleCreated createdRule)
    {
        CrmMessageDto updatedRule = updateInvoiceDao.readLastByEntityId( ruleId);

        if( updatedRule != null)
        {
            //
            // There was an update for this part of the created invoicing rule. This must represent the latest values
            //
            validateTrue( "All updates of InvoicingRules must have been treated", updatedRule.isDeleted());

            InvoicingRuleUpdated expandedRule = gson.fromJson( updatedRule.getJsonMessage(), InvoicingRuleUpdated.class);

            validateInvoicingRuleInAx( expandedRule.getInvoicingRule());
        }
        else
        {
            if( createdRule != null)
            {
                //
                // We have created an invoicing rule
                //
                validateInvoicingRuleInAx( createdRule.getInvoicingRule());
            }
        }
    }

    private void validateClientPart( String clientId, InvoicingRuleCreated createdRule)
    {
        CrmMessageDto updatedClient = updateClientDao.readLastByEntityId( clientId);

        if( updatedClient != null)
        {
            //
            // There was an update for this part of the created invoicing rule. This must represent the latest values
            //
            validateTrue( "All updates of Clients must have been treated", updatedClient.isDeleted());

            ClientUpdated expandedClient = gson.fromJson( updatedClient.getJsonMessage(), ClientUpdated.class);

            validateClientInAx( expandedClient.getClient() );
        }
        else
        {
            if( createdRule != null)
            {
                //
                // We have created an invoicing rule
                //
                validateClientInAx( createdRule.getClient() );
            }
        }
    }

    private void validateAgreementPart( String agreementId, InvoicingRuleCreated createdRule)
    {
        CrmMessageDto updatedAgreement = updateAgreementDao.readLastByEntityId( agreementId);

        if( updatedAgreement != null)
        {
            //
            // There was an update for this part of the created invoicing rule. This must represent the latest values
            //
            validateTrue( "All updates of Agreements must have been treated", updatedAgreement.isDeleted());

            AgreementUpdated expandedAgreement = gson.fromJson( updatedAgreement.getJsonMessage(), AgreementUpdated.class);

            validateAgreementInAx( expandedAgreement.getAgreement());
        }
        else
        {
            if( createdRule != null)
            {
                //
                // We have created an invoicing rule
                //
                validateAgreementInAx( createdRule.getAgreement());
            }
        }
    }

    private void validateInvoicingRuleInAx( InvoicingRule invoicingRule)
    {
        try
        {
            axComparator.validateInvoicingRule( invoicingRule);
        }
        catch( AxValidationException e)
        {
            printErrors( e);
        }
    }

    private void validateClientInAx( Client client)
    {
        try
        {
            axComparator.validateClient( client );
        }
        catch( AxValidationException e)
        {
            printErrors( e);
        }
    }

    private void validateAgreementInAx( Agreement agreement)
    {
        try
        {
            axComparator.validateAgreement( agreement );
        }
        catch( AxValidationException e)
        {
            printErrors( e);
        }
    }

    private void validateMarketMessageInAx( InvoiceIssuerMessageRule invoiceMessageRule)
    {
        try
        {
            axComparator.validateMarketMessage( invoiceMessageRule );
        }
        catch( AxValidationException e)
        {
            printErrors( e);
        }
    }

    private void printErrors( AxValidationException e)
    {
        printFail( e.getMessage());

        for( String message: e.getErrors())
        {
            printFail( "\t" + message);
        }
    }
}
