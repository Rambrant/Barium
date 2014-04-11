package com.tradedoubler.billing.suite.unit_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/unit_test/process-order-line-jobs-scenarios.xml",
    data   = "suite/unit_test/data/process-order-line-jobs-data.xml" )
public class ProcessOrderLineJobs extends InvoicingSolutionScenario
{
}
