package org.example;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class App {
    private static final int HOURS_IN_A_DAY = 24;
    private static final int[] prices = new int[HOURS_IN_A_DAY]; // Lagrar array med pris/timma.
    private static final int GRAPH_WIDTH = 76;
    private static final int GRAPH_HEIGHT = 10;

    public static void main(String[] args) {
        boolean running = true;
        Scanner scanner = new Scanner(System.in);  // Scanner utanför loopen

        while (running) {
            printMenu();

            if (!scanner.hasNextLine()) {  // Kollar efter input
                break;
            }

            String choice = scanner.nextLine().trim().toLowerCase(); // Tar input, trimmar ner den
                                                                     // + gör om till lowerCase.
            switch (choice) {
                case "1":
                    inputPrices(scanner);
                    break;
                case "2":
                    displayMinMaxAverage();
                    break;
                case "3":
                    sortAndPrintPrices();
                    break;
                case "4":
                    displayBest4HourPeriod();
                    break;
                case "5":
                    visualizePrices();
                    break;
                case "e":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.print("Ogiltigt val, vänligen försök igen.\n");
                    break;
            }
        }

        scanner.close();  // Stänger scanner efter loopen.
    }

    // Metoden för menyn
    private static void printMenu() {
        System.out.print("""
                Elpriser
                ========
                1. Inmatning
                2. Min, Max och Medel
                3. Sortera
                4. Bästa Laddningstid (4h)
                5. Visualisering
                e. Avsluta
                """);
    }

    // Metod för input av elpriser för varje timma
    private static void inputPrices(Scanner scanner) {
        System.out.print("Ange elpriser för varje timme på dygnet (i öre per kWh):\n");
        for (int i = 0; i < HOURS_IN_A_DAY; i++) {
            System.out.printf("Pris för timme %02d-%02d:\n", i, (i + 1) % 24);

            if (!scanner.hasNextLine()) {  // Kollar om det finns input
                System.out.print("Ingen ytterligare inmatning hittades.\n");
                return;
            }

            prices[i] = Integer.parseInt(scanner.nextLine().trim());
        }
    }

    // Metod för att visa min-, max- och medelpris.
    private static void displayMinMaxAverage() {
        Locale.setDefault(new Locale("sv", "SE"));
        int minPrice = Integer.MAX_VALUE;
        int maxPrice = Integer.MIN_VALUE;
        int minHour = -1;
        int maxHour = -1;
        int sum = 0;

        for (int i = 0; i < HOURS_IN_A_DAY; i++) {
            if (prices[i] < minPrice) {
                minPrice = prices[i];
                minHour = i;
            }
            if (prices[i] > maxPrice) {
                maxPrice = prices[i];
                maxHour = i;
            }
            sum += prices[i];
        }

        double average = sum / (double) HOURS_IN_A_DAY;
        System.out.print("Lägsta pris: " + formatTime(minHour) + ", "+ minPrice + " öre/kWh\n");
        System.out.print("Högsta pris: " + formatTime(maxHour) + ", " +maxPrice+" öre/kWh\n");
        System.out.printf("Medelpris: %.2f öre/kWh\n", average);
    }

    // Metod som visar priserna samt sorterar från högsta till lägsta
    private static void sortAndPrintPrices() {

        int[][] sortedPrices = new int[HOURS_IN_A_DAY][2];

        for (int i = 0; i < HOURS_IN_A_DAY; i++) {
            sortedPrices[i][0] = i;
            sortedPrices[i][1] = prices[i];
        }

        Arrays.sort(sortedPrices, (a, b) -> Integer.compare(b[1], a[1]));

        System.out.print("Timmar sorterade från dyrast till billigast:\n");
        for (int[] hourPrice : sortedPrices) {
            System.out.print(formatTime(hourPrice[0]) + " " + hourPrice[1] + " öre\n");
        }
    }




    // Metod för att hitta bästa värdena under 4h
    private static void displayBest4HourPeriod() {
        Locale.setDefault(new Locale("sv", "SE"));
        int bestStartHour = 0;
        double lowestAverage = Double.MAX_VALUE;
        for (int i = 0; i <= HOURS_IN_A_DAY - 4; i++) {
            int sum = prices[i] + prices[i + 1] + prices[i + 2] + prices[i + 3];
            double average = (double) sum / 4;
            if (average < lowestAverage) {
                lowestAverage = average;
                bestStartHour = i;
            }
        }

        System.out.print("Påbörja laddning klockan " + bestStartHour + "\n");
        System.out.printf("Medelpris 4h: %.1f öre/kWh\n", lowestAverage);
    }

    private static String formatTimeForVisual(int hour) {
        return String.format("%02d-%02d", hour, hour + 1);
    }
    private static void visualizePrices() {
        int maxPris = Arrays.stream(prices).max().orElse(1);
        int minPris = Arrays.stream(prices).min().orElse(1);
        final int HEIGHT = 6;
        final int COLUMN_COUNT = prices.length;
        final float DIFFERENCE = (maxPris - minPris) / (HEIGHT - 1f);

        System.out.println("Visualisering av elpriser:");


        for (int i = HEIGHT; i > 0; i--) {
            StringBuilder output = new StringBuilder();
            int lowerBound = (i == 1) ? minPris : (int) (maxPris - (HEIGHT - i) * DIFFERENCE);

            int maxLength = Integer.toString(maxPris).length();
            int minLength = Integer.toString(minPris).length();
            int longest = Math.max(maxLength, minLength);


            if (i == HEIGHT) {
                String spaces = maxLength < longest ? addSpaces(longest - maxLength) : "";
                output.append(spaces).append(maxPris).append("|");
            } else if (i == 1) {
                String spaces = minLength < longest ? addSpaces(longest - minLength) : "";
                output.append(spaces).append(minPris).append("|");
            } else {
                output.append(addSpaces(longest)).append("|");
            }


            for (int j = 0; j < COLUMN_COUNT; j++) {
                int currentPrice = prices[j];
                if (currentPrice >= lowerBound) {
                    output.append("  x");
                } else {
                    output.append("   ");
                }
            }
            System.out.println(output);
        }


        System.out.print("   |------------------------------------------------------------------------\n");
        System.out.print("   | 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23\n");
    }


    private static String addSpaces(int count) {
        return " ".repeat(count);
    }


    private static String formatTime(int hour) {
        return String.format("%02d-%02d", hour, hour + 1);
    }

}
