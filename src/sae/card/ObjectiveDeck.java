package sae.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ObjectiveDeck {
    private final Map<String, List<Card>> deck;

    public ObjectiveDeck() {
        HashMap<String, List<Card>> newDeck = new HashMap<String, List<Card>>();
        newDeck.put("objective", new ArrayList<Card>());
        this.deck = newDeck;
    }
    
    public List<Card> get(String cardType) {
    	Objects.requireNonNull(cardType);
    	return deck.get(cardType);
    }

    public void addObjectiveCard(Card card) {
        Objects.requireNonNull(card);
        deck.get("objective").add(card);
    }
    
    public void removeCard(Card card) {
    	Objects.requireNonNull(card);
    	deck.get("objective").remove(card);
    }
}
