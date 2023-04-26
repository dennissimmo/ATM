package edu.ecommerce.bankomat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


@Document(collection = "card")
@JsonIgnoreProperties(value = {"pin"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Card {

    @Id
    private String id;

    private String cardNumber;
    private String accountNumber;
    @JsonIgnore
    @Transient
    private String pin;
    private String pvv;
    private String cvv;
    private double balance;
    private boolean isAuthorized;

    public Card(String cvv, String pvv) {
        this.cvv = cvv;
        this.pvv = pvv;
        // generate cardNumber
        // generate accountNumber
        this.balance = 100;
    }

    public Card(String pin, String pvv, String accountNumber, String cardNumber, String cvv) {
        this.pin = pin;
        this.pvv = pvv;
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.balance = 100;
    }

    // generate cardNumber
    // generate accountNumber
    // generate pvv
    // generate cvv

    public String getCardAccountHash() {
        String hash = cardNumber.toString() + accountNumber.toString();
        hash = getHash(getHash(getHash(hash)));
        // cut second half of cash
        String secondHalf = hash.substring(hash.length() / 2 - 1);
        return secondHalf;
    }

    private String getHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash");
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
