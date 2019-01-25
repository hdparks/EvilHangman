package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class EvilHangmanGame implements IEvilHangmanGame {

    public Set<String> wordPool;

    public EvilHangmanGame() {
        wordPool = new HashSet<String>();
    }

    public static void printUsage(){
        System.out.println("Usage: java EvilHangmanGame dictionary wordLength guesses");
        System.out.println();
        System.out.println("dictionary: path to a text file with whitespace separated words");
        System.out.println("wordLength: (int) >= 2");
        System.out.println("guesses: (int) >= 1");
    }

    @Override
    public void startGame(File dictionary, int wordLength) {

        //  Read in dictionary file of words of length wordLength to Set<String> wordPool


        try(Scanner scin = new Scanner(dictionary)){

            scin.useDelimiter("[^A-Za-z]+");

            while(scin.hasNext()){
                String nextWord = scin.next();
                if (nextWord.length() == wordLength){
                    wordPool.add(nextWord);
                }
            }

        } catch(FileNotFoundException ex){
            System.out.println(ex.getMessage());
            EvilHangmanGame.printUsage();
        }

    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        return null;
    }
}
