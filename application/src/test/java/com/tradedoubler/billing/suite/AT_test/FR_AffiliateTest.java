package com.tradedoubler.billing.suite.AT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/AT_test/fr-affiliate-scenarios.xml",
    data   = "suite/AT_test/data/fr-affiliate-data.xml")
public class FR_AffiliateTest extends InvoicingSolutionScenario
{
}
