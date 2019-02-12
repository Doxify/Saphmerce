package org.saphron.saphmerce;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.saphron.saphmerce.serialization.ShopSerializer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    Saphmerce plugin;
    ShopSerializer serializer;

    public FileManager(Saphmerce p) {
        plugin = p;
        serializer = new ShopSerializer();
    }


    public Shop loadShopFromFile() {
        try {
            Object shopFileParsed = new JSONParser().parse(new FileReader("./plugins/Saphmerce/shop.json"));
            JSONObject shopFileJson = (JSONObject) shopFileParsed;
            JSONArray shopFileCategoriesJson = (JSONArray) shopFileJson.get("categories");
            List<Category> categories = new ArrayList<>();

            for(Object category : shopFileCategoriesJson) {
                JSONObject categoryJson = (JSONObject) category;
                categories.add(serializer.jsonToCategory(categoryJson));
            }

            return new Shop(plugin, categories);

        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("[Saphmerce] Could not find shop.json, returning an empty shop.");
            return new Shop(plugin);

        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("[Saphmerce] Could not parse shop.json, returning an empty shop.");
            return new Shop(plugin);
        }
    }

    public void saveShopToFile() {
        JSONObject shopFileJson = new JSONObject();
        JSONArray categoriesArray = new JSONArray();
        List<Category> categoryObjects = plugin.getShop().getShopCategories();

        for(Category category : categoryObjects) {
            categoriesArray.add(serializer.categoryToJson(category));
        }

        shopFileJson.put("categories", categoriesArray);

        try {
            FileWriter writer = new FileWriter("./plugins/Saphmerce/shop.json");
            writer.write(shopFileJson.toString());
            writer.close();
            System.out.println("[Saphmerce] Successfully saved shop to shop.json!");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Saphmerce] An error occurred while saving shop to shop.json");
        }


    }


}
