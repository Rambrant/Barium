package com.tradedoubler.billing.scenario;

import com.tradedoubler.billing.agent.*;
import com.tradedoubler.billing.agentcontrol.Agent;
import com.tradedoubler.billing.agentcontrol.AgentController;
import com.tradedoubler.billing.derby.DerbyServer;
import com.tradedoubler.billing.derby.dao.StartStopCommandDao;
import com.tradedoubler.billing.derby.dto.StartStopCommandDto;
import com.tradedoubler.billing.domain.dto.OrganizationDto;
import com.tradedoubler.billing.domain.generator.IdGenerator;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.generator.OrderLineBuilder;
import com.tradedoubler.billing.domain.parameter.*;
import com.tradedoubler.billing.domain.validator.AxValidator;
import com.tradedoubler.billing.domain.validator.InvoicingRuleValidator;
import com.tradedoubler.billing.domain.validator.PanOrderLinesValidator;
import com.tradedoubler.billing.domain.validator.ValidatorBase;
import com.tradedoubler.billing.domain.xml.XmlAttributeWrapper;
import com.tradedoubler.billing.domain.xml.XmlDataFileParser;
import com.tradedoubler.billing.emulation.MuleInvoicingEmulator;
import com.tradedoubler.billing.exception.ConsumerCreationFailedException;
import com.tradedoubler.billing.exception.InvoicingSolutionValidationException;
import com.tradedoubler.billing.oracle.dao.PanOrderLineDao;
import com.tradedoubler.billing.oracle.dao.SimpleDao;
import com.tradedoubler.billing.oracle.dto.PanOrderLineDto;
import com.tradedoubler.billing.util.ExecutionUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Rambrant (thore)
 */

public class InvoicingSolutionScenario extends BillingScenarioTester
{
    private static DerbyServer            database         = null;
    private static Connection             oracleConnection = null;

    private static Timestamp              testStartTime = new Timestamp( System.currentTimeMillis());
    private static IdGenerator            idGenerator;
    private static InvoicingRuleBuilder   invoicingRuleBuilder;
    private static OrderLineBuilder       orderLineBuilder;
    private static StartStopCommandDao    startStopDao;
    private static StartStopCommandDto    startStopDto;
    private static PanOrderLineDao        panOrderLineDao;
    private static AgentController        agentController;
    private static MuleInvoicingEmulator  muleInvoicingEmulator;
    private static boolean                orderLinesProcessed;

    private static InvoicingRuleValidator invoicingRuleValidator;
    private static PanOrderLinesValidator orderLinesValidator;
    private static AxValidator            axValidator;


    @Override
    public Connection getConnection()
    {
        //
        // Start the derby database and make sure it's empty
        //
        try
        {
            database = ExecutionUtil.setupDatabase();

            database.cleanDatabase();
        }
        catch( Exception e)
        {
            fail( "Database failed to start", e);
        }

        //
        // Get hold of the connection to the Oracle database and return it to the framework
        //
        try
        {
            oracleConnection = super.getConnection();

            SimpleDao.setConnection( oracleConnection);
        }
        catch( Throwable e)
        {
            LOGGER.info( "Oracle DB could not be opened: " + e.getCause().getMessage());
        }

        return oracleConnection;
    }

    @Override
    public void releaseConnection()
    {
        try
        {
            if( oracleConnection != null)
            {
                super.releaseConnection();
//                oracleConnection.close();
                oracleConnection = null;
            }

            if( database != null)
            {
                database.stop();
                database = null;
            }
        }
        catch( SQLException e)
        {
            fail( e);
        }
        catch( Exception e)
        {
            fail( "This could be totally normal since the MULE might already have taken down the database", e);
        }
    }

    @Override
    public void before()
    {
        //
        // Make a note of the time we started the test session
        //
        testStartTime = new Timestamp( System.currentTimeMillis());

        //
        // Setup the oracle data if we have a connection
        //
        if( oracleConnection != null)
        {
            try
            {
                super.before();
            }
            catch( Throwable e)
            {
                LOGGER.info( "PAN data could not be created: " + e.getCause().getMessage());
            }
        }

        //
        // Create the needed data (if necessary) in AX as if it came from CRM
        //
        OrganizationDto advertiser = getBillingData().getCustomer();

        if( advertiser != null)
        {
            //TODO xxx
        }

        //
        // Signal the start and stop for the external service agents in the MULE environment
        //
        try
        {
            startStopDao = new StartStopCommandDao();
            startStopDto = new StartStopCommandDto();

            startStopDao.create( startStopDto);
        }
        catch( SQLException e)
        {
            fail( e);
        }

        //
        // Initialize the list of producers and consumers
        //
        agentController = new AgentController();

        //
        // Setup the generation of ids used when generating data
        //
        idGenerator = new IdGenerator();

        //
        // Setup the invoice rule builder used to create the input data from CRM
        //
        XmlDataFileParser   xmlDataFileParser = new XmlDataFileParser();
        XmlAttributeWrapper wrapper           = xmlDataFileParser.parseXmlFile( getScenarioModel().getDataFilename());

        invoicingRuleBuilder = new InvoicingRuleBuilder(
            wrapper,
            idGenerator,
            getScenarioModel().getScenarioName());

        orderLineBuilder = new OrderLineBuilder(
            wrapper,
            idGenerator,
            getScenarioModel().getScenarioName());

        //
        // Setup the DB entities
        //
        panOrderLineDao = new PanOrderLineDao();

        //
        // Setup the Mule emulator
        //
        muleInvoicingEmulator = new MuleInvoicingEmulator();

        orderLinesProcessed = false;

        //
        // Setup the validators
        //
        try
        {
            invoicingRuleValidator = new InvoicingRuleValidator();
            orderLinesValidator    = new PanOrderLinesValidator( getBillingData());
            axValidator            = new AxValidator( idGenerator);
        }
        catch( SQLException e)
        {
            fail( e );
        }

    }

    @Override
    public void after()
    {
        //
        // Time to take down the external MULE stuff so set the stop status to true
        //
        startStopDto.setStopped( true);
        startStopDao.update( startStopDto);
    }

    //
    // ***** Operations *****
    //

    /**
     * Adds an agent that creates InvoiceRules according to the parameter.
     * The result is put in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addCrmCreateInvoiceRuleAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( CrmCreateInvoiceRuleAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "CrmCreateInvoiceRuleAgent already added");
                }
            }

            agentController.addAgent( new CrmCreateInvoiceRuleAgent( parameter, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that updates the invoiceRule part of an already created InvoiceRule
     * according to the parameter
     * The result is put in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addCrmUpdateInvoiceRuleAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( CrmUpdateInvoiceRuleAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "CrmUpdateInvoiceRuleAgent already added");
                }
            }

            for( Agent invoiceRuleAgent : agentController.getAgents())
            {
                if( invoiceRuleAgent.getAgentName().equals( CrmCreateInvoiceRuleAgent.class.getSimpleName()))
                {
                    agentController.addAgent( new CrmUpdateInvoiceRuleAgent(
                        parameter,
                        (CrmCreateInvoiceRuleAgent) invoiceRuleAgent,
                        invoicingRuleBuilder));

                    return;
                }
            }

            agentController.addAgent( new CrmUpdateInvoiceRuleAgent( parameter, null, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that updates the client part of an already created InvoiceRule
     * according to the parameter
     * The result is put in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addCrmUpdateClientAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( CrmUpdateClientAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "CrmUpdateClientAgent already added");
                }
            }

            agentController.addAgent( new CrmUpdateClientAgent( parameter, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that updates the agreement part of an already created InvoiceRule
     * according to the parameter
     * The result is put in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addCrmUpdateAgreementAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( CrmUpdateAgreementAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "CrmUpdateAgreementAgent already added");
                }
            }

            agentController.addAgent( new CrmUpdateAgreementAgent( parameter, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that updates or creates a market message
     * The result is put in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addCrmUpdateMarketMessageAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( CrmUpdateMarketMessageAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "CrmUpdateMarketMessageAgent already added");
                }
            }

            agentController.addAgent( new CrmUpdateMarketMessageAgent( parameter, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that creates InvoiceRules according to the parameter
     * The resulting stored in the correct Azure queue instead of in the Derby DB
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addAzureCreateInvoiceRuleAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( AzureCreateInvoiceRuleAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "AzureCreateInvoiceRuleAgent already added");
                }
            }

            agentController.addAgent( new AzureCreateInvoiceRuleAgent( parameter, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that updates the InvoiceRule part of the created InvoiceRule according to the parameter
     * The resulting stored in the correct Azure queue instead of in the Derby DB
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addAzureUpdateInvoiceRuleAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( AzureUpdateInvoiceRuleAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "AzureUpdateInvoiceRuleAgent already added");
                }
            }

            agentController.addAgent( new AzureUpdateInvoiceRuleAgent( parameter, null, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that updates the client part of the created InvoiceRule according to the parameter
     * The resulting stored in the correct Azure queue instead of in the Derby DB
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addAzureUpdateClientAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( AzureUpdateClientAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "AzureUpdateClientAgent already added");
                }
            }

            agentController.addAgent( new AzureUpdateClientAgent( parameter, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that updates the agreement part of the created InvoiceRule according to the parameter
     * The resulting stored in the correct Azure queue instead of in the Derby DB
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addAzureUpdateAgreementAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( AzureUpdateAgreementAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "AzureUpdateAgreementAgent already added");
                }
            }

            agentController.addAgent( new AzureUpdateAgreementAgent( parameter, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that updates or creates a Market Message according to the parameter
     * The resulting stored in the correct Azure queue instead of in the Derby DB
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addAzureUpdateMarketMessageAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( AzureUpdateMarketMessageAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "AzureUpdateMarketMessageAgent already added");
                }
            }

            agentController.addAgent( new AzureUpdateMarketMessageAgent( parameter, invoicingRuleBuilder));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that creates the messages that controls the invoicing in AX according to the parameter
     * The resulting stored in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addPanInvoiceControlAgent( ControlParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( PanInvoiceControlAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "PanInvoiceControlAgent already added");
                }
            }

            agentController.addAgent( new PanInvoiceControlAgent( parameter));
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that creates order lines that the invoicing in AX uses, all according to the parameter
     * The resulting stored in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addPanOrderLineAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( PanOrderLineAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "PanOrderLineAgent already added");
                }

                if( agent.getAgentName().equals( AxInvoiceOrderLinesAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "PanOrderLineAgent can't coexist with AxInvoiceOrderLinesAgent");
                }
            }

            agentController.addAgent( new PanOrderLineAgent( parameter, orderLineBuilder));

            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( PanInvoiceControlAgent.class.getSimpleName()))
                {
                    return;
                }
            }

            throw new RuntimeException( "PanOrderLineAgent need the PanInvoiceControlAgent to function!");
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that creates order lines that the invoicing in AX creates. The result from invoicing...
     * All according to the parameter
     * The resulting stored in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addAxInvoiceOrderLineAgent( ProducerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( AxInvoiceOrderLinesAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "AxInvoiceOrderLinesAgent already added");
                }

                if( agent.getAgentName().equals( PanOrderLineAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "AxInvoiceOrderLinesAgent can't coexist with PanOrderLineAgent");
                }
            }

            agentController.addAgent( new AxInvoiceOrderLinesAgent( parameter, orderLineBuilder));

            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( PanInvoiceControlAgent.class.getSimpleName()))
                {
                    return;
                }
            }

            throw new RuntimeException( "AxInvoiceOrderLinesAgent need the PanInvoiceControlAgent to function!");
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that controls the state of the Mule AxService
     * The resulting stored in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addAxServiceFailAgent( ConsumerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( AxServiceFailAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "AxServiceFailAgent already added");
                }
            }

            agentController.addAgent( new AxServiceFailAgent( parameter));
        }
        catch( ConsumerCreationFailedException e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that controls the state of the Mule CrmService
     * The resulting stored in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addCrmServiceFailAgent( ConsumerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( CrmServiceFailAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "CrmServiceFailAgent already added");
                }
            }

            agentController.addAgent( new CrmServiceFailAgent( parameter));
        }
        catch( ConsumerCreationFailedException e)
        {
            fail( e);
        }
    }

    /**
     * Adds an agent that controls the state of the Mule PanService
     * The resulting stored in the Derby DB used by the test Mule services.
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void addPanServiceFailAgent( ConsumerParameter parameter)
    {
        try
        {
            for( Agent agent : agentController.getAgents())
            {
                if( agent.getAgentName().equals( PanServiceFailAgent.class.getSimpleName()))
                {
                    throw new RuntimeException( "PanServiceFailAgent already added");
                }
            }

            agentController.addAgent( new PanServiceFailAgent( parameter));
        }
        catch( ConsumerCreationFailedException e)
        {
            fail( e);
        }
    }

    /**
     * Starts all the agents and executes according to the given parameter. The agents stores records in the
     * Derby DB, on Azure queues or in any other form that the agents support.
     * The Mule services will report back through the Derby DB
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void startEmulation( ExecutionParameter parameter)
    {
        try
        {
            //
            // Remember the current time to aid us in the validation. We need to limit the data read from
            // the database to just the ones created in this test
            //
            ValidatorBase.setBaseTimeToCurrentTime();

            //
            // Start the emulation
            //
            agentController.execute( parameter);

            LOGGER.info( "Emulation ended [" + new Date() + "]");
        }
        catch( Exception e)
        {
            LOGGER.error( "Emulation failed", e);

            fail( e);
        }
    }

    /**
     * Validates the result records. Has everything happened as it should or not?
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void validateResult( ValidationParameter parameter)
    {
        try
        {
            invoicingRuleValidator.validate( parameter);
        }
        catch( InvoicingSolutionValidationException e)
        {
            fail( e);
        }
    }

    /**
     * Validates the result in the Dynamics AX DB. Is the data as we expect it to be?
     *
     * @param parameter the parameters controlling the behaviour
     */
    public void validateAxContent( ValidationParameter parameter)
    {
        try
        {
            axValidator.validate( parameter);
        }
        catch( InvoicingSolutionValidationException e)
        {
            fail( e);
        }
    }

    /**
     * Adds a job in PAN that generates order lines for the current program.
     */
    public void addOrderLinesJobForProgram()
    {
        try
        {
            panOrderLineDao.createOrderLinesJob(
                getBillingData().getProgram().getId(),
                getBillingData().getPersonId());
        }
        catch( SQLException e)
        {
            //
            // This is ok if the error is for the legality to generate order lines for the current program
            //
            if( e.getErrorCode() != 20103)
            {
                LOGGER.info( "No order lines job inserted since this program is invoiced by PAN");
            }

            fail( e);
        }
    }

    /**
     * Starts the jobs in PAN that generates order lines according to the generated transactions.
     */
    public void processOrderLinesJobs()
    {
        try
        {
            panOrderLineDao.processOrderLinesJob();

            orderLinesProcessed = true;
        }
        catch( SQLException e)
        {
            fail( e);
        }
    }

    /**
     * A operation that Emulates the whole Mule flow involved in invoicing. PAN will think that
     * Mule is running normally. Used when testing the PAN functionality
     *
     */
    public void emulateMule()
    {
        try
        {
            muleInvoicingEmulator.emulateMuleInvoicing();
        }
        catch( SQLException e)
        {
            fail( e);
        }
    }

    /**
     * Verifies that Order Lines has been generated.
     */
    public void verifyOrderLinesExists()
    {
        try
        {
            //
            // Is the Mule emulator executed?
            //
            if( ! orderLinesProcessed)
            {
                throw new RuntimeException( "operation emulateMule must be executed prior to this check");
            }

            //
            // Get the order lines for the current program and check that they exists
            //
            Long programId = getBillingData().getProgram().getId();

            List< PanOrderLineDto> list = panOrderLineDao.getOrderLinesByProgramId( programId, testStartTime);

            if( list.size() == 0)
            {
                throw new RuntimeException( "Expected OrderLines to exist!");
            }

            //
            // Verify the order lines
            //
            orderLinesValidator.validate( list);
        }
        catch( Exception e)
        {
            fail( e);
        }
    }

    /**
     * Verifies that no Order Lines has been generated.
     */
    public void verifyOrderLinesDoNotExist()
    {
        try
        {
            //
            // Is the Mule emulator executed?
            //
            if( ! orderLinesProcessed)
            {
                throw new RuntimeException( "operation emulateMule must be executed prior to this check");
            }

            //
            // Get the order lines for the current program and check that they do not exists
            //
            Long programId =  getBillingData().getProgram().getId();

            List< PanOrderLineDto> list = panOrderLineDao.getOrderLinesByProgramId( programId, testStartTime);

            if( list.size() != 0)
            {
                throw new RuntimeException( "Did not expect OrderLines to exist!");
            }
        }
        catch( Exception e)
        {
            fail( e);
        }
    }
}
