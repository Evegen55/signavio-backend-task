
package com.signavio;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) throws InterruptedException {
        List<EventlogRow> eventlogRows = CSVReader.readFile("samples/Activity_Log_2004_to_2014.csv");

        List<String> benchmarkResults = new ArrayList<>(100);//to properly make a benchmark

        for (int i = 0; i < 100; i++) {
            long begin = System.currentTimeMillis();

            String queryEventLog = queryEventLog(eventlogRows);
            System.out.println(queryEventLog);

            long end = System.currentTimeMillis();

            benchmarkResults.add(String.format("Duration: %s milliseconds", end - begin));
        }

        benchmarkResults.forEach(System.out::println);
    }

    /**
     *
     * @param eventlogRows - log with events from ERP system
     * @return JSON string with aggregated cases that have the same event execution order and list the 10 variants with the most cases.
     */
    public static String queryEventLog(List<EventlogRow> eventlogRows) {
        Map<String, Map<OffsetDateTime, String>> allUniqueProcureCases = getUniqueProcuresOnly(eventlogRows);

        LinkedHashMap<TreeSet<String>, Integer> uniqueStepsWithNumberOfUniqueProcures =
                getUniqueStepsWithNumberOfUniqueProcures(allUniqueProcureCases);

        return makeFormattedJsonStringWithoutAnyLibrary(uniqueStepsWithNumberOfUniqueProcures);
    }

    private static String makeFormattedJsonStringWithoutAnyLibrary(
            LinkedHashMap<TreeSet<String>, Integer> uniqueStepsWithNumberOfUniqueProcures
    ) {
        int maxElements = 10;
        int counter = 0;

        var iterator = uniqueStepsWithNumberOfUniqueProcures.entrySet().iterator();
        StringBuilder stringBuilderWholeJson = new StringBuilder();
        stringBuilderWholeJson.append("{\n");
        stringBuilderWholeJson.append("\t\"totalUniqueSetsOfProcureSteps\": ");
        stringBuilderWholeJson.append(uniqueStepsWithNumberOfUniqueProcures.size());
        stringBuilderWholeJson.append(",\n");
        stringBuilderWholeJson.append("\t\"setsOfProcureSteps\": [");

        while (iterator.hasNext() && counter < maxElements) {
            Map.Entry<TreeSet<String>, Integer> next = iterator.next();
            String setOfStepsString = "{\n" +
                                         "\t\t\"setOfSteps\": [\"" +
                                         String.join("\",\"", next.getKey()) +
                                         "\"]" +
                                         ",\n" +
                                         "\t\t\"occurence\": " +
                                         next.getValue() +
                                         "\n\t}" +
                                         ", ";
            stringBuilderWholeJson.append(setOfStepsString);
            counter += 1;
        }
        int lastIndexOf = stringBuilderWholeJson.lastIndexOf(",");
        stringBuilderWholeJson.delete(lastIndexOf, lastIndexOf + 1);
        stringBuilderWholeJson.append("]\n}");
        return stringBuilderWholeJson.toString();
    }

    private static LinkedHashMap<TreeSet<String>, Integer> getUniqueStepsWithNumberOfUniqueProcures(
            Map<String, Map<OffsetDateTime, String>> allUniqueProcureCases
    ) {
        //first we need to see how many procures have same steps in same time order
        var uniqueProcuresGroupedByUniqueSetOfProcureSteps =
                allUniqueProcureCases.entrySet()
                        .stream()
                        .collect(Collectors.groupingBy(entry -> new TreeSet<>(entry.getValue().values()),
                                                       HashMap::new,
                                                       Collectors.toList()));
        //next, we need to range by number of procures for each set of steps in DESC order
        var uniqueStepsWithNumberOfUniqueProcures =
                uniqueProcuresGroupedByUniqueSetOfProcureSteps.entrySet()
                        .stream()
                        .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().size(),
                                (e1, e2) -> e1,
                                LinkedHashMap::new));
        return uniqueStepsWithNumberOfUniqueProcures;
    }

    private static Map<String, Map<OffsetDateTime, String>> getUniqueProcuresOnly(
            List<EventlogRow> eventlogRows
    ) {
        HashSet<EventlogRow> onlyUniqueLogEventRows = new HashSet<>(); //filter duplicates events from log
        Map<String, Map<OffsetDateTime, String>> allUniqueProcureCases = new HashMap<>();

        eventlogRows
                .stream()
                .filter(eventlogRow -> !onlyUniqueLogEventRows.contains(eventlogRow))
                .forEach(eventlogRow -> {
                    onlyUniqueLogEventRows.add(eventlogRow);
                    Map<OffsetDateTime, String> procureCaseSteps = allUniqueProcureCases.get(eventlogRow.caseId);
                    if (procureCaseSteps != null) {
                        procureCaseSteps.put(eventlogRow.timestamp, eventlogRow.eventName);
                    } else {
                        allUniqueProcureCases.put(eventlogRow.caseId, new TreeMap<>());
                    }
                });
        return allUniqueProcureCases;
    }
}
