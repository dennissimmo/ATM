package edu.ecommerce.bankomat.controller;

import edu.ecommerce.bankomat.model.Card;
import edu.ecommerce.bankomat.repository.CardRepository;
import edu.ecommerce.bankomat.response.*;
import edu.ecommerce.bankomat.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/atm")
public class BankomatController {

    @Autowired
    private CardService cardService;

    @GetMapping("/create-card")
    public CardCreationResponse createCard() {
        Card card = this.cardService.createCard();
        // send to user
        // open PIN
        // CVV
        // balance
        // account number
        return new CardCreationResponse(card.getPin(), card.getCvv(), card.getBalance(), card.getAccountNumber());
    }

    @PostMapping("/authorize-card")
    public Boolean authorizeCard(@RequestBody CardRequest data) {
        boolean isAuthentic = this.cardService.authorizeCard(data.getCvv(), data.getPvv());
        // maybe we have to update card status here
        if (isAuthentic) {
//            card.setAuthorized(true);
//            cardRepository.save(card);
            System.out.println("Card data is valid. Card authorized");
            return isAuthentic;
        }

        System.out.println("Card data is invalid. Card unauthorized");

        return isAuthentic;
    }

    @PostMapping("/deposit")
    public DepositResponse deposit(@RequestBody OperationRequest request) {
        return this.cardService.depositAmount(request.getAccountNumber(), request.getAmount());
    }

    @PostMapping("/withdraw")
    public WithdrawResponse withdraw(@RequestBody OperationRequest request) {
        return this.cardService.withdrawAmount(request.getAccountNumber(), request.getAmount());
    }

}