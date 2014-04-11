package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentcontrol.Agent;
import com.tradedoubler.billing.derby.dao.InvoicingCommandDao;
import com.tradedoubler.billing.domain.parameter.ControlParameter;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;
import com.tradedoubler.billing.type.InvoicingStatusType;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanInvoiceControlAgent extends Agent
{
    protected static final Logger LOGGER  = Logger.getLogger( PanInvoiceControlAgent.class);

    private ControlParameter    parameter;
    private InvoicingCommandDao commandDao;
    private long                currentDelay;
    private long                beatCounter = 0L;

    public PanInvoiceControlAgent( ControlParameter parameter) throws ProducerCreationFailedException, SQLException
    {
        super();

        this.parameter    = parameter;
        this.commandDao   = new InvoicingCommandDao();
        this.currentDelay = parameter.getStartDelay();
    }

    @Override
    protected void before() throws Exception
    {
        commandDao.create( InvoicingStatusType.STOPPED, true);
    }

    @Override
    protected void after() throws Exception
    {
    }

    //
    // The method that is called by the AgentController at second interval.
    //
    @Override
    public void execute() throws Exception
    {
        ++beatCounter;

        if( beatCounter > currentDelay)
        {
            if( commandDao.isStopped())
            {
                LOGGER.info(
                    "***== Invoicing started: " + new Date());

                commandDao.create( InvoicingStatusType.STARTED, parameter.getTriggerAx());

                currentDelay = parameter.getPeriodicity();
            }
            else
            {
                throw new RuntimeException( "Invoicing started to soon. Please specify a longer startDelay");
            }

            beatCounter = 0L;
        }
    }
}
