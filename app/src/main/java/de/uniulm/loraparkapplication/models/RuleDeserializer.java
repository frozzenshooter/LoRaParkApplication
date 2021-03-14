package de.uniulm.loraparkapplication.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Custom deserializer that converts a json rule to a rule object
 */
public class RuleDeserializer implements JsonDeserializer<Rule> {

    @Override
    public Rule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();

        // Id
        JsonElement idElement = jObject.get("id");
        if(idElement == null){
            throw new JsonParseException("No id found");
        }

        String id = idElement.getAsString();

        // Name
        JsonElement nameElement = jObject.get("name");

        String name = "";
        if(idElement != null){
            name = nameElement.getAsString();
        }

        // Description
        JsonElement descriptionElement = jObject.get("description");

        String description = "";
        if(idElement != null){
            description = descriptionElement.getAsString();
        }

        // Condition
        JsonElement conditionElement = jObject.get("condition");

        String condition = "";
        if(conditionElement != null) {
            //TODO: IT MIGHT BE NECESSARY TO ESCAPE THE " IN THE RESULT
            condition  = conditionElement.getAsJsonObject().toString();
        }

        //TODO: LOAD SENSORS AND GEO LOCATIONS

        // Rule creation
        Rule rule = new Rule();

        rule.setId(id);
        rule.setName(name);
        rule.setDescription(description);
        rule.setCondition(condition);
        rule.setIsActive(false);

        return rule;
    }
}
