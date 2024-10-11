package com.example.cashcard.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.cashcard.api.service.CashCardService;
import com.example.cashcard.data.entity.CashCard;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    @Autowired
    private CashCardService cardServ;
    
    // READ / GET
    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> getCashCard(
        @PathVariable("requestedId") Long requestedId
    ) {
        
        CashCard cashCard = cardServ.getCashCard(requestedId);
        return ResponseEntity.ok(cashCard);
        // return ResponseEntity.notFound().build();
    }
    
    // CREATE / POST
    @PostMapping
    private ResponseEntity<Void> postCashCard(
        @RequestBody CashCard newCashCard,
        UriComponentsBuilder ucb
    ) {
        CashCard savedCashCard = cardServ.createCashCard(newCashCard);

        URI locationURIOfNewCashCard = ucb
            .path("cashcards/{id}")
            .buildAndExpand(savedCashCard)
            .toUri();

        return ResponseEntity
                    .created(locationURIOfNewCashCard)
                    .build();
    }

    // READ ALL / GET ALL
    @GetMapping
private ResponseEntity<Iterable<CashCard>> getAllCashCards(

) {
    Iterable<CashCard> list = cardServ.getAllCashCard();

    return ResponseEntity.ok(list);
}
}
