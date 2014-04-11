package com.tradedoubler.billing.suite.AT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/AT_test/gb-integral-scenarios.xml",
    data   = "suite/AT_test/data/gb-integral-data.xml")
public class GB_IntegralTest extends InvoicingSolutionScenario
{
}
