package com.paymentraiders.core;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

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
    public QuestionDialog(@Nullable String id) {
        super(id != null ? id : "QuestionDialog");
        System.out.println("QuestionDialog::QuestionDialog: id: " + id);
        addDialog(new TextPrompt("TextPrompt"));
        addDialog(new ConfirmPrompt("ConfirmPrompt"));
        WaterfallStep[] waterfallSteps = {
            this::askQuestionStep,
            this::checkAnswerStep,
            this::finalStep
        };
        addDialog(new WaterfallDialog("WaterfallDialog", Arrays.asList(waterfallSteps)));

        // The initial child Dialog to run.
        setInitialDialogId("WaterfallDialog");
    }

    private CompletableFuture<DialogTurnResult> askQuestionStep(WaterfallStepContext stepContext) {
        System.out.println("QuestionDialog::askQuestionStep: Starting askQuestionStep");

        QuizDetails quizDetails = (QuizDetails) stepContext.getOptions();

        System.out.println("QuestionDialog::askQuestionStep: quizDetails.getCurrentQuestion: " + quizDetails.getCurrentQuestion());
        System.out.println("QuestionDialog::askQuestionStep: quizDetails.getQuestions().size(): " + quizDetails.getQuestions().size());

        if (quizDetails.getCurrentQuestion() < quizDetails.getQuestions().size()) {
            System.out.println("QuestionDialog::askQuestionStep: prepping the question");
            QuestionDetails question = quizDetails.getQuestions().get(quizDetails.getCurrentQuestion());
            String questionString = question.getQuestion();
            
            for (int i = 0; i < question.getAnswerOptions().size(); i++) {
                questionString = questionString + "\n\n" + i + ") " + question.getAnswerOptions().get(i);
            }

            System.out.println("QuestionDialog::askQuestionStep: questionString: " + questionString);

            Activity promptMessage =
                MessageFactory.text(questionString, questionString,
                    InputHints.EXPECTING_INPUT
                );

            PromptOptions promptOptions = new PromptOptions();
            promptOptions.setPrompt(promptMessage);

            System.out.println("QuestionDialog::askQuestionStep: prompting with question");
            return stepContext.prompt("TextPrompt", promptOptions);
        }

        return stepContext.next(quizDetails.getCurrentAnswer());
    }

    private CompletableFuture<DialogTurnResult> checkAnswerStep(WaterfallStepContext stepContext) {
        System.out.println("QuestionDialog::checkAnswerStep: starting");
        QuizDetails quizDetails = (QuizDetails) stepContext.getOptions();

        quizDetails.setCurrentAnswer((String) stepContext.getResult());
        System.out.println("QuestionDialog::checkAnswerStep: setCurrentAnswer: " + quizDetails.getCurrentAnswer());

        if (quizDetails.getCurrentAnswer().equals(quizDetails.getQuestions().get(quizDetails.getCurrentQuestion()).getCorrectAnswer())) {
            System.out.println("QuestionDialog::checkAnswerStep: correct answer");
            quizDetails.markCorrect();
        } else {
            System.out.println("QuestionDialog::checkAnswerStep: incorrect answer");
            quizDetails.markIncorrect();
        }

        // if ((Boolean) stepContext.getResult()) {
        //     QuizDetails quizDetails = (QuizDetails) stepContext.getOptions();
        //     return stepContext.endDialog(quizDetails);
        // }
        System.out.println("QuestionDialog::checkAnswerStep: currentQuestion: " + quizDetails.getCurrentQuestion());
        return stepContext.next(quizDetails);
    }

    private CompletableFuture<DialogTurnResult> finalStep(WaterfallStepContext stepContext) {
        System.out.println("QuestionDialog::finalStep: starting");
        QuizDetails quizDetails = (QuizDetails) stepContext.getResult();
        System.out.println("QuestionDialog::finalStep: quizDetails.getCurrentQuestion(): " + quizDetails.getCurrentQuestion());
        return stepContext.endDialog(quizDetails);
    }
}
