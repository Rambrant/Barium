package com.tradedoubler.billing.suite.integration_test;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.InvoicingSolutionScenario;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model = "suite/integration_test/pan-new-campaign-verification-scenario-GB.xml",
    data  = "suite/integration_test/data/pan-new-campaign-verification-data.xml")
public class PanNewCampaignVerificationGB extends InvoicingSolutionScenario
{
}
