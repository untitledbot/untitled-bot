package dev.alexisok.untitledbot.modules.basic.brainfreak;

/**
 * My implementation of Brainf**k.
 * 
 * https://en.wikipedia.org/wiki/Brainfuck
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class BFToNormalString {
    
    private static final int MAX_BYTES = 32768;
    
    /**
     * Translate...
     * @param bf the brain f input
     * @return the output
     */
    @SuppressWarnings("StringConcatenationInLoop")
    public static String output(String bf) {
        bf = bf.replace("\n", "");
        String returnString = "";
        byte[] memory = new byte[MAX_BYTES];
        int pointer = 0;
        int temp = 0;
        int timesRun = 0;
        int limitRun = 65536;
        int unknownCharacters = -10; //-10 because of the command name.
        
        long startTime = System.nanoTime();
        
        for(int i = 0; i < bf.length(); i++) {
            if(timesRun >= limitRun) {
                returnString = "Computation time of " + limitRun + " exceeded.";
                break;
            }
            timesRun++;
            switch(bf.charAt(i)) {
                case '>':
                    pointer = pointer == MAX_BYTES - 1
                            ? 0
                            : pointer + 1;
                    break;
                case '<':
                    pointer = pointer == 0
                            ? MAX_BYTES - 1
                            : pointer - 1;
                    break;
                case '+':
                    memory[pointer]++;
                    break;
                case '-':
                    memory[pointer]--;
                    break;
                case '.':
                    returnString += ((char) memory[pointer]);
                    break;
                case '[':
                    if(memory[pointer] == 0) {
                        i++;
                        while(temp > 0 || bf.charAt(i) != '[') {
                            if(bf.charAt(i) == ']') temp--;
                            if(bf.charAt(i) == '[') temp++;
                            i++;
                        }
                        i++;
                    }
                    break;
                case ']':
                    if(memory[pointer] != 0) {
                        i--;
                        while(temp > 0 || bf.charAt(i) != '[') {
                            if(bf.charAt(i) == '[') temp--;
                            if(bf.charAt(i) == ']') temp++;
                            i--;
                        }
                        i--;
                    }
                    break;
                default:
                    unknownCharacters++;
                    break;
            }
        } //end for
        
        //get the MS it took to run the BF thing
        long totalTimeMS = (System.nanoTime() - startTime) / 1000000;
        
        returnString += "\n\n\nLoops: " + timesRun + " out of " + limitRun + " (" + ((int) Math.ceil(((double) timesRun / (double) limitRun) * 100.0)) + "%).";
        
        if(unknownCharacters > 0)
            returnString += "\nUnknown characters: " + unknownCharacters + ".";
        
        returnString += "\nExecution time: " + totalTimeMS + "ms.";
        
        return returnString;
    }
    
}
