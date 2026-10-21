package subsystemC;

public class BillingSelector {
    public static BillingStrategy selectStrategy(String studentType, ExternalExchangeRateProvider provider) {
        if ("HONORS".equalsIgnoreCase(studentType)) {
            return new DiscountFeeStrategy();
        } else if ("INTERNATIONAL".equalsIgnoreCase(studentType)) {
            return new ExternalAdjustedFeeStrategy(provider);
        } else {
            return new StandardFeeStrategy();
        }
    }
}
