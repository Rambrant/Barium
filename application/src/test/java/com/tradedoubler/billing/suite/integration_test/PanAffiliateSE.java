package com.tradedoubler.billing.suite.integration_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/integration_test/pan-affiliate-scenarios-SE.xml",
    data  = "suite/integration_test/data/pan-affiliate-data.xml")
public class PanAffiliateSE extends InvoicingSolutionScenario
{
}
