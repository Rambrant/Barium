package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.UpdateMarketMessageBase;
import com.tradedoubler.billing.derby.dao.CrmUpdateMarketMessageDao;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;

import java.sql.SQLException;

/**
 * @author Thomas Rambrant (thore)
 */

public class CrmUpdateMarketMessageAgent extends UpdateMarketMessageBase
{
    private CrmUpdateMarketMessageDao updateDao;

    public CrmUpdateMarketMessageAgent(
        ProducerParameter    parameter,
        InvoicingRuleBuilder ruleBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter, ruleBuilder);

        this.updateDao = new CrmUpdateMarketMessageDao();
    }

    @Override
    public void storeMessage( CrmMessageDto dto, String entityId)
    {
        updateDao.create( dto, entityId);
    }
}
