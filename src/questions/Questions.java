package questions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Questions {

    private String prompt;
    private String answer;


    private Questions[] listOfQuestions = new Questions[100];
    private int questionCount = 0;  // tracks how many questions were actually loaded

    public Questions(String prompt, String answer) {
        this.prompt = prompt;
        this.answer = answer;
    }


    public void loadQuestions(String fileName) {
        questionCount = 0;

        try {
            InputStream is = getClass().getResourceAsStream(fileName);
            if (is == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String linePrompt;
            while (questionCount < listOfQuestions.length
                    && (linePrompt = reader.readLine()) != null) {


                String lineAnswer = reader.readLine();  // second line is the answer
                if (lineAnswer == null) {
                    // If there's no answer line, stop reading
                    break;
                }


                listOfQuestions[questionCount++] = new Questions(linePrompt, lineAnswer);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Questions[] getListOfQuestions() {
        return listOfQuestions;
    }

    /** Returns how many questions were actually loaded. */
    public int getQuestionCount() {
        return questionCount;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getAnswer() {
        return answer;
    }
}