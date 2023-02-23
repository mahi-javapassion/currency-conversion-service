package com.mahi.javapassion.microservices.currencyconversionservice.controller;

import com.mahi.javapassion.microservices.currencyconversionservice.bean.CurrencyConversion;
import com.mahi.javapassion.microservices.currencyconversionservice.proxy.CurrencyExchangeProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrencyFeign(@PathVariable String from, @PathVariable String to,
                                              @PathVariable BigDecimal quantity) {
        CurrencyConversion  currencyConversion = currencyExchangeProxy.getExchangeValue(from, to);
        currencyConversion.setQuantity(quantity);
        currencyConversion.setTotalCalculatedValue(currencyConversion.getConversionMultiple().multiply(quantity));
        currencyConversion.setEnvironment(currencyConversion.getEnvironment() + " Feign");
        return currencyConversion;
    }

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrency(@PathVariable String from, @PathVariable String to,
                                              @PathVariable BigDecimal quantity) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversion>  response = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversion.class, uriVariables);
        CurrencyConversion currencyConversion = response.getBody();
        currencyConversion.setQuantity(quantity);
        currencyConversion.setTotalCalculatedValue(currencyConversion.getConversionMultiple().multiply(quantity));
        currencyConversion.setEnvironment(currencyConversion.getEnvironment() + " Rest Template");
        return currencyConversion;
    }

}
