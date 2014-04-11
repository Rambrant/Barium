package com.tradedoubler.billing.suite.platform_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/platform_test/platform-load-test-scenario.xml",
    data  = "suite/platform_test/data/platform-load-test-data.xml")
public class PlatformLoadTest extends InvoicingSolutionScenario
{
}