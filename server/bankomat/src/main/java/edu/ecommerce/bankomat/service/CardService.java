package edu.ecommerce.bankomat.service;

import edu.ecommerce.bankomat.model.Card;
import edu.ecommerce.bankomat.repository.CardRepository;
import edu.ecommerce.bankomat.response.DepositResponse;
import edu.ecommerce.bankomat.response.WithdrawResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;

    private Card activeCard;

    public Card createCard() {
        String cardNumber = generateCardNumber();
        String accountNumber = generateAccountNumber();
        String cvv = generateCVV(cardNumber, accountNumber);
        String pin = this.generatePIN();
        String pvv = generatePVV(pin);
        Card card = new Card(pin, pvv, accountNumber, cardNumber, cvv);
        cardRepository.save(card);
        return card;
    }

    public Card getActiveCard() {
        return activeCard;
    }

    public boolean authorizeCard(String cvv, String pvv) {
        Card card = cardRepository.findByCvvAndPvv(cvv, pvv);
        if (card == null) {
            return false;
        }

        card.setAuthorized(true);
        this.cardRepository.save(card);
        this.activeCard = card;

        return true;
    }

    public WithdrawResponse withdrawAmount(String accountNumber, double amount) {
        Card card = this.activeCard;
        if (card != null && card.isAuthorized() && card.getAccountNumber().equals(accountNumber)) {
            double balance = card.getBalance();
            if (amount > balance) {
                return new WithdrawResponse(false, balance, "Insufficient funds");
//            throw new InsufficientFundsException();
            }
            balance -= amount;
            card.setBalance(balance);
            cardRepository.save(card);
            this.updateCurrentCard();
            return new WithdrawResponse(true, balance, "Success");
        } else {
            return new WithdrawResponse(false, 0, "Unauthorized");
        }
    }

    public DepositResponse depositAmount(String accountNumber, double amount) {
        Card card = this.activeCard;
        if (card != null && card.getAccountNumber().equals(accountNumber) && card.isAuthorized()) {
            double balance = card.getBalance();
            balance += amount;
            card.setBalance(balance);
            cardRepository.save(card);
            this.updateCurrentCard();
            return new DepositResponse(true, balance);
        }

        return new DepositResponse(false, 0);
    }

    private String generateCardNumber() {
        SecureRandom random = new SecureRandom();
        String cardNumber = "";
        for (int i = 0; i < 14; i++) {
            cardNumber += random.nextInt(10);
        }

        return cardNumber;
    }

    private String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        String accountNumber = "";
        for (int i = 0; i < 16; i++) {
            accountNumber += random.nextInt(10);
        }
        return accountNumber;
    }

    private String generateCVV(String cardNumber, String accountNumber) {
        String hash = cardNumber.toString() + accountNumber.toString();
        hash = getHash(getHash(getHash(hash)));
        // cut second half of cash
        String secondHalf = hash.substring(hash.length() / 2 - 1);
        return secondHalf;
    }

    private String generatePIN() {
        SecureRandom random = new SecureRandom();
        String pin = "";
        for (int i = 0; i < 4; i++) {
            pin += random.nextInt(10);
        }

        return pin;
    }

    private String generatePVV(String pin) {
        String pvvHash = getHash(getHash(getHash(pin)));
        // cut second half of cash
        String secondHalf = pvvHash.substring(pvvHash.length() / 2 - 1);
        return secondHalf;
    }

    private void updateCurrentCard() {
        this.activeCard = this.cardRepository.findByCvvAndPvv(this.activeCard.getCvv(), this.activeCard.getPvv());
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
