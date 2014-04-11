package com.tradedoubler.billing.domain.validator;

import com.tradedoubler.billing.derby.dao.*;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.derby.dto.ErrorLogDto;
import com.tradedoubler.billing.derby.dto.EventLogDto;
import com.tradedoubler.billing.domain.parameter.ValidationParameter;
import com.tradedoubler.billing.domain.type.ValidationDepthType;
import com.tradedoubler.billing.exception.InvoicingSolutionValidationException;
import com.tradedoubler.billing.fetch.FlowName;
import com.tradedoubler.billing.type.EventLogType;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Rambrant (thore)
 */

public class InvoicingRuleValidator extends ValidatorBase
{
    private CrmCreateInvoiceRuleDao   createInvoiceDao;
    private CrmUpdateInvoiceRuleDao   updateInvoiceDao;
    private CrmUpdateAgreementDao     updateAgreementDao;
    private CrmUpdateClientDao        updateClientDao;
    private CrmUpdateMarketMessageDao updateMarketMessageDao;
    private ErrorLogDao               errorDao;
    private EventLogDao               eventDao;


    public InvoicingRuleValidator() throws SQLException
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
        // Result logs
        //
        errorDao  = new ErrorLogDao();
        eventDao  = new EventLogDao();

        //
        // Remember the parameter values
        //
    }

    public void validate( ValidationParameter parameter) throws InvoicingSolutionValidationException
    {
        printErrorLog();
        validateInputData( parameter.getDepth());

        if( parameter.getDepth() == ValidationDepthType.DEEP)
        {
            validateEventSequence( eventDao.readAllAfter( getBaseTime()));
        }
    }

    private void printErrorLog()
    {
        List< ErrorLogDto> errorList = errorDao.readAllAfter( getBaseTime());

        for( ErrorLogDto errorDto: errorList)
        {
            printFail( "[" + errorDto.getCreated() + "] " + errorDto.getMessage());
        }
    }

    private void validateInputData( ValidationDepthType depth)
    {
        verifyCrmInputData( depth, createInvoiceDao.readAllAfter( getBaseTime()));
        verifyCrmInputData( depth, updateInvoiceDao.readAllAfter( getBaseTime()));
        verifyCrmInputData( depth, updateClientDao.readAllAfter( getBaseTime()));
        verifyCrmInputData( depth, updateAgreementDao.readAllAfter( getBaseTime()));
        verifyCrmInputData( depth, updateMarketMessageDao.readAllAfter( getBaseTime()));
    }

    private void validateEventSequence( List< EventLogDto> eventLogs)
    {
        Set< FlowName> seenMessages = new HashSet< FlowName>();

        for( EventLogDto event: eventLogs)
        {
            if( ! seenMessages.contains( event.getFlowName()))
            {
                seenMessages.add( event.getFlowName());

                LOGGER.info( "***** [" + event.getFlowName() + "]");

                validateMessageEventSequence( eventDao.readAllByFlowNameAfter( event.getFlowName(),getBaseTime()));
            }
        }
    }

    private void validateMessageEventSequence( List< EventLogDto> eventLogs)
    {
        for( EventLogDto event: eventLogs)
        {
            //TODO add validation (timing impossible?)
            LOGGER.info( event);
        }
    }

    private void verifyCrmInputData( ValidationDepthType depth, List< CrmMessageDto> crmMessages)
    {
        for( CrmMessageDto dto: crmMessages)
        {
            //
            // Check the sent and delete status
            //
            validateCrmSentAndDeleteStatus( dto);

            if( depth == ValidationDepthType.DEEP)
            {
                //
                // Check the send and delete times
                //
                if( dto.isSent())
                {
                    //TODO
//                    validateFalse(
//                        "Message sent during disturbance! " + dto,
//                        failDao.isInFailState( dto.getIntegrationPointId(), dto.getSendTime().getTime()));
                }

                if( dto.isDeleted())
                {
//                    validateFalse(
//                        "Message deleted during disturbance! " + dto,
//                        failDao.isInFailState( dto.getIntegrationPointId(), dto.getDeleteTime().getTime()));
                }
            }
        }
    }

    private void validateCrmSentAndDeleteStatus( CrmMessageDto dto)
    {
        if( dto.isSent())
        {
            switch( dto.getEventType())
            {
            case NONE:
            case SEQUENCE:
            case ORDER:
                //
                // These should be deleted since they are treated as ok or is thrown away as "old news"
                //
                validateTrue(
                    "message should be deleted: " + dto,
                    dto.isDeleted());

                break;

            case SYNTAX:
            case ILLEGAL:

                //
                // These should not be deleted since they shall never reach any output points
                //
                validateFalse(
                    "message should not be deleted: " + dto,
                    dto.isDeleted());

                break;
            }
        }
        else
        {
            //
            // There should be only disturbance event records for messages we never sent...
            //
            for( EventLogDto eventDto: eventDao.readAllByMessageId( dto.getMessageId()))
            {
                if( eventDto.getEventType() != EventLogType.DISTURB)
                {
                    printFail(
                        "Illegal event type for a non sent message: " + eventDto.getEventType() + " (" + dto + ")");
                }
            }
        }
    }
}
