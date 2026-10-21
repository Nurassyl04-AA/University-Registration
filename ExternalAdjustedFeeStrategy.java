package subsystemC;

public class ExternalAdjustedFeeStrategy implements BillingStrategy {
    private final ExternalExchangeRateProvider rateProvider;

    public ExternalAdjustedFeeStrategy(ExternalExchangeRateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    @Override
    public double calculate(String course) {
        double base = course.toLowerCase().contains("advanced") ? 50000.0 : 30000.0;
        return base * rateProvider.getRate();
    }
}