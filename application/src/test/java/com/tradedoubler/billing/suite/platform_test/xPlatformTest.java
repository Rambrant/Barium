package com.tradedoubler.billing.suite.platform_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/platform_test/x-unit-invoicingsolution-scenario.xml",
    data  = "suite/platform_test/data/x-unit-invoicingsolution-data.xml")
public class xPlatformTest extends InvoicingSolutionScenario
{
}
