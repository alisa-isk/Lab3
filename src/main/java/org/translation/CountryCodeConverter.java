package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides the service of converting country codes to their names.
 */
public class CountryCodeConverter {
    private Map<String, String> countryCode = new HashMap<>();
    private Map<String, String> reverseMap = new HashMap<>();
    /**
     * Default constructor which will load the country codes from "country-codes.txt"
     * in the resources folder.
     */
    public CountryCodeConverter() {
        this("country-codes.txt");
    }

    /**
     * Overloaded constructor which allows us to specify the filename to load the country code data from.
     * @param filename the name of the file in the resources folder to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public CountryCodeConverter(String filename) {

        try {
            List<String> lines = Files.readAllLines(Paths.get(getClass()
                    .getClassLoader().getResource(filename).toURI()));
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                int index = line.indexOf('\t');
                int index1 = line.indexOf('\t', index + 1);
                int index2 = line.indexOf('\t', index1 + 1);
                if (index != -1) {
                    String first = line.substring(0, index);
                    String second = line.substring(index1, index2);

                    countryCode.put(first.trim(), second.trim());
                    reverseMap.put(second.trim(), first.trim());
                }
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * Returns the name of the country for the given country code.
     * @param code the 3-letter code of the country
     * @return the name of the country corresponding to the code
     */
    public String fromCountryCode(String code) {
        String countryName = reverseMap.get(code);
        return countryName;
    }

    /**
     * Returns the code of the country for the given country name.
     * @param country the name of the country
     * @return the 3-letter code of the country
     */

    // We are assuming that countries will be entered as we are given for example "Niger (the)"
    public String fromCountry(String country) {
        String countryName = countryCode.get(country);
        return countryName;
    }

    /**
     * Returns how many countries are included in this code converter.
     * @return how many countries are included in this code converter.
     */
    public int getNumCountries() {
        return reverseMap.size();
    }
}
