package com.tradedoubler.billing.suite.AT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/AT_test/gb-internal-scenarios.xml",
    data   = "suite/AT_test/data/gb-internal-data.xml")
public class GB_InternalTest extends InvoicingSolutionScenario
{
}
