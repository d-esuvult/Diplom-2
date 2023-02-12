import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class AssembleIngredients {

    public static ArrayList<String> getIngredientsByType(String body, String type) {

        ArrayList<String> idHash = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(body);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject ingredient = jsonArray.getJSONObject(i);
            String typeIngredient = ingredient.getString("type");
            if (typeIngredient.equals(type)) {
                String id = ingredient.getString("_id");
                idHash.add(id);
            }
        }
        return idHash;
    }

    public static String[] assembleIngredients(String body) {
        ArrayList<String> bun = getIngredientsByType(body, "bun");
        ArrayList<String> main = getIngredientsByType(body, "main");
        ArrayList<String> sauce = getIngredientsByType(body, "sauce");
        String[] burger = new String[3];
        Random random = new Random();
        burger[0] = bun.get(random.nextInt(bun.size()));
        burger[1] = main.get(random.nextInt(main.size()));
        burger[2] = sauce.get(random.nextInt(sauce.size()));
        return burger;
    }
}