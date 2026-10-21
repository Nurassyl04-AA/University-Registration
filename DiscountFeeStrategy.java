package subsystemC;

public class DiscountFeeStrategy implements BillingStrategy {
    @Override
    public double calculate(String course) {
        double base = course.toLowerCase().contains("advanced") ? 50000.0 : 30000.0;
        return base * 0.8;
    }
}
