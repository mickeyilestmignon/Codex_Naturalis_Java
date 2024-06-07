package sae.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlayableDeck {
    private final Map<String, List<PlayableCard>> deck;

    public PlayableDeck() {
        HashMap<String, List<PlayableCard>> newDeck = new HashMap<String, List<PlayableCard>>();
        newDeck.put("starterCard", new ArrayList<PlayableCard>());
        newDeck.put("resourceCard", new ArrayList<PlayableCard>());
        newDeck.put("goldCard", new ArrayList<PlayableCard>());
        this.deck = newDeck;
    }
    
    public List<PlayableCard> get(String cardType) {
    	Objects.requireNonNull(cardType);
    	return deck.get(cardType);
    }

    public void addStarterCard(PlayableCard card) {
        Objects.requireNonNull(card);
        deck.get("starterCard").add(card);
    }

    public void addResourceCard(PlayableCard card) {
        Objects.requireNonNull(card);
        deck.get("resourceCard").add(card);
    }

    public void addGoldCard(PlayableCard card) {
        Objects.requireNonNull(card);
        deck.get("goldCard").add(card);
    }
    
    public void removeCard(String cardType, PlayableCard card) {
    	Objects.requireNonNull(cardType);
    	Objects.requireNonNull(card);
    	deck.get(cardType).remove(card);
    }
}
