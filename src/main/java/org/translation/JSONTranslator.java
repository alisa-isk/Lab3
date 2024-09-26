package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private List<List<String>> countryDataList;
    private List<String> countryCodes;
    private String delimiter = ",";

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        countryDataList = new ArrayList<>();
        countryCodes = new ArrayList<>();
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject hold = jsonArray.getJSONObject(i);

                // Create a list to store the country data
                List<String> countryData = new ArrayList<>();

                // Get the country code
                String countryCode = hold.getString("alpha3");
                countryCodes.add(countryCode);
                countryData.add(countryCode);

                // Create lists for language codes and country names
                List<String> languageCodes = new ArrayList<>();
                List<String> countryNames = new ArrayList<>();

                // Iterate over the JSON object to get language codes and country names
                for (String key : hold.keySet()) {
                    if (!"id".equals(key) && !"alpha2".equals(key) && !"alpha3".equals(key)) {
                        languageCodes.add(key);
                        countryNames.add(hold.getString(key));
                    }
                }

                // Add the languageCodes and countryNames to the countryCodes list
                countryData.add(String.join(delimiter, languageCodes));
                countryData.add(String.join(delimiter, countryNames));

                // Add the complete countryCodes list to the countryDataList
                countryDataList.add(countryData);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        for (List<String> countryData : countryDataList) {
            String countryCode = countryData.get(0);
            if (countryCode.equalsIgnoreCase(country)) {
                String languages = countryData.get(1);
                return List.of(languages.split(delimiter));
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countryCodes);
    }

    @Override
    public String translate(String country, String language) {

        for (List<String> countryData : countryDataList) {
            String countryCode = countryData.get(0);
            if (countryCode.equalsIgnoreCase(country)) {
                List<String> languageCodes = List.of(countryData.get(1).split(delimiter));
                List<String> countryNames = List.of(countryData.get(2).split(delimiter));

                // Find the index of the language in the languageCodes list
                int index = languageCodes.indexOf(language);
                if (index != -1) {
                    return countryNames.get(index);
                }
            }
        }
        return null;
    }
}
