package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentcontrol.Consumer;
import com.tradedoubler.billing.derby.dao.FailStateDao;
import com.tradedoubler.billing.domain.parameter.ConsumerParameter;
import com.tradedoubler.billing.exception.ConsumerCreationFailedException;
import com.tradedoubler.billing.service.pan.PanService;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanServiceFailAgent extends Consumer
{
    private FailStateDao failDao;

    public PanServiceFailAgent( ConsumerParameter parameter) throws ConsumerCreationFailedException
    {
        super( parameter);
    }

    @Override
    protected void before() throws Exception
    {
        failDao = new FailStateDao();
    }

    @Override
    protected void after() throws Exception
    {
        //
        // Set the service to a normal non-fail state
        //
        failDao.create( PanService.class.getSimpleName(), false);
    }

    @Override
    protected void handleFailStatusChange( boolean failState)
    {
        failDao.create( PanService.class.getSimpleName(), failState);
    }
}
