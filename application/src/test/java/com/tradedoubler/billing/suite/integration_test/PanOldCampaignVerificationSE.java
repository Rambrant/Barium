package com.tradedoubler.billing.suite.integration_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/integration_test/pan-old-campaign-verification-SE-scenarios.xml",
    data  = "suite/integration_test/data/pan-old-campaign-verification-data.xml")
public class PanOldCampaignVerificationSE extends InvoicingSolutionScenario
{
}
