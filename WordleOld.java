import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Wordle {



    public static final String RESET = "\u001B[0m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String CYAN = "\u001B[36m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";

    public static void ResetConsole()
    {
         System.out.println("\033[H\033[2J");
         System.out.flush();
    }


    public static boolean isRealWord(String wordToCheck){
        try {
            String urlStr = "https://api.dictionaryapi.dev/api/v2/entries/en/" + wordToCheck.toLowerCase();
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            for (String line; (line = in.readLine()) != null;) {
                response.append(line);
            }
            in.close();

            return !response.toString().contains("\"title\":\"No Definitions Found\"");

        } catch (Exception e) {
            return false;
        }
    }



    public static void printGuesses(String[] guesses){
        for(int i=0; i<guesses.length; i++){
            
            System.out.println(guesses[i]);
            waiter(0.25);
        }
    }

    public static void waiter(double seconds){
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public static void playGame (Scanner input) throws IOException {


            ResetConsole();
            System.out.println("Welcome to " + GREEN + "Wordle!" + RESET);
    
            waiter(2);



            //String[] dictionary = {"Cakes", "Bakes", "Alone", "Crane"};


            String content = Files.readString(Path.of("wordchoice.json"));
            content = content.substring(1, content.length() - 1);
            String[] dictionary = content.replace("\"", "").split(",");

            int randomInt = (int) (Math.random() * dictionary.length);
            String target_word = dictionary[randomInt];
            target_word = target_word.toUpperCase();
            
            String[] guesses = new String[6];

            ResetConsole();
            System.out.println("You have 6 guesses to correctly identify the target word");
            System.out.println('\n');
            waiter(1);
            for(int i=0; i<guesses.length; i++){
                
                guesses[i] = "_____";
                
            }

            printGuesses(guesses);
            System.out.println('\n');
            boolean won = false;

            for(int j =0; j<guesses.length && !won; j++){
                
                System.out.println("Enter your guess below!");
                System.out.println('\n');
                int count = 0;

                while(guesses[j].length() != 5 || count != 5 || isRealWord(guesses[j]) == false){
                    

                    guesses[j] = input.next().trim();
                    input.nextLine(); 
                    

                    
                    count = guesses[j].replaceAll("[^A-Za-z]", "").length();
                
                    System.out.print("\033[1A\033[K");
                    if (!guesses[j].contains(" ") && guesses[j].length() == 5 && count == 5 && isRealWord(guesses[j]) == true) break;
                    System.out.println("Try again, your guess must be an english 5 letter word (a-z or A-Z)!");
                }   

    

                ResetConsole();
                System.out.println("Guesses:");
                
                guesses[j] = guesses[j].toUpperCase();

                if(guesses[j].equals(target_word))
                {
                    winSequence(j+1);
                    won= true;
                    break;
                    
                }

                for(int i=0; i<=j; i++){
                 
                    char[] targetCopy = target_word.toCharArray();
                    
                    for(int x=0; x<guesses[i].length(); x++)
                    {
                        if(guesses[i].charAt(x) == targetCopy[x])
                        {
                            targetCopy[x] =  '-';
                            System.out.print(GREEN + guesses[i].charAt(x) + RESET);
             
                        }
                        else{
                            
                            String targetCopyStr = new String(targetCopy);
                            int index = targetCopyStr.indexOf(guesses[i].charAt(x));
                            if (index >= 0){
                                targetCopy[index] = '-';
                                System.out.print(YELLOW + guesses[i].charAt(x) + RESET);
                       
                             
                            }
                            else {
                                System.out.print(guesses[i].charAt(x));
                            }
                            
    
                            
                  
                        }
                    }
                    System.out.println('\n');
        
                }
                for(int k = j + 1; k < guesses.length && !won; k++){
                    System.out.println(guesses[k]);
                    
                }

            }
            if(won == false){
                waiter(1);
                System.out.println("You lost and ran out of attempts :(");
                waiter(1);
                System.out.println("The target word was " + RED + target_word + RESET);
            }
            waiter(1);
            System.out.println('\n');
            
    }

    public static void winSequence(int attempts){
        
        ResetConsole();
        System.out.println(CYAN + "Congrats! You win!" + RESET);
        waiter(0.5);
        System.out.println('\n');
        System.out.println("Guessed in " + GREEN + (attempts) + " attempt(s)" + RESET);
                        
    }


    public static void main(String[] args) throws Exception {

        ResetConsole(); // reset console

        Scanner input = new Scanner(System.in);

        System.out.println("Press P to Play Wordle!");
        String pCheck = input.next();
        


        while(!pCheck.contains(String.valueOf('p')) && !pCheck.contains(String.valueOf('P'))){
            System.out.print("\033[1A\033[K"); //Delete last line 
            pCheck = input.next();

        }
            

        ResetConsole();
        System.out.println("P key pressed. Starting game...");


        waiter(2);

        boolean playing = true;
        while (playing == true) {
            playGame(input);

            System.out.println(BLUE + "Play Again? (Y or N)" + RESET);
            System.out.println('\n');
            boolean responseGiven = false;
            while(responseGiven == false){
            String playAgain = input.next();
                if(playAgain.toUpperCase().charAt(0) == 'Y'){
                    ResetConsole();
                    System.out.println("Loading...");
                    waiter(1);
                    responseGiven = true;
                    playing = true;
                    break;
                }
                else if (playAgain.toUpperCase().charAt(0) == 'N'){
                    System.out.println('\n');
                    System.out.println("Thanks for playing!");
                    System.out.println('\n');
                    responseGiven = true;
                    playing = false;
                    break;
                }
                else {
                    System.out.print("\033[1A\033[K"); //Delete last line 
                        
                }
            }
        }
        
    }
}
