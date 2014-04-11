package com.tradedoubler.billing.suite.integration_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/integration_test/MarketMessageUpdatedFlow-scenario.xml",
    data  = "suite/integration_test/data/MarketMessageUpdatedFlow-data.xml")
public class MarketMessageUpdatedFlowTest extends InvoicingSolutionScenario
{
}
