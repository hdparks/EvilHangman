package hangman;

import java.io.File;
import java.util.Set;

public class EvilHangmanGame implements IEvilHangmanGame {

    @Override
    public void startGame(File dictionary, int wordLength) {
        
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        return null;
    }
}
