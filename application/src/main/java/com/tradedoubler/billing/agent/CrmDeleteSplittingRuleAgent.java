package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.DeleteSplittingRuleBase;
import com.tradedoubler.billing.derby.dao.CrmDeleteSplittingRuleDao;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;

import java.sql.SQLException;

/**
 * @author Thomas Rambrant (thore)
 */

@SuppressWarnings( { "FieldCanBeLocal"})
public class CrmDeleteSplittingRuleAgent extends DeleteSplittingRuleBase
{
    private CrmDeleteSplittingRuleDao deleteDao;

    public CrmDeleteSplittingRuleAgent(
        ProducerParameter parameter,
        InvoicingRuleBuilder ruleBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter, ruleBuilder);

        deleteDao = new CrmDeleteSplittingRuleDao();
    }

    @Override
    public void storeMessage( CrmMessageDto dto, String entityId)
    {
        deleteDao.create( dto, entityId);
    }
}
