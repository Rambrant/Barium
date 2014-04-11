package com.tradedoubler.billing.suite.UAT_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/UAT_test/de-uat-scenarios.xml",
    data   = "suite/UAT_test/data/de-uat-data.xml")
public class DE_UAT_Test extends InvoicingSolutionScenario
{
}
