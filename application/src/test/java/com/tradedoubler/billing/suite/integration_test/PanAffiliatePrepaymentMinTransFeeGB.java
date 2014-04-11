package com.tradedoubler.billing.suite.integration_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.BillingScenarioTester;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/integration_test/pan-affiliate-prepayment-min-trans-fee-scenario-GB.xml",
    data  = "suite/integration_test/data/pan-affiliate-prepayment-min-trans-fee-data.xml")
public class PanAffiliatePrepaymentMinTransFeeGB extends InvoicingSolutionScenario
{
}
