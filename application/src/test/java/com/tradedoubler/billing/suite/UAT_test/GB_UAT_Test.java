package com.tradedoubler.billing.suite.UAT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/UAT_test/gb-uat-scenarios.xml",
    data   = "suite/UAT_test/data/gb-uat-data.xml")
public class GB_UAT_Test extends InvoicingSolutionScenario
{
}
