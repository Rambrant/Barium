package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.CreateInvoiceRuleBase;
import com.tradedoubler.billing.derby.dao.CrmCreateInvoiceRuleDao;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;

import java.sql.SQLException;

/**
 * @author Thomas Rambrant (thore)
 */

public class CrmCreateInvoiceRuleAgent extends CreateInvoiceRuleBase
{
    private CrmCreateInvoiceRuleDao createDao;

    public CrmCreateInvoiceRuleAgent(
        ProducerParameter    parameter,
        InvoicingRuleBuilder ruleBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter, ruleBuilder);

        this.createDao = new CrmCreateInvoiceRuleDao();
    }

    @Override
    public CrmMessageDto storeMessage( CrmMessageDto dto, String entityId)
    {
        createDao.create( dto, entityId);

        return dto;
    }
}
