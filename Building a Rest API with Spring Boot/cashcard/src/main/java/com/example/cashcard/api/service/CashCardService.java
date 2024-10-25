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

        Iterable<CashCard> list = new ArrayList<CashCard>() {{
            add(new CashCard(78L, 89.90));
            add(new CashCard(8L, 34.67));
            add(new CashCard(12L, 49.00));
        }};

        return list;
    }
}
