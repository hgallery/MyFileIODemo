package com.calmalgo.test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 A demo of extracting info from the example plain text data file, and writing the extracted info to another file.
 Requires NIO.2 (Non-blocking I/O Version 2)
 **/
public class Main {

    static final String INPUT_FILE_NAME = "input.txt";
    static final String OUTPUT_FILE_NAME = "output.txt";
    static final String NAME_TAG = "name";
    static final String PRICE_TAG = "price";


    public static void main(String[] args) throws IOException {

        String foodType = "veggie";
        //String foodType = "fruit";
        //String foodType = "dessert";

        List<List<String>> listOfUsefulItems = extractListOfUsefulItems (foodType);
        listOfUsefulItems.forEach(System.out::println);

        outputUsefulItems(listOfUsefulItems);
    }



    /**
     * Read all lines from the input file, then select only the useful lines.
     * For each useful line, extract the useful items specific to the data file format, then construct a list of useful items
     * And then construct a list of list of useful items.
     * @param foodType filter which lines of data according to food type
     * @return list of list of useful data items
     * @throws IOException NIO methods may throw IOException
     */
    static List<List<String>> extractListOfUsefulItems(String foodType) throws IOException {

        Function<String, List<String>> extractUsefulItems =  (text) -> {
            List<String> usefulItems = new ArrayList<>();
            String[] tokens = text.split(" ");
            for (String str : tokens) {
                if (str.startsWith(NAME_TAG) || str.startsWith(PRICE_TAG))
                    usefulItems.add(str.split(":")[1]); // add the data item next to the Name_TAG or Price_TAG
            }
            return usefulItems;
        };


        Predicate<String> checkFoodType = s -> s.contains(foodType);


        Path inputPath = Path.of(INPUT_FILE_NAME);
        try (Stream<String> textStream = Files.lines(inputPath)) {      // returning a Stream avoids storing all the lines in RAM

            return textStream
                    //.filter(s -> s.contains(foodType))
                    .filter(checkFoodType)
                    .map(extractUsefulItems)
                    .collect(Collectors.toList());

        }
    }


    /**
     * Write the list of useful data items to a file
     * @param listOfUsefulItems list of useful data items to be written to file
     * @throws IOException NIO methods may throw IOException
     */
    static void outputUsefulItems (List<List<String>> listOfUsefulItems) throws IOException{
        Path outputPath = Path.of(OUTPUT_FILE_NAME);
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            for (List<String> usefulItems : listOfUsefulItems) {
                writer.write(usefulItems.toString());
                writer.newLine();
            }
        }
    }

}
