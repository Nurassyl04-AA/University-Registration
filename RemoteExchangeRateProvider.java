package subsystemC;

public class RemoteExchangeRateProvider implements ExternalExchangeRateProvider {
    @Override
    public double getRate() {
        return 1.0; 
    }
}
