package app.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
public class ExchangeRate {
    private int id;

    private Currency baseCurrency;

    private Currency targetCurrency;

    private BigDecimal rate;

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
