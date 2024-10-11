package com.example.cashcard.api.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.cashcard.data.entity.CashCard;

@Service
public class CashCardService {
    
    public CashCard getCashCard(
        Long id
    ) {
        //...

        return new CashCard(id, 20.33);
    }

    public CashCard createCashCard(
        CashCard newCashCard
    ) {
        return newCashCard;
    }

    public Iterable<CashCard> getAllCashCard() {

        Iterable<CashCard> list = null;

        return list;
    }
}
