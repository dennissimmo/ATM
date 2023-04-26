package edu.ecommerce.bankomat.repository;

import edu.ecommerce.bankomat.model.Card;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends MongoRepository<Card, String> {
    Card findByCardNumber(String cardNumber);

    Card findByCvvAndPvv(String cvv, String pvv);

}
