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

    // TODO Task: pick appropriate instance variables for this class

    private List<List<Object>> countryDataList;
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
                JSONObject hold = jsonArray.getJSONObject(i);

                // Create a list to store the country data
                List<Object> countryData = new ArrayList<>();

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
                countryData.add(languageCodes);
                countryData.add(countryNames);

                // Add the complete countryCodes list to the countryDataList
                countryDataList.add(countryData);
            }
            // TODO Task: use the data in the jsonArray to populate your instance variables
            //            Note: this will likely be one of the most substantial pieces of code you write in this lab.
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public List<String> getCountryLanguages(String country) {
        // TODO Task: return an appropriate list of language codes,
        //            but make sure there is no aliasing to a mutable object
        for (List<Object> countryData : countryDataList) {
            String countryCode = (String) countryData.get(0);
            if (countryCode.equalsIgnoreCase(country)) {
                List<String> languageCodes = (List<String>) countryData.get(1);
                return new ArrayList<>(languageCodes);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getCountries() {
        // TODO Task: return an appropriate list of country codes,
        //            but make sure there is no aliasing to a mutable object
        return countryCodes;
    }

    @Override
    public String translate(String country, String language) {
        // TODO Task: complete this method using your instance variables as needed
        for (List<Object> countryData : countryDataList) {
            String countryCode = (String) countryData.get(0);
            if (countryCode.equalsIgnoreCase(country)) {
                // Get the list of language codes and country names
                List<String> languageCodes = (List<String>) countryData.get(1);
                List<String> countryNames = (List<String>) countryData.get(2);

                // Find the index of the language in the languageCodes list
                int index = languageCodes.indexOf(language);
                if (index != -1) {
                    // Return the corresponding country name from countryNames list
                    return countryNames.get(index);
                }
            }
        }
        // Return null if the country or language is not found
        return null;
    }
}
