// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.paymentraiders.core;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.dialogs.ComponentDialog;
import com.microsoft.bot.dialogs.DialogTurnResult;
import com.microsoft.bot.dialogs.WaterfallDialog;
import com.microsoft.bot.dialogs.WaterfallStep;
import com.microsoft.bot.dialogs.WaterfallStepContext;
import com.microsoft.bot.dialogs.prompts.TextPrompt;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.InputHints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;


/**
 * The class containing the main dialog for the sample.
 */
public class MainDialog extends ComponentDialog {


    /**
     * The constructor of the Main Dialog class.
     *
     * @param withLuisRecognizer The FlightBookingRecognizer object.
     * @param bookingDialog      The BookingDialog object with booking dialogs.
     */
    public MainDialog(QuizDialog quizDialog) {
        super("MainDialog");

        addDialog(new TextPrompt("TextPrompt"));
        addDialog(quizDialog);
        WaterfallStep[] waterfallSteps = {
            this::introStep,
            this::actStep,
            this::finalStep
        };
        addDialog(new WaterfallDialog("WaterfallDialog", Arrays.asList(waterfallSteps)));

        // The initial child Dialog to run.
        setInitialDialogId("WaterfallDialog");
    }

    /**
     * First step in the waterfall dialog. Prompts the user for a command. Currently, this expects a
     * booking request, like "book me a flight from Paris to Berlin on march 22" Note that the
     * sample LUIS model will only recognize Paris, Berlin, New York and London as airport cities.
     *
     * @param stepContext A {@link WaterfallStepContext}
     * @return A {@link DialogTurnResult}
     */
    private CompletableFuture<DialogTurnResult> introStep(WaterfallStepContext stepContext) {
            Activity text = MessageFactory.text("Welcome to the Azure Learning Platform.  You will be asked 10 questions, please respond to each question with the full text of the chosen answer.", null, InputHints.IGNORING_INPUT);
            return stepContext.getContext().sendActivity(text)
                .thenCompose(sendResult -> stepContext.next(null));
    }

    /**
     * Second step in the waterfall.  This will use LUIS to attempt to extract the origin,
     * destination and travel dates. Then, it hands off to the bookingDialog child dialog to collect
     * any remaining details.
     *
     * @param stepContext A {@link WaterfallStepContext}
     * @return A {@link DialogTurnResult}
     */
    private CompletableFuture<DialogTurnResult> actStep(WaterfallStepContext stepContext) {
            
            // Initialize Quiz questiosn
            ArrayList<String> questionAnswers1 = new ArrayList<>();
            questionAnswers1.add("ISO");
            questionAnswers1.add("GDPR");
            questionAnswers1.add("CIS");
            questionAnswers1.add("ANSI");
            QuestionDetails question1 = new QuestionDetails("The ___________________ is a regulation in EU law on data protection and privacy in the European Union and the European Economic Area.", questionAnswers1, "GDPR");
            
            ArrayList<String> questionAnswers2 = new ArrayList<>();
            questionAnswers2.add("Public");
            questionAnswers2.add("Private");
            QuestionDetails question2 = new QuestionDetails("When computing and processing demand increases beyond an on-premises datacenter’s capabilities, businesses can easily use the ___________ cloud to instantly scale capacity up or down to handle excess capacity.", questionAnswers2, "Public");
            
            ArrayList<QuestionDetails> questions = new ArrayList<QuestionDetails>();
            questions.add(question1);
            questions.add(question2);

            QuizDetails quiz = new QuizDetails();
            quiz.setQuestions(questions);
            System.out.println("MainDialog::actStep: Calling QuizDialog");

            return stepContext.beginDialog("QuizDialog", quiz);
    }


    /**
     * This is the final step in the main waterfall dialog. It wraps up the sample "book a flight"
     * interaction with a simple confirmation.
     *
     * @param stepContext A {@link WaterfallStepContext}
     * @return A {@link DialogTurnResult}
     */
    private CompletableFuture<DialogTurnResult> finalStep(WaterfallStepContext stepContext) {
        CompletableFuture<Void> stepResult = CompletableFuture.completedFuture(null);

        // If the child dialog ("BookingDialog") was cancelled,
        // the user failed to confirm or if the intent wasn't BookFlight
        // the Result here will be null.
        if (stepContext.getResult() instanceof QuizDetails) {
            // Now we have all the booking details call the booking service.
            // If the call to the booking service was successful tell the user.
            QuizDetails quizDetails = (QuizDetails) stepContext.getResult();
            String messageText = String.format("You answered " + quizDetails.getCorrectCount() + " correct and " + quizDetails.getIncorrectCount() + " incorrect");
            System.out.println("MainDialog::finalStep: messageText: " + messageText);
            Activity message = MessageFactory
                .text(messageText, messageText, InputHints.IGNORING_INPUT);
            stepResult = stepContext.getContext().sendActivity(message).thenApply(sendResult -> null);
        }

        System.out.println("MainDialog::finalStep: reprompting with a different message");
        // Restart the main dialog with a different message the second time around
        String promptMessage = "What else can I do for you?";
        System.out.println("MainDialog::finalStep: getInitialDialogId: " + getInitialDialogId() + " id: " + getId());
        return stepResult
            .thenCompose(result -> stepContext.replaceDialog(getInitialDialogId(), promptMessage));
    }
}
