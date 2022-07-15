package com.learn.microservices.camelmicroservicesb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExchangeMapping {
    private Long id;
    private String from;
    private String to;
    private BigDecimal conversionMultiple;
}
