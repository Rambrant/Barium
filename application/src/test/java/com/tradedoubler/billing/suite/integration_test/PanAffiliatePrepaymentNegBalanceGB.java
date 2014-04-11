package com.tradedoubler.billing.suite.integration_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.BillingScenarioTester;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/integration_test/pan-affiliate-prepayment-neg-balance-scenarios-GB.xml",
    data  = "suite/integration_test/data/pan-affiliate-prepayment-neg-balance-data.xml")
public class PanAffiliatePrepaymentNegBalanceGB extends InvoicingSolutionScenario
{
}
