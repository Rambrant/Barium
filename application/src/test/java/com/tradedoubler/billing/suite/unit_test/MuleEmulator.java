package com.tradedoubler.billing.suite.unit_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/unit_test/mule-emulator-scenarios.xml",
    data   = "suite/unit_test/data/mule-emulator-data.xml")
public class MuleEmulator extends InvoicingSolutionScenario
{
}
