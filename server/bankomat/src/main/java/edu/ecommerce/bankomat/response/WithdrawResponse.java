package edu.ecommerce.bankomat.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WithdrawResponse {
    private boolean status;
    private double updatedBalance;
    private String message;
}
