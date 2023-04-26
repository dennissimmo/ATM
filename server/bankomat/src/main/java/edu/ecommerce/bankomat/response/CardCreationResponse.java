package edu.ecommerce.bankomat.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CardCreationResponse {
    private String PIN;
    private String CVV;
    private double balance;
    private String accountNumber;

}
