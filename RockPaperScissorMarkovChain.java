import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;
import java.text.DecimalFormat;

public class RockPaperScissorMarkovChain {

  //choices that can be thrown
  public enum Item {
    ROCK, PAPER, SCISSORS;

    public List<Item> willLoseTo;

    public boolean willLoseTo(Item other) {
      return willLoseTo.contains(other);
    }

    //which item loses to the other choice (throw)
    static {
      ROCK.willLoseTo = Arrays.asList(PAPER);
      PAPER.willLoseTo = Arrays.asList(SCISSORS);
      SCISSORS.willLoseTo = Arrays.asList(ROCK);
    }
  }

  // Markov Chain for the AI of our computer
  private int[][] markovChain; // used to increment when one state goes to another for prediction
  private int numberThrows = 0;
  private Item last = null;
  private static DecimalFormat df = new DecimalFormat(".##");
  public static final Random RANDOM = new Random();
  // data of the games played (you win / tie / computer win)
  private int[] gameData = new int[] {0, 0, 0};

  //creating the matrix
  private void init() {
    int length = Item.values().length;
    markovChain = new int[length][length];

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < length; j++) {
        markovChain[i][j] = 0;
      }
    }
  }

  //update the matrix when an item has been thrown going from one throw to another
  private void updateMarkovChain(Item previous, Item next) {
    markovChain[previous.ordinal()][next.ordinal()]++;
  }

  //get the throw chosen by the computer (used to also check who the winner is based on what the player and AI has thrown)
  private Item nextMove(Item previous) {
    if (numberThrows < 1) {
      // first move, we can't use Markov Chain prediction
      // so we use a random on the Item list
      return Item.values()[RANDOM.nextInt(Item.values().length)];
    }

    // we try to predict next Item chosen by the user by reading data in our Markov Chain
    // for the previous entry in the array
    int nextIndex = 0;
    for (int i = 0; i < Item.values().length; i++) {
      int previousIndex = previous.ordinal();

      if (markovChain[previousIndex][i] > markovChain[previousIndex][nextIndex]) {
        nextIndex = i;
      }
    }

    // Item most likely played by the user is in nextIndex
    Item nextThrowPrediction = Item.values()[nextIndex];
    // choose within item for which most likely the player choice will lose to
    List<Item> willLoseTo = nextThrowPrediction.willLoseTo;
    return willLoseTo.get(RANDOM.nextInt(willLoseTo.size()));
  }

  public void play() {
    init();

    //takes in user input
    Scanner in = new Scanner(System.in);
    System.out.print("Please choose either ROCK, PAPER or SCISSORS: ");

    while (in.hasNextLine()) {
      String input = in.nextLine();

      //is STOP is will stop the game and display the stats.
      if ("STOP".equals(input))
        break;

      // read user choice
      Item choice;

      //validation check
      try {
        choice = Item.valueOf(input.toUpperCase());
      } catch (Exception e) {
        System.out.println("Invalid choice");
        continue;
      }

      Item computerChoice = nextMove(last);
      numberThrows++;

      // update Markov Chain
      if (last != null) {
        updateMarkovChain(last, choice);
      }

      last = choice;
      System.out.println("Computer choice : " + computerChoice);

      //checks who has won
      if(computerChoice.willLoseTo(choice)) {
        System.out.println("You win!\n");
        gameData[0]++;
      } else if (computerChoice.equals(choice)) {
        System.out.println("Tie!\n");
        gameData[1]++;
      } else {
        System.out.println("You lose!\n");
        gameData[2]++;
      }

      System.out.print("Please choose your next throw: ");
    }

    in.close();

    // display stats of all the games that have been played
    System.out.println("\n");
    System.out.println("Stats of the games played");
    int total = gameData[0] + gameData[1] + gameData[2];
    System.out.println("You won: " + gameData[0] + " game(s) giving a win percentage of: " + df.format(gameData[0] / (float) total * 100f) + "%");
    System.out.println("This many games tied : " + gameData[1] + " with a percentage of " + df.format(gameData[1] / (float) total * 100f) + "%");
    System.out.println("Computer won: " + gameData[2] + " game(s) giving a win percentage of " + df.format(gameData[2] / (float) total * 100f) + "%");
  }

  public static void main(String[] args) {
    RockPaperScissorMarkovChain rps = new RockPaperScissorMarkovChain();
    rps.play();
  }
}