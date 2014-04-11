package com.tradedoubler.billing.suite.generate_data;

import com.tradedoubler.billing.annotaition.ScenarioDefinition;
import com.tradedoubler.billing.scenario.BillingScenarioTester;

/**
 * @author Thomas Rambrant (thore)
 */

@ScenarioDefinition(
    model  = "suite/generate_data/market-orderlines-country-scenarios.xml",
    data   = "suite/generate_data/data/market-orderlines-denmark-data.xml" )
public class MarketInvoicesDenmarkTest extends BillingScenarioTester
{
}
