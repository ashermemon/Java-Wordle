import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Scanner;

//Imports for the scanner, reading Json file and for the API


public class WordleTest { //Creates a class containing the app


    //Defining variables for the strings to change text color in the terminal
    public static final String RESET = "\u001B[0m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String CYAN = "\u001B[36m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";

    public static final String BLACK = "\u001B[30m";


    public static final String YELLOWBG = "\u001B[43m";
    public static final String GREENBG = "\u001B[42m";
    public static final String WHITEBG = "\u001B[47m";



    public static void CreateKeyboard(StringBuilder[] guesses, char[] yellows, char[] greens)//Defining a method that creates a visible keyboard in the terminal
    //the method takes in the user's guesses, and the yellow and green letters
    {
        String yellowsStr = new String(yellows); //Converting the character arrays to strings to use indexOf func
        String greensStr = new String(greens);
        String guess = String.join("", guesses); //join all the guesses into one string to check for letters across all guesses
        char[] row = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'}; //The order of letters on the keyboard to print

        for(int i =0; i<row.length; i++){ //for loop repeats for each character in the array row. Basically checking and printing each letter
            if(i==10 || i== 19){ //Add new lines after each line on the keyboard
                System.out.println("\n");
            }
            if(guess.indexOf(row[i]) != -1){ //guess contains the letter
                if(greensStr.indexOf(row[i]) != -1){ //Check if the letter is a part of the green letters (returns -1 if not found)
                    System.out.print(GREENBG + " " + row[i] + " " + RESET + " "); //Print the green letter
                }
                else if(yellowsStr.indexOf(row[i]) != -1){ //Check i fthe letter is a part of the yellow letters
                    System.out.print(YELLOWBG + " " + row[i] + " " + RESET + " "); //Prints the yellow letter
                }else{
                    System.out.print(RED + " " + row[i] + " " + RESET + " "); //Otherwise the letter is not in the target word so print it in red
                }
                
            }
            else{
                System.out.print(WHITEBG + BLACK + " " + row[i] + " " + RESET + " "); //Else the letter hasnt been guessed at all yet, display it as whitebg, black color
            }
        }

        System.out.println("\n"); //Two extra newlines for spacing
        System.out.println("\n");
    }

    public static void ResetConsole() //Defining a function to clear the console 
    {
         System.out.println("\033[H\033[2J");
         System.out.flush();
    }


    public static boolean isRealWord(String wordToCheck){ // Method to check if the word is real, takes in the word to check and returns a boolean
        try {
            String urlStr = "https://api.dictionaryapi.dev/api/v2/entries/en/" + wordToCheck.toLowerCase(); //lowercase the word and add it to the end of the link
            URL url = new URL(urlStr); //convert the String to a URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Open a connection
            connection.setRequestMethod("GET"); //requesting data from the server

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); //Revieves data and converts to text
            StringBuilder response = new StringBuilder(); //creates a string builder to update the string efficiently
            for (String line; (line = in.readLine()) != null;) { // read the entire response, line by line
                response.append(line);
            }
            in.close(); //close the connection stream

            return !response.toString().contains("\"title\":\"No Definitions Found\""); //If the word exists in the dictionary return true, else return false

        } catch (Exception e) { //if error is caught, just return the word as non existent
            return false;
        }
    }


    public static void waiter(double seconds){ // defines a method that waits for a certain amount of time. Takes in seconds which it converts to milliseconds for the sleep functionality
        try {
            Thread.sleep((long) (seconds * 1000)); //wait
        } catch (InterruptedException e) { //required error checker thingy
            e.printStackTrace();
        }

    }

    public static void printGuesses(StringBuilder[] guesses){ //defines a method to print the guesses with a slight delay between each one (for the start of the game)
        for(int i=0; i<guesses.length; i++){
            
            System.out.println(guesses[i].toString());
            waiter(0.25);
        }
    }

    public static void playGame (Scanner input) throws IOException { //defines main game loop, takes in the scanner


            ResetConsole();
            System.out.println("Welcome to " + GREEN + "Wordle!" + RESET);
    
            waiter(2);



            //String[] dictionary = {"Cakes", "Bakes", "Alone", "Crane"};


            String content = Files.readString(Path.of("wordchoice.json")); //Read the json file of all the possible words
            content = content.substring(1, content.length() - 1); 
            String[] dictionary = content.replace("\"", "").split(",");

            int randomInt = (int) (Math.random() * dictionary.length); // random number generator between 0 and the dictionaries length
            String target_word = dictionary[randomInt]; //Selects the random word from the dictionary and assigns it to String target_word
     
            
            target_word = target_word.toUpperCase(); //Makes the target word uppercase for consistency
            
            StringBuilder[] guesses =new StringBuilder[6]; //creates an array of string builders; one string builder for each guess
            char[] yellows = new char[5]; //Creates an empty character array to hold the yellow letters
            char[] greens = new char[5]; //Creates an empty character array to hold the greeen letters

            ResetConsole();
            System.out.println("You have 6 guesses to correctly identify the target word");
            System.out.println('\n');
            waiter(1);
            for(int i=0; i<guesses.length; i++){ //using a loop, assign each guess to be 'blank' with underscores 
                
                guesses[i].append("_ _ _ _ _ \n");

                
            }
            
            printGuesses(guesses); //print all the guesses using the function from before
            System.out.println('\n'); 
            CreateKeyboard(guesses, yellows, greens); //calls the create keyboard method from before to create the initial keyboard
            boolean won = false; //set won to false so it doesnt go to the win sequence automatically

            for(int j =0; j<guesses.length && !won; j++){ //loop which runs for each guess
                
                System.out.println("Enter your guess below!");
                System.out.println('\n');
                int count = 0;

                while(guesses[j].length() != 5 || count != 5 || isRealWord(guesses[j].toString()) == false){
                    

                    guesses[j].append(input.next().trim()); //Waits for a user input
                    input.nextLine(); 
                    

                    
                    count = guesses[j].toString().replaceAll("[^A-Za-z]", "").length(); //counts all the a to z or A to Z characters
                
                    System.out.print("\033[1A\033[K"); //Delete last line
                    if (!guesses[j].toString().contains(" ") && guesses[j].length() == 5 && count == 5 && isRealWord(guesses[j].toString()) == true) break; //if the word doesnt have a space, symbols, and is a real word, then break
                    System.out.println("Try again, your guess must be an english 5 letter word (a-z or A-Z)!"); //otherwise tell them to try again and restart from the start of the loop
                }   

    

                ResetConsole();
                System.out.println("Guesses:" + "\n");
                
                guesses[j].append(guesses[j].toString().toUpperCase()); //make user word uppercase to match the case of the target word

                if(guesses[j].toString().equals(target_word)) // check if the guess is the same as the target
                {
                    winSequence(j+1); //call the win sequence function with the number of attempts (j+1)
                    won= true; 
                    break; //leave the loop
                    
                }

                for(int i=0; i<=j; i++){ //Colouring logic plays after the guess, reprinting all previous guesses
                 
                    char[] targetCopy = target_word.toCharArray();
                    
                    for(int x=0; x<guesses[i].length(); x++)
                    {
                        if(guesses[i].charAt(x) == targetCopy[x]) //if the letter in the guess is in the same place in the target word
                        {
                            targetCopy[x] =  '-'; //occupy the spot in the copy (to count quantity of green letters properly)
                            guesses[i].append(GREEN + guesses[i].charAt(x) + RESET + " "); //Print the letter as green
                            
                            String greensStr = new String(greens);
                            if(greensStr.indexOf(guesses[i].charAt(x)) == -1){ //i f the letter isnt already in the char array
                                greens[x] = guesses[i].charAt(x); // add the letter to the array (for keyboard colouring)
                            }
                            
                        }
                        else{ //if the letter isnt green
                            
                            String targetCopyStr = new String(targetCopy);
                            int index = targetCopyStr.indexOf(guesses[i].charAt(x));
                            if (index >= 0){ //if the word contains the letter
                                targetCopy[index] = '-'; //occupt the spot in the copy (to count quantity of yellow letters properly)
                                guesses[i].append(YELLOW + guesses[i].charAt(x) + RESET  + " "); //Print the letter as yellow
                                String yellowsStr = new String(yellows); //Add it to the array yellows (for keyboard colouring)
                                if(yellowsStr.indexOf(guesses[i].charAt(x)) == -1){ //if the letter isnt already in the char array
                                    yellows[x] = guesses[i].charAt(x); //add it 
                                }
                                
                             
                            }
                            else { //if the letter isnt in the target word, print it without any colouring
                                guesses[i].append(guesses[i].charAt(x)  + " "); //else print it as normal (no effects)
                                
                            }
                            System.out.print(guesses[i]);
                            
    
                            
                  
                        }
                    }
                    System.out.println('\n');
        
                }
                for(int k = j + 1; k < guesses.length && !won; k++){ //print the remaining empty guesses
                    System.out.println(guesses[k]);
                    
                }
                System.out.println('\n');
                CreateKeyboard(guesses, yellows, greens); //create the keyboard again with the updated colouring

            }
            if(won == false){ //if there are no more attempts and the user hasnt won
                waiter(1);
                System.out.println("You lost and ran out of attempts :(");
                waiter(1);
                System.out.println("The target word was " + RED + target_word + RESET);
            }
            waiter(1);
            System.out.println('\n');
            
    }

    public static void winSequence(int attempts){ //defines a method for when the player wins
        
        ResetConsole();
        System.out.println(CYAN + "Congrats! You win!" + RESET);
        waiter(0.5);
        System.out.println('\n');
        System.out.println("Guessed in " + GREEN + (attempts) + " attempt(s)" + RESET); //tell the user how many attempts they won in
                        
    }


    public static void main(String[] args) throws Exception { // main method which is outside of the game loop so that when the game restarts, this code is not run

        ResetConsole(); // reset console

        Scanner input = new Scanner(System.in); //create a scanner for inputs

        System.out.println("Press P to Play Wordle!");
        String pCheck = input.next(); //wait for user input
        


        while(!pCheck.contains(String.valueOf('p')) && !pCheck.contains(String.valueOf('P'))){ //if the user did not respond with p or P
            System.out.print("\033[1A\033[K"); //Delete last line 
            pCheck = input.next(); //prompt them again

        }
            

        ResetConsole();
        System.out.println("P key pressed. Starting game...");


        waiter(2);

        boolean playing = true; //boolean to either play the game loop or exit the program
        while (playing == true) { // as long as playing is true
            playGame(input); //calls on the game loop and passes in the scanner

            System.out.println(BLUE + "Play Again? (Y or N)" + RESET); //after the game is completed, ask the user if they want to play again
            System.out.println('\n');
            boolean responseGiven = false;
            while(responseGiven == false){ // as long as the user doesn't reply
            String playAgain = input.next(); // wait for input
                if(playAgain.toUpperCase().charAt(0) == 'Y'){ //check if the input is a yes
                    ResetConsole(); 
                    System.out.println("Loading...");
                    waiter(1);
                    responseGiven = true; 
                    playing = true; //essentially reset the game loop
                    break;
                }
                else if (playAgain.toUpperCase().charAt(0) == 'N'){ //check if the user responded with a no
                    System.out.println('\n');
                    System.out.println("Thanks for playing!");
                    System.out.println('\n');
                    responseGiven = true;
                    playing = false; //break out from the loop, essentially ending the program
                    break; 
                }
                else { //otherwise delete the previous line and wait again for a response
                    System.out.print("\033[1A\033[K"); //Delete last line 
                        
                }
            }
        }
        
    }
}
