package com.tradedoubler.billing.suite.AT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/AT_test/de-integral-scenarios.xml",
    data   = "suite/AT_test/data/de-integral-data.xml")
public class DE_IntegralTest extends InvoicingSolutionScenario
{
}
