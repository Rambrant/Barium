package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.UpdateAgreementBase;
import com.tradedoubler.billing.derby.dao.CrmUpdateAgreementDao;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;

import java.sql.SQLException;

/**
 * @author Thomas Rambrant (thore)
 */

public class CrmUpdateAgreementAgent extends UpdateAgreementBase
{
    private CrmUpdateAgreementDao updateDao;

    public CrmUpdateAgreementAgent(
        ProducerParameter    parameter,
        InvoicingRuleBuilder ruleBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter, ruleBuilder);

        this.updateDao = new CrmUpdateAgreementDao();
    }

    @Override
    public void storeMessage( CrmMessageDto dto, String entityId)
    {
        updateDao.create( dto, entityId);
    }
}
