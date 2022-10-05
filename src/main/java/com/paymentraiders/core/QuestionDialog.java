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

public class QuestionDialog extends CancelAndHelpDialog {
    
        /**
     * The constructor of the Booking Dialog class.
     */
    public QuestionDialog() {
        super("QuestionDialog");

        addDialog(new TextPrompt("TextPrompt"));
        addDialog(new ConfirmPrompt("ConfirmPrompt"));
        WaterfallStep[] waterfallSteps = {
            this::questionStep,
            this::checkAnswerStep
        };
        addDialog(new WaterfallDialog("WaterfallDialog", Arrays.asList(waterfallSteps)));

        // The initial child Dialog to run.
        setInitialDialogId("WaterfallDialog");
    }

    private CompletableFuture<DialogTurnResult> questionStep(WaterfallStepContext stepContext) {
        QuizDetails quizDetails = (QuizDetails) stepContext.getOptions();

        if (quizDetails.getCurrentQuestion() < quizDetails.getQuestions().size()) {
            QuestionDetails question = quizDetails.getQuestions().get(quizDetails.getCurrentQuestion());
            String questionString = question.getQuestion();
            
            for (int i = 0; i < question.getAnswerOptions().size(); i++) {
                questionString = questionString + "\n" + i + ") " + question.getAnswerOptions().get(i);
            }

            Activity promptMessage =
                MessageFactory.text(questionString, questionString,
                    InputHints.EXPECTING_INPUT
                );

            PromptOptions promptOptions = new PromptOptions();
            promptOptions.setPrompt(promptMessage);
            return stepContext.prompt("TextPrompt", promptOptions);
        }

        return stepContext.next(quizDetails.getCurrentAnswer());
    }

    private CompletableFuture<DialogTurnResult> checkAnswerStep(WaterfallStepContext stepContext) {
        QuizDetails quizDetails = (QuizDetails) stepContext.getOptions();

        quizDetails.setCurrentAnswer((String) stepContext.getResult());

        quizDetails.setCurrentAnswer((String) stepContext.getResult());
        if (quizDetails.getCurrentAnswer() == quizDetails.getQuestions().get(quizDetails.getCurrentQuestion()).getCorrectAnswer()) {
            quizDetails.markCorrect();
        } else {
            quizDetails.markIncorrect();
        }

        if ((Boolean) stepContext.getResult()) {
            BookingDetails bookingDetails = (BookingDetails) stepContext.getOptions();
            return stepContext.endDialog(bookingDetails);
        }

        return stepContext.endDialog(null);
    }
}
