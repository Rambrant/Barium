package com.tradedoubler.billing.suite.integration_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/integration_test/TrafficGeneratedFlow-scenario.xml",
    data  = "suite/integration_test/data/TrafficGeneratedFlow-data.xml")
public class TrafficGeneratedFlowTest extends InvoicingSolutionScenario
{
}
