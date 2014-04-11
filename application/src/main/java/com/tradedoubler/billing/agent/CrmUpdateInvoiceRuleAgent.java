package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.UpdateInvoiceRuleBase;
import com.tradedoubler.billing.derby.dao.CrmUpdateInvoiceRuleDao;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;

import java.sql.SQLException;

/**
 * @author Thomas Rambrant (thore)
 */

public class CrmUpdateInvoiceRuleAgent extends UpdateInvoiceRuleBase
{
    private CrmUpdateInvoiceRuleDao   updateDao;

    public CrmUpdateInvoiceRuleAgent(
        ProducerParameter         parameter,
        CrmCreateInvoiceRuleAgent createAgent,
        InvoicingRuleBuilder      ruleBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter, createAgent, ruleBuilder);

        this.updateDao = new CrmUpdateInvoiceRuleDao();
    }

    @Override
    public void storeMessage( CrmMessageDto dto, String entityId)
    {
         updateDao.create( dto, entityId);
    }
}
