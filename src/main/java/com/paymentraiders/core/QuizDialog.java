package com.paymentraiders.core;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.WaterfallDialog;
import com.microsoft.bot.dialogs.WaterfallStep;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.dialogs.prompts.ConfirmPrompt;
import com.microsoft.bot.dialogs.prompts.PromptOptions;
import com.microsoft.bot.dialogs.prompts.TextPrompt;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.InputHints;

public class QuizDialog extends CancelAndHelpDialog {
    
    public QuizDialog() {
        super("QuizDialog");

        addDialog(new TextPrompt("TextPrompt"));
        addDialog(new ConfirmPrompt("ConfirmPrompt"));
        WaterfallStep[] waterfallSteps = {
            this::questionStep1,
            this::questionStep2,
            this::finalStep
        };
        addDialog(new WaterfallDialog("WaterfallDialog", Arrays.asList(waterfallSteps)));

        System.out.println("Initialized QuizDialog");

        // The initial child Dialog to run.
        setInitialDialogId("WaterfallDialog");
    }

    private CompletableFuture<DialogTurnResult> questionStep1(WaterfallStepContext stepContext) {

        System.out.println("Starting questionStep1");


        QuizDetails quizDetails = (QuizDetails) stepContext.getOptions();



        if (quizDetails.getCurrentQuestion() == 0) {
            System.out.println("Inside Question1 if statment");

            QuestionDetails question = quizDetails.getQuestions().get(0);

            System.out.println("question");

            String questionString = question.getQuestion();

            System.out.println(questionString);

            String questionAndAnswers = questionString 
                + " a) " + question.getAnswerOptions().get(0) 
                + " b) " + question.getAnswerOptions().get(1) 
                + " c) " + question.getAnswerOptions().get(2) 
                + " d) " + question.getAnswerOptions().get(3);

            System.out.println(questionAndAnswers);

            Activity promptMessage =
                MessageFactory.text(questionAndAnswers, questionAndAnswers,
                    InputHints.EXPECTING_INPUT
                );

            System.out.println(promptMessage.getText());

            PromptOptions promptOptions = new PromptOptions();
            promptOptions.setPrompt(promptMessage);
            return stepContext.prompt("TextPrompt", promptOptions);
        }

        System.out.println("Skipping loop, currentQuestion: " + quizDetails.getCurrentQuestion());

        return stepContext.next(quizDetails.getCurrentAnswer());
    }

    private CompletableFuture<DialogTurnResult> questionStep2(WaterfallStepContext stepContext) {
        QuizDetails quizDetails = (QuizDetails) stepContext.getOptions();

        quizDetails.setCurrentAnswer((String) stepContext.getResult());
        if (quizDetails.getCurrentAnswer().equals(quizDetails.getQuestions().get(0).getCorrectAnswer())) {
            quizDetails.markCorrect();
            System.out.println("The answer was correct");
        } else {
            quizDetails.markIncorrect();
            System.out.println("The answer was wrong");
        }

        if (quizDetails.getCurrentQuestion() == 1) {
            QuestionDetails question = quizDetails.getQuestions().get(1);
            String questionString = question.getQuestion();
            String questionAndAnswers = questionString + "\na) " + question.getAnswerOptions().get(0) + "\nb) " + question.getAnswerOptions().get(1);
            Activity promptMessage =
                MessageFactory.text(questionAndAnswers, questionAndAnswers,
                    InputHints.EXPECTING_INPUT
                );
            PromptOptions promptOptions = new PromptOptions();
            promptOptions.setPrompt(promptMessage);
            return stepContext.prompt("TextPrompt", promptOptions);
        }

        return stepContext.next(quizDetails.getCurrentAnswer());
    }


    private CompletableFuture<DialogTurnResult> finalStep(WaterfallStepContext stepContext) {
        QuizDetails quizDetails = (QuizDetails) stepContext.getOptions();

        quizDetails.setCurrentAnswer((String) stepContext.getResult());
        if (quizDetails.getCurrentAnswer().equals(quizDetails.getQuestions().get(quizDetails.getCurrentQuestion()).getCorrectAnswer())) {
            quizDetails.markCorrect();
            System.out.println("The answer was correct");
        } else {
            quizDetails.markIncorrect();
            System.out.println("The answer was wrong");
        }


        // if ((Boolean) stepContext.getResult()) {
        //     QuizDetails quizDetails = (quizDetails) stepContext.getOptions();
        //     return stepContext.endDialog(quizDetails);
        // }

        return stepContext.endDialog(quizDetails);
    }
}
