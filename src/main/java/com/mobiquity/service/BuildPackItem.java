package com.mobiquity.service;


import static com.mobiquity.model.Constants.COMMA;
import static com.mobiquity.model.Constants.CONSTRAINT_VALIDATION;
import static com.mobiquity.model.Constants.EURO_CURRENCY;
import static com.mobiquity.model.Constants.LEFT_PARENTHESIS;
import static com.mobiquity.model.Constants.MESSAGE_PATH_NOT_FOUND;
import static com.mobiquity.model.Constants.RIGHT_PARENTHESIS;
import static com.mobiquity.model.Constants.EMPTY_STRING;
import static com.mobiquity.model.Constants.COLON;

import com.mobiquity.exception.APIException;
import com.mobiquity.model.Constants;
import com.mobiquity.model.PackItem;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildPackItem {

    private BuildPackItem() {
    }

    public static String getFileContent(String pathFile) throws APIException {

        String fileContent;
        String packResponse;
        try {
            fileContent = Files.readString(Paths.get(pathFile));
            List<String> content = Arrays.asList(fileContent.split("\n"));
            var itemResult = getPackItem(content);
            packResponse = (long) content.size()
                            + "\n"
                            + itemResult.map(Object::toString).collect(Collectors.joining("\n"));

        } catch (IOException fileException) {
            fileException.printStackTrace();
            throw new APIException(MESSAGE_PATH_NOT_FOUND);
        }
        return packResponse;
    }

    private static Stream<List<String>> getPackItem(List<String> content) {
        return content.stream().filter(s ->  Integer.parseInt(s.substring(0, s.indexOf(COLON)).trim())<100)
                .map( line -> {
                            var weight = new BigDecimal(line.substring(0, line.indexOf(COLON)).trim());
                            var lineContent = getLineContentConstants(line);
                            var itemValues = getItemValues(line, lineContent);
                            var packItems = buildPackItems(itemValues);
                            return getItemByWeight(packItems,weight );
                        });
    }

    private static String getLineContentConstants(String line) {
        return line.replace(LEFT_PARENTHESIS, EMPTY_STRING)
                .replace(EURO_CURRENCY, EMPTY_STRING)
                .replace(RIGHT_PARENTHESIS, COMMA);
    }

    private static List<String> getItemValues(String line, String lineContent) {
        return Arrays.stream(lineContent.substring(line.indexOf(Constants.COLON) + 1).split(COMMA))
                .collect(Collectors.toList());
    }

    private static List<PackItem> buildPackItems(List<String> itemValues) {
        List<PackItem> packItems = new ArrayList<>();

        int i= 0;
        while( i < itemValues.size()) {
            packItems.add(
                    PackItem.builder()
                            .index(Integer.parseInt(itemValues.get(i).trim()))
                            .weight(new BigDecimal(itemValues.get(i+1).trim()))
                            .cost(new BigDecimal(itemValues.get(i+2).trim()))
                            .build());
            i=i + 3;
        }
        return packItems;
    }

    private static List<String> getItemByWeight(List<PackItem> itemValues, BigDecimal weight) {
        int iteration = 0;
        List<String> indexItemsPackage= new ArrayList<>();

        while (iteration < itemValues.size()) {
            BigDecimal weightAccumulator = BigDecimal.ZERO;
            String packageItem = Constants.EMPTY_STRING;

            for (int i = 0; i < itemValues.size(); i++) {
                BigDecimal indexItemValue = itemValues.get(i).getWeight();
                var sumWeight =   weightAccumulator.add(indexItemValue);

                if ((i != iteration && sumWeight.compareTo(weight) < 0) && isValidConstraint(itemValues, i)) {
                    weightAccumulator = sumWeight;

                    if( ! String.valueOf(itemValues.get(i).getIndex()).equals(Constants.EMPTY_STRING)) {
                        packageItem = getPackageItem(String.valueOf(itemValues.get(i).getIndex()), packageItem);
                    }
                }
            }
            if(! packageItem.equals(EMPTY_STRING)){
                indexItemsPackage.add(packageItem);
            }

            iteration ++;
        }
        return indexItemsPackage.stream().distinct().map(s -> s.substring(0,s.length()-1)) .collect(Collectors.toList());
    }

    private static boolean isValidConstraint(List<PackItem> itemValues, int i) {
        return itemValues.get(i).getWeight().compareTo(CONSTRAINT_VALIDATION) < 0 &&
                itemValues.get(i).getCost().compareTo(CONSTRAINT_VALIDATION) < 0;
    }

    private static String getPackageItem(String indexValue, String packageItem) {
        return packageItem + indexValue + '-';
    }
}
