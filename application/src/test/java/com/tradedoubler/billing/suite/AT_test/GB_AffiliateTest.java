package com.tradedoubler.billing.suite.AT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/AT_test/gb-affiliate-scenarios.xml",
    data   = "suite/AT_test/data/gb-affiliate-data.xml")
public class GB_AffiliateTest extends InvoicingSolutionScenario
{
}
