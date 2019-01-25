package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class EvilHangmanGame implements IEvilHangmanGame {

    public Set<String> wordPool = new TreeSet<>();

    public Set<String> guesses = new TreeSet<>();

    public int turnsLeft = 0;

    public String currentPattern = "";

    public EvilHangmanGame() {
    }

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
        }

    }


    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        if (guesses.contains(guess)) throw new GuessAlreadyMadeException();
        return null;
    }
}
