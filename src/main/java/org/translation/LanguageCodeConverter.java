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

    private Map<String, List<String>> languagesCodes = new HashMap<>();
    private Map<String, String> reverseMap = new HashMap<>();

    /**
     * Default constructor which will load the language codes from "language-codes.txt"
     * in the resources folder.
     */
    public LanguageCodeConverter() {
        this("language-codes.txt");
    }

    /**
     * Overloaded constructor which allows us to specify the filename to load the language code data from.
     * @param filename the name of the file in the resources folder to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public LanguageCodeConverter(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(getClass()
                    .getClassLoader().getResource(filename).toURI()));

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String languageNames = parts[0].trim();
                    String languageCode = parts[1].trim();

                    // Handle possible malformed entries
                    if (languageNames.contains(",=") || languageNames.contains("=(")) {
                        System.err.println("Malformed entry detected: " + line);
                        continue; // Skip malformed entries
                    }

                    // Store in languagesCodes
                    List<String> names = Arrays.asList(languageNames.split(",\\s*"));
                    languagesCodes.put(languageCode, names);

                    // Store in reverseMap
                    for (String name : names) {
                        name = name.trim(); // Clean up whitespace
                        reverseMap.put(name, languageCode); // Overwrite if duplicate
                    }

                    // Debugging output
                    System.out.println("Languages Codes: " + languagesCodes);
                    System.out.println("Reverse Map: " + reverseMap);
                    System.out.println("Number of Languages in languagesCodes: " + languagesCodes.size());
                    System.out.println("Number of Languages in reverseMap: " + reverseMap.size());
                } else {
                    System.err.println("Not enough parts in line: " + line);
                }
            }
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        // Create an instance and pass the filename
        new LanguageCodeConverter("language-codes.txt");
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
        return reverseMap.size();
    }
}
