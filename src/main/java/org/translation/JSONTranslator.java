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

    // Define a class to hold the country data
    private static class CountryData {
        String countryCode;
        List<String> languageCodes;
        List<String> countryNames;

        CountryData(String countryCode, List<String> languageCodes, List<String> countryNames) {
            this.countryCode = countryCode;
            this.languageCodes = languageCodes;
            this.countryNames = countryNames;
        }
    }

    private List<CountryData> countryDataList;
    private List<String> countryCodes;

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
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Get the country code
                String countryCode = jsonObject.getString("alpha3");
                countryCodes.add(countryCode);

                // Create lists for language codes and country names
                List<String> languageCodes = new ArrayList<>();
                List<String> countryNames = new ArrayList<>();

                // Iterate over the JSON object to get language codes and country names
                for (String key : jsonObject.keySet()) {
                    if (!"id".equals(key) && !"alpha2".equals(key) && !"alpha3".equals(key)) {
                        languageCodes.add(key);
                        countryNames.add(jsonObject.getString(key));
                    }
                }

                // Add the country data to the list
                countryDataList.add(new CountryData(countryCode, languageCodes, countryNames));
            }

        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        for (CountryData countryData : countryDataList) {
            if (countryData.countryCode.equalsIgnoreCase(country)) {
                return new ArrayList<>(countryData.languageCodes);  // Return a copy to avoid aliasing
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countryCodes);  // Return a copy to avoid aliasing
    }

    @Override
    public String translate(String country, String language) {
        for (CountryData countryData : countryDataList) {
            if (countryData.countryCode.equalsIgnoreCase(country)) {
                int index = countryData.languageCodes.indexOf(language);
                if (index != -1) {
                    return countryData.countryNames.get(index);
                }
            }
        }
        return null;  // Return null if the country or language is not found
    }
}
