package controlClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entities.Player;
import logics.Game;

import java.io.*;
import java.lang.reflect.Type;

/**
 * Формирование JSON
 * Формирует библиоека GSON
 * Для сохранения и загрузки игры
 */
class JsonOperator {
    static Game readJson(String path){
        File file = new File(path);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line, ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null)
                sb.append(line).append(ls);
            sb.deleteCharAt(sb.length() - 1);
            reader.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return fromJson(sb.toString());
    }
    static void writeJson(Game game, String path){
        String json = toJson(game);

        File file = new File(path);
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    static String toJson(Game game){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(game);
    }

    static Game fromJson(String json){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Player.class, new PlayerAdapter())
                .create();
        Type type = new TypeToken<Game>(){}.getType();
        return gson.fromJson(json, type);
    }
}
