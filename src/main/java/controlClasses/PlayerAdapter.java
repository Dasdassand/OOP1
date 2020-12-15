package controlClasses;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import entities.Card;
import entities.Human;
import entities.Player;

import java.lang.reflect.Type;

/**
 * Реализация механизма опредления человека
 * При дисериализации
 */
@JsonAdapter(Player.class)
public class PlayerAdapter implements JsonDeserializer<Player> {
    @Override
    public Player deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        if (object.get("isRealHuman").getAsBoolean()) {
            Human human = new Human(object.get("name").getAsString(),true);
            JsonArray cards = object.getAsJsonArray("hand");
            for (JsonElement card : cards){
                human.addToHand(jsonDeserializationContext.deserialize(card, Card.class));
            }
            return human;
        } else {
            Player player = new Player(object.get("name").getAsString(),false);
            JsonArray cards = object.getAsJsonArray("hand");
            for (JsonElement card : cards){
                player.addToHand(jsonDeserializationContext.deserialize(card, Card.class));
            }
            return player;
        }
    }
}
