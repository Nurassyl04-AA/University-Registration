import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subsystemC.*;

import static org.junit.jupiter.api.Assertions.*;

class BillingStrategyTest {

    @Test
    void testStandardFeeStrategy() {
        BillingStrategy strategy = new StandardFeeStrategy();
        assertEquals(50000.0, strategy.calculate("Advanced Java"));
        assertEquals(30000.0, strategy.calculate("Basic Python"));
    }

    @Test
    void testDiscountFeeStrategy() {
        BillingStrategy strategy = new DiscountFeeStrategy();
        assertEquals(40000.0, strategy.calculate("Advanced Java"));
        assertEquals(24000.0, strategy.calculate("Basic Python"));
    }

    @Test
    void testExternalAdjustedFeeStrategy() {
        ExternalExchangeRateProvider fakeProvider = new FakeExchangeRateProvider(1.1);
        BillingStrategy strategy = new ExternalAdjustedFeeStrategy(fakeProvider);
        // Добавили погрешность 0.0001 третьим аргументом, чтобы микро-погрешность double не мешала тесту
        assertEquals(55000.0, strategy.calculate("Advanced Java"), 0.0001);
    }

    @Test
    void testSelectorRules() {
        ExternalExchangeRateProvider provider = new FakeExchangeRateProvider(1.0);

        BillingStrategy honors = BillingSelector.selectStrategy("HONORS", provider);
        assertTrue(honors instanceof DiscountFeeStrategy);

        BillingStrategy standard = BillingSelector.selectStrategy("REGULAR", provider);
        assertTrue(standard instanceof StandardFeeStrategy);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Advanced Java", "Basic Python", "Data Structures", "Web Development"})
    void parameterizedTestNonNegativeFee(String course) {
        BillingStrategy[] strategies = {
                new StandardFeeStrategy(),
                new DiscountFeeStrategy(),
                new ExternalAdjustedFeeStrategy(new FakeExchangeRateProvider(0.9))
        };

        for (BillingStrategy strategy : strategies) {
            double fee = strategy.calculate(course);
            assertTrue(fee >= 0, "Fee should never be negative for course: " + course);
        }
    }
}