package subsystemC;

public class StandardFeeStrategy implements BillingStrategy {
    @Override
    public double calculate(String course) {
        return course.toLowerCase().contains("advanced") ? 50000.0 : 30000.0;
    }
}
