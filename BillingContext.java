package subsystemC;

public class BillingContext {
    private BillingStrategy strategy;

    public BillingContext(BillingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(BillingStrategy strategy) {
        this.strategy = strategy;
    }

    public double executeCalculate(String course) {
        return strategy.calculate(course);
    }
}
