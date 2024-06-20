import java.util.ArrayList; 
import java.util.List; 

  
class BoyerMooreAlgorithm { 

    // Defines a constant to represent the number of possible characters in the input text and pattern 
    static final int NUM_CHARS = 256; 

    // Function to find the maximum of two integers 
    static int max(int a, int b) { 
        return Math.max(a, b); 

    } 

  

    // Bad Character Heuristic: Preprocesses the pattern to create the bad character table 
    static void badCharHeuristic(char[] pattern, int patternLength, int[] badChar) { 

        // Initialize all occurrences as -1 to indicate that the character does not exist in the pattern 
        for (int i = 0; i < NUM_CHARS; i++) { 

            badChar[i] = -1; 

        } 

        // Fill actual value of the last occurrence of a character in the pattern 
        for (int i = 0; i < patternLength; i++) { 

            badChar[pattern[i]] = i; 

        } 

    }  

    // Good Suffix Heuristic: Preprocesses the pattern to create shift and border position arrays 

    static void goodSuffixHeuristic(char[] pattern, int patternLength, int[] shift, int[] borderPosition) { 

        int i = patternLength, j = patternLength + 1; 

        borderPosition[i] = j; // Setting the border position 
        // Preprocess to create border position array 
        while (i > 0) { 

            while (j <= patternLength && pattern[i - 1] != pattern[j - 1]) { 

                if (shift[j] == 0) { 
                    shift[j] = j - i; 
                } 
                j = borderPosition[j]; 

            } 
            i--; 
            j--; 
            borderPosition[i] = j; 

        } 

        // Fill the shift array for the border cases 
        for (int k = 0; k <= patternLength; k++) { 

            if (shift[k] == 0) { 
                shift[k] = j; 
            } 

        } 

    } 

    // Boyer-Moore algorithm with Bad Character Heuristic 
    static void searchPatternBadChar(char[] text, char[] pattern, ComparisonResult result) { 

        int patternLength = pattern.length; 
        int textLength = text.length; 
        int[] badChar = new int[NUM_CHARS]; 

        // Preprocess the pattern using the bad character heuristic 
        badCharHeuristic(pattern, patternLength, badChar); 

        // shiftIndex defines how much pattern is shifted 
        int shiftIndex = 0; 

        // Search the pattern in the text 

        while (shiftIndex <= (textLength - patternLength)) { 

            int j = patternLength - 1; 

            // Compare the pattern with the current segment of the text 
            // Reducing j or pattern until getting a mistmatch character 
            while (j >= 0 && pattern[j] == text[shiftIndex + j]) { 

                j--; 
                result.comparisons++; 

            } 

  

            // Incase if we get j = -1 which signify that pattern is present at the current shift 
            if (j < 0) { 

                result.positions.add(shiftIndex); 
                // Shift the pattern to align the next character in the text with its last occurrence in the pattern 
                shiftIndex += (shiftIndex + patternLength < textLength) ? patternLength - badChar[text[shiftIndex + patternLength]] : 1; 
                result.shifts++; 

            } else { 

               // Calculate the shift using the bad character heuristic 
                int badCharShift = j - badChar[text[shiftIndex + j]]; 
                shiftIndex += max(1, badCharShift); 
                result.shifts++; 
                result.comparisons++; 

            } 

        } 

    } 

    // Boyer-Moore algorithm with Good Suffix Heuristic 
    static void searchPatternGoodSuffix(char[] text, char[] pattern, ComparisonResult result) { 

        int patternLength = pattern.length; 
        int textLength = text.length; 
        int[] shift = new int[patternLength + 1]; 
        int[] borderPosition = new int[patternLength + 1]; 

        // Preprocess the pattern using the good suffix heuristic 
        goodSuffixHeuristic(pattern, patternLength, shift, borderPosition); 

        int shiftIndex = 0; 

        // Search the pattern in the text 
        while (shiftIndex <= (textLength - patternLength)) { 

            int j = patternLength - 1; 

            // Compare the pattern with the current segment of the text 
            while (j >= 0 && pattern[j] == text[shiftIndex + j]) { 
                j--; 
                result.comparisons++; 
            } 

  

            if (j < 0) { 

                // Pattern found at shiftIndex 
                result.positions.add(shiftIndex); 
                shiftIndex += shift[0]; 
                result.shifts++; 

            } else { 

               // Calculate the shift using the good suffix heuristic 
                shiftIndex += shift[j + 1]; 
                result.shifts++; 
                result.comparisons++; 

            } 

        } 

    } 

  

    public static void main(String[] args) { 

        char[] text = "GIVE ME A STAR".toCharArray(); 
        char[] pattern = "STAR".toCharArray(); 
        // Print the text and pattern 

        System.out.println("Text: " + new String(text)); 
        System.out.println("Pattern: " + new String(pattern)); 

        ComparisonResult badCharResult = new ComparisonResult(); 
        ComparisonResult goodSuffixResult = new ComparisonResult(); 

        // Perform search using Bad Character Heuristic 
        searchPatternBadChar(text, pattern, badCharResult); 

        // Perform search using Good Suffix Heuristic 
        searchPatternGoodSuffix(text, pattern, goodSuffixResult); 
    
        // Print results for Bad Character Heuristic 
        System.out.println("\nBoyer-Moore Algorithm with Bad Character Heuristic:"); 
        System.out.println("Total Comparisons : " + badCharResult.comparisons); 
        System.out.println("Total Shifts : " + badCharResult.shifts); 

        // Print results for Good Suffix Heuristic 
        System.out.println("\nBoyer-Moore Algorithm with Good Suffix Heuristic:"); 
        System.out.println("Total Comparisons : " + goodSuffixResult.comparisons); 
        System.out.println("Total Shifts : " + goodSuffixResult.shifts); 

  

        // Print pattern positions for both heuristics 
        if (badCharResult.positions.equals(goodSuffixResult.positions)) { 
            List<Integer> patternPosition = badCharResult.positions; 
            System.out.println("\nPattern Positions for both Bad Character and Good Suffix: " + patternPosition); 

        } 
 

        // Print comparison summary 
        System.out.println("\nComparison Summary:"); 

        if (badCharResult.comparisons < goodSuffixResult.comparisons) { 

            System.out.println("Bad Character Heuristic had fewer comparisons."); 

        } else if (badCharResult.comparisons > goodSuffixResult.comparisons) { 

            System.out.println("Good Suffix Heuristic had fewer comparisons."); 

        } else { 

            System.out.println("Both heuristics had the same number of comparisons."); 

        } 

        if (badCharResult.shifts < goodSuffixResult.shifts) { 

            System.out.println("Bad Character Heuristic had fewer shifts."); 

        } else if (badCharResult.shifts > goodSuffixResult.shifts) { 

            System.out.println("Good Suffix Heuristic had fewer shifts."); 

        } else { 

            System.out.println("Both heuristics had the same number of shifts."); 

        } 

    } 

  

    // Helper class to store the comparison results 
    static class ComparisonResult { 

        int comparisons = 0; 
        int shifts = 0; 
        List<Integer> positions = new ArrayList<>(); 

    } 

} 