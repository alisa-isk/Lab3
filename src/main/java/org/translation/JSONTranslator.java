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
    private final List<CountryData> countryDataList;
    private final List<String> countryCodes;

    public JSONTranslator() {
        this("sample.json");
    }

    public JSONTranslator(String filename) {
        countryDataList = new ArrayList<>();
        countryCodes = new ArrayList<>();
        try {
            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String countryCode = jsonObject.getString("alpha3");
                countryCodes.add(countryCode);
                List<String> languageCodes = new ArrayList<>();
                List<String> countryNames = new ArrayList<>();
                for (String key : jsonObject.keySet()) {
                    if (!"id".equals(key) && !"alpha2".equals(key) && !"alpha3".equals(key)) {
                        languageCodes.add(key);
                        countryNames.add(jsonObject.getString(key));
                    }
                }
                countryDataList.add(new CountryData(countryCode, languageCodes, countryNames));
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Returns the list of languages spoken in the specified country.
     *
     * @param country the country code for which to get languages
     * @return the list of languages spoken in the country, or null if the country is not found
     */
    @Override
    public List<String> getCountryLanguages(String country) {
        for (CountryData countryData : countryDataList) {
            if (countryData.getCountryCode().equalsIgnoreCase(country)) {
                return new ArrayList<>(countryData.getLanguageCodes());
            }
        }
        return null;
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countryCodes);
    }

    /**
     * Translates the given country name to the specified language.
     *
     * @param country  the country code for the country to be translated
     * @param language the language code for the language to translate to
     * @return the name of the country in the specified language, or null if the translation is not found
     */
    @Override
    public String translate(String country, String language) {
        for (CountryData countryData : countryDataList) {
            if (countryData.getCountryCode().equalsIgnoreCase(country)) {
                int index = countryData.getLanguageCodes().indexOf(language);
                if (index != -1) {
                    return countryData.getCountryNames().get(index);
                }
            }
        }
        return null;
    }

    private static class CountryData {
        private final String countryCode;
        private final List<String> languageCodes;
        private final List<String> countryNames;

        /**
         * Constructs a CountryData instance with the specified parameters.
         *
         * @param countryCode   the country code (may be null)
         * @param languageCodes the list of language codes (may be null)
         * @param countryNames  the list of country names (may be null)
         */
        CountryData(String countryCode, List<String> languageCodes, List<String> countryNames) {
            this.countryCode = countryCode;
            this.languageCodes = languageCodes;
            this.countryNames = countryNames;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public List<String> getLanguageCodes() {
            return new ArrayList<>(languageCodes);
        }

        public List<String> getCountryNames() {
            return new ArrayList<>(countryNames);
        }
    }
}

