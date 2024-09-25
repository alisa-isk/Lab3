package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides the service of converting language codes to their names.
 */
public class LanguageCodeConverter {

    private final Map<String, List<String>> languagesCodes = new HashMap<>();
    private final Map<String, String> reverseMap = new HashMap<>();

    // Add an empty line before the constructor
    /**
     * Default constructor which will load the language codes from "language-codes.txt"
     * in the resources folder.
     */
    public LanguageCodeConverter() {
        this("language-codes.txt");
    }

    // Add an empty line before the overloaded constructor
    /**
     * Overloaded constructor which allows us to specify the filename to load the language code data from.
     * @param filename the name of the file in the resources folder to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public LanguageCodeConverter(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(getClass()
                    .getClassLoader().getResource(filename).toURI()));
            for (int i = 0; i < lines.size(); i++) {
                String hold = lines.get(i);
                int index = hold.indexOf('\t');
                if (index != -1) {
                    String first = hold.substring(0, index);
                    String end = hold.substring(index + 1);

                    List<String> names = Arrays.asList(first.split(",\\s*"));
                    languagesCodes.put(end.trim(), names);

                    for (String name : names) {
                        reverseMap.put(name.trim(), end.trim());
                    }
                }
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns the name of the language for the given language code.
     * @param code the language code
     * @return the name of the language corresponding to the code
     */
    public String fromLanguageCode(String code) {
        List<String> names = languagesCodes.get(code);
        if (names != null && !names.isEmpty()) {
            return names.get(0);
        }
        return null;
    }

    /**
     * Returns the code of the language for the given language name.
     * @param language the name of the language
     * @return the 2-letter code of the language
     */
    public String fromLanguage(String language) {
        return reverseMap.get(language);
    }

    /**
     * Returns how many languages are included in this code converter.
     * @return how many languages are included in this code converter.
     */
    public int getNumLanguages() {
        return languagesCodes.size();
    }
}