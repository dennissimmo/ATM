package edu.ecommerce.bankomat.response;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    private String pvv;
    private String cvv;

    public String getPvv() {
        return pvv;
    }

    public void setPvv(String pvv) {
        this.pvv = pvv;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}