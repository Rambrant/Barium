package com.tradedoubler.billing.suite.UAT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/UAT_test/at-uat-scenarios.xml",
    data   = "suite/UAT_test/data/at-uat-data.xml")
public class AT_UAT_Test extends InvoicingSolutionScenario
{
}
