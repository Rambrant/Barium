package com.tradedoubler.billing.suite.integration_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/integration_test/pan-affiliate-min-trans-fee-scenario-GB.xml",
    data   = "suite/integration_test/data/pan-affiliate-min-trans-fee-data.xml")
public class PanAffiliateMinTransFeeGB extends InvoicingSolutionScenario
{
}
