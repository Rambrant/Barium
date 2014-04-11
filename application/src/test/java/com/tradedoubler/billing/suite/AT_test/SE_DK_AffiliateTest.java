package com.tradedoubler.billing.suite.AT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/AT_test/se_dk-affiliate-scenarios.xml",
    data   = "suite/AT_test/data/se_dk-affiliate-data.xml")
public class SE_DK_AffiliateTest extends InvoicingSolutionScenario
{
}
