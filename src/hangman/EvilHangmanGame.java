package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    public Set<String> wordPool = new TreeSet<>();

    public Set<String> guesses = new TreeSet<>();

    public int turnsLeft = 0;

    public static Comparator<String> evilPatternComparator =  new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            //  Fewest letters wins
            int o1Score = o1.replace("-","").length();
            int o2Score = o2.replace("-","").length();

            if(o1Score - o2Score != 0) return o1Score - o2Score;

            //  If: same number of letters
            //  Then: rightmost guessed letter wins
            for(int i = o1.length(); i > 0; i--){
                if(o1.charAt(i)==o2.charAt(i)) continue;
                if(o1.charAt(i)=='-') return 1;
                else return -1;
            }
            //  If identical, they compare equally
            return 0;
        }
    });


    public static void printUsage(){
        System.out.println("Usage: java EvilHangmanGame dictionary wordLength guesses");
        System.out.println();
        System.out.println("dictionary: path to a text file with whitespace separated words");
        System.out.println("wordLength: (int) >= 2");
        System.out.println("guesses: (int) >= 1");
    }

    public String printGuessed(){
        String g = "";
        for (String i : guesses){
            g += i + " ";
        }
        return g;
    }

    public void printPrompt(){
        System.out.println("You have "+turnsLeft+" guesses left");
        System.out.println("Used letters: "+this.printGuessed());
        System.out.println("Word: "+this.currentPattern);
        System.out.print("Enter guess: ");
    }


    @Override
    public void startGame(File dictionary, int wordLength) {

        //  Read in dictionary file of words of length wordLength to Set<String> wordPool

        try(Scanner scin = new Scanner(dictionary)){

            scin.useDelimiter("[^A-Za-z]+");

            while(scin.hasNext()){
                String nextWord = scin.next().toLowerCase();
                if (nextWord.length() == wordLength){
                    wordPool.add(nextWord);
                }
            }

        } catch(FileNotFoundException ex){
            System.out.println(ex.getMessage());
            EvilHangmanGame.printUsage();
            return;
        }


        //  Main game loop
        Scanner scin = new Scanner(System.in);
        String guess;
        while(this.turnsLeft > 0){
            this.printPrompt();
            guess = scin.next();

            //  Parse guess and update pool
            try{

                //  Must be one character long.
                if(guess.length() != 1) throw new InvalidGuessException("Invalid guess: (must be one letter)");

                //  Must be alphabetic character
                guess = guess.replace("[^A-Za-z]","");
                if(guess.isEmpty()) throw new InvalidGuessException("Invalid guess: (must be a letter)");

                //  Make everything lowercase
                guess = guess.toLowerCase();

                //  makeGuess (throws GuessAlreadyMadeException
                wordPool = makeGuess(guess.charAt(0));

            } catch(InvalidGuessException ex){
                System.out.println();
                System.out.println(ex.getMessage());
                continue;

            } catch(GuessAlreadyMadeException ex){
                System.out.println();
                System.out.println("You already used that letter.");
                continue;

            }

        }

    }

    public static String getWordPattern(String word, String letter){
        return word.replace("[^"+letter+"]","-");
    }


    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        String sGuess = Character.toString(guess);

        if (guesses.contains(sGuess)) throw new GuessAlreadyMadeException();

        //  Construct the map
        Map<String,Set<String>> patternMap = new HashMap<>();

        for (String word : wordPool){
            //  FOR EACH WORD:
            //  Find the pattern
            String pattern = EvilHangmanGame.getWordPattern(word,sGuess);


            //  Find its corresponding wordSet in patternMap
            Set<String> wordSet;
            if(!patternMap.containsKey(pattern)){
                wordSet = new TreeSet<>();
                patternMap.put(pattern,wordSet);
            } else {
                wordSet = patternMap.get(pattern);
            }
            //  Add word to wordSet
            wordSet.add(word);
        }

        //  Choose the next wordPool according to the evil algorithm

        List<String> patterns = new ArrayList(patternMap.keySet());

        //  Sort patterns by evil algorithm preference
        patterns.sort(EvilHangmanGame.evilPatternComparator);

        String winningPattern ="";
        int winningSize = 0;

        //  Iterate through map by sorted patterns.
        //  We now only need to pick the biggest list
        for(String pattern : patterns){
            if(patternMap.get(pattern).size() > winningSize){
                winningPattern = pattern;
                winningSize = patternMap.get(winningPattern).size();
            }
        }

        //  Output the result depending on the winning pattern.
        if (winningPattern.matches("-+")){
            //  No letters
            System.out.println("Sorry, there are no "+guess+"'s");
        } else {
            int numLetters = winningPattern.replace("-","").length();
            System.out.println("Yes, there is "+numLetters+" "+guess);
        }

        return patternMap.get(winningPattern);

    }


    public static class InvalidCommandLineArgumentException extends Exception{
        public InvalidCommandLineArgumentException(String s){
            super(s);
        }
    }

    public static class InvalidGuessException extends Exception{
        public InvalidGuessException(String s){
            super(s);
        }
    }

    public static class EmptyDictionaryException extends Exception {
        public EmptyDictionaryException(String s){
            super(s);
        }
    }

    public static void main(String[] args){
        //  Ensure we have valid dictionary, length, guesses.
        File dictionary;
        IEvilHangmanGame game = new EvilHangmanGame();

        try{
            //  Need all three arguments
            if(args.length != 3) throw new InvalidCommandLineArgumentException("Invalid command line arguments.");

            //  Word length: int >= 2
            if(Integer.parseInt(args[1]) < 2) throw new InvalidCommandLineArgumentException("Word length must be an int >= 2");

            //  Number of guesses: int >= 1
            if(Integer.parseInt(args[2]) < 1) throw new InvalidCommandLineArgumentException("Word length must be an int >= 1");

            //  Dictionary not empty
            dictionary = new File(args[0]);

            if(dictionary.length() == 0) throw new EmptyDictionaryException("The provided dictionary file is empty.");


        } catch(NumberFormatException ex){
            System.out.println(ex.getMessage());
            System.out.println("'wordLength' and 'guesses' must be integers");
            EvilHangmanGame.printUsage();
            return;

        } catch( InvalidCommandLineArgumentException | EmptyDictionaryException ex){
            System.out.println(ex.getMessage());
            EvilHangmanGame.printUsage();
            return;
        }

        //  If we have everything we need, we start the game:

        game.startGame(dictionary,Integer.parseInt(args[1]));

    }
}
