package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.UpdateClientBase;
import com.tradedoubler.billing.derby.dao.CrmUpdateClientDao;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;

import java.sql.SQLException;

/**
 * @author Thomas Rambrant (thore)
 */

public class CrmUpdateClientAgent extends UpdateClientBase
{
    private CrmUpdateClientDao updateDao;

    public CrmUpdateClientAgent(
        ProducerParameter    parameter,
        InvoicingRuleBuilder ruleBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter, ruleBuilder);

        this.updateDao = new CrmUpdateClientDao();
    }

    @Override
    public void storeMessage( CrmMessageDto dto, String entityId)
    {
        updateDao.create( dto, entityId);
    }
}
