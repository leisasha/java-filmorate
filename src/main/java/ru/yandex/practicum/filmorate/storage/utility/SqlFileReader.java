package ru.yandex.practicum.filmorate.storage.utility;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SqlFileReader {
    public static String readSqlQuery(String fileName, String queryName) {
        Map<String, String> queries = loadSqlQueries(fileName);

        if (!queries.containsKey(queryName)) {
            throw new IllegalArgumentException("SQL-запрос с именем " + queryName + " не найден в файле " + fileName);
        }

        return queries.get(queryName);
    }

    private static Map<String, String> loadSqlQueries(String fileName) {
        Map<String, String> queries = new HashMap<>();

        try (InputStream is = SqlFileReader.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder currentQuery = new StringBuilder();
            String currentQueryName = null;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("--")) {
                    if (currentQueryName != null && currentQuery.length() > 0) {
                        queries.put(currentQueryName, currentQuery.toString().trim());
                        currentQuery.setLength(0);
                    }

                    currentQueryName = line.substring(2).trim();
                } else {
                    if (!line.isEmpty()) {
                        currentQuery.append(line).append(" ");
                    }
                }
            }

            if (currentQueryName != null && currentQuery.length() > 0) {
                queries.put(currentQueryName, currentQuery.toString().trim());
            }

        } catch (IOException | NullPointerException e) {
            throw new ValidationException("Ошибка при чтении SQL-файла " + fileName);
        }

        return queries;
    }
}
