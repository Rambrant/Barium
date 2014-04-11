package com.tradedoubler.billing.suite.unit_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/unit_test/x-unit-invoicingsolution-scenario.xml",
    data  = "suite/unit_test/data/x-unit-invoicingsolution-data.xml")
public class xInvoicingSolutionTest extends InvoicingSolutionScenario
{
}
