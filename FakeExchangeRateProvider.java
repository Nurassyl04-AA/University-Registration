package subsystemC;

public class FakeExchangeRateProvider implements ExternalExchangeRateProvider {
    private final double fixedRate;

    public FakeExchangeRateProvider(double fixedRate) {
        this.fixedRate = fixedRate;
    }

    @Override
    public double getRate() {
        return fixedRate;
    }
}
