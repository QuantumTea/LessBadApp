package io.fictional.qa;

/**
 * Description: This is an intentionally buggy program for QA training in exploratory testing.
 * The Close button (top right red X or circle) doesn't work
 * App closes randomly, leaving log file with a stack trace on the desktop
 * Message popup is misspelled
 * Exit button always fails the first time and works 40% of the time after that
 * Exit button may randomly minimise the app, but not close it
 * When the exit button fails, the app gets wider or taller
 * Dialogs have buttons and input fields that make no sense
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class LessBadApp extends JFrame
{
    private static int failedCloseAttemptsCounter = 0;
    private static int minimisedInsteadOfClosedCounter = 0;
    private static JLabel failedCloseAttemptsLabel;
    private static int timeToFatalException = 0;
    private static int survivedExceptionAttemptsCounter = 0;
    private static double finalRandomNumber = 0;
    private final static String systemLineSeparator = System.getProperty("line.separator");
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
    private final String pathToDesktop = System.getProperty("user.home")
            + System.getProperty("file.separator") + "Desktop"
            + System.getProperty("file.separator") + "LessBadApp error log.txt";
    private PrintWriter printWriter;

    private LessBadApp()
    {
        super("Welcome to the LessBadApp");
        ConstructContentPane();
        GenerateMenuBar();
        SetUpTimerToGenerateExceptions();
    }

    public static void main(String[] args)
    {
        LessBadApp app;
        app = new LessBadApp();
        app.setVisible(true);
    }

    private void ConstructContentPane()
    {
        setSize(450, 300);
        final Container contentPane = getContentPane();
        final JButton launchMessageWindowButton = new JButton("Launch message window");
        launchMessageWindowButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                launchMisspelledMessagePopup();
            }
        });
        contentPane.add(launchMessageWindowButton, BorderLayout.NORTH);

        final JButton faultyExitButton = new JButton("Exit");
        faultyExitButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                AttemptToCloseTheApp();
            }
        });
        contentPane.add(faultyExitButton, BorderLayout.SOUTH);

        failedCloseAttemptsLabel = new JLabel();
        contentPane.add(failedCloseAttemptsLabel, BorderLayout.CENTER);

        // disable the red X at the top right of the window
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void SetUpTimerToGenerateExceptions()
    {
        int delay = 5000; //milliseconds
        ActionListener taskPerformer = new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                try
                {
                    timeToFatalException += 5;
                    SeeIfAppWillClose();
                } catch (Exception e)
                {
                    dealWithException(e);
                }
            }
        };
        new Timer(delay, taskPerformer).start();
    }

    private void GenerateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        JMenu forbiddenMenu = new JMenu("Do not click this menu");
        menuBar.add(forbiddenMenu);

        JMenuItem aboutAction = new JMenuItem("About the LessBadApp");
        helpMenu.add(aboutAction);
        aboutAction.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                displayAboutBox();
            }
        });

        JMenuItem forbiddenAction = new JMenuItem("I'm serious, don't click this");
        forbiddenMenu.add(forbiddenAction);
        forbiddenAction.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                youClickedTheForbiddenMenu();
            }
        });
    }

    private void launchMisspelledMessagePopup()
    {
        JOptionPane.showInputDialog(this, "You launched a messege window, well done.", "Alart!", JOptionPane.ERROR_MESSAGE);
    }

    private void displayAboutBox()
    {
        JOptionPane.showConfirmDialog(this, "LessBadApp was written by Alison Hawke",
                "About the LessBadApp", JOptionPane.YES_NO_OPTION);
    }

    private void youClickedTheForbiddenMenu()
    {
        // see if you can change the OK button to an "I'm sorry"
        JOptionPane.showMessageDialog(this, "I told you not to click that.", "Ouch!", JOptionPane.WARNING_MESSAGE);
    }

    private void dealWithException(Exception exception)
    {
        writeErrorLog(exception);
        System.exit(0);
    }

    private void SeeIfAppWillClose() throws Exception
    {
        double randomNumber = GetRandomNumber();

        // throws exception 5% of the time and goes off every five seconds
        if (randomNumber < 0.95)
        {
            survivedExceptionAttemptsCounter++;
        } else
        {
            finalRandomNumber = randomNumber;
            throw new Exception("Something has gone wrong, but not horribly wrong.");
        }
    }

    private void AttemptToCloseTheApp()
    {

        if (FailFirstTwoCloseAttemptsWhenCounterIsOneOrTwo()) return;
        if (Fail60PercentOfTime()) return;
        IncrementCounterToLimitOf23();
        ChangeWindowSize();
    }

    private void ChangeWindowSize()
    {
        if (failedCloseAttemptsCounter % 2 == 0)
        {
            MakeTheWindowTaller();
        } else
        {
            MakeTheWindowWider();
        }
    }

    private void IncrementCounterToLimitOf23()
    {
        failedCloseAttemptsCounter++;
        if (failedCloseAttemptsCounter == 23)
        {
            writeErrorLog("Close attempts exceeds maximum safe level, exiting");
            System.exit(0);
        }
    }

    private boolean Fail60PercentOfTime()
    {
        if (GetRandomNumber() < 0.04)
        {
            writeErrorLog("The exit button worked as designed, mostly.");
            System.exit(0);
            return true;
        }

        if (GetRandomNumber() < 0.05)
        {
            // small chance of minimising the app instead of closing it
            this.setState(Frame.ICONIFIED);
            minimisedInsteadOfClosedCounter++;
        }
        return false;
    }

    double GetRandomNumber()
    {
        // Returns a number between 0.0 and 0.1
        return Math.random();
    }

    private boolean FailFirstTwoCloseAttemptsWhenCounterIsOneOrTwo()
    {
        if ((failedCloseAttemptsCounter == 0) || (failedCloseAttemptsCounter == 1))
        {
            FailTheFirstTwoCloseAttempts();
            return true;
        }
        return false;
    }

    private void FailTheFirstTwoCloseAttempts()
    {
        failedCloseAttemptsCounter++;
        ChangeWindowSize();
    }

    private void MakeTheWindowTaller()
    {
        int newHeight = this.getHeight() + 10;
        int oldWidth = this.getWidth();
        this.setSize(oldWidth, newHeight);
        SetFailedCloseAttemptsLabel();
    }

    private void SetFailedCloseAttemptsLabel()
    {
        failedCloseAttemptsLabel.setText("    Failed attempts to close this window: " + failedCloseAttemptsCounter);
    }

    private void MakeTheWindowWider()
    {
        int newWidth = this.getWidth() + 50;
        int oldHeight = this.getHeight();
        this.setSize(newWidth, oldHeight);
        SetFailedCloseAttemptsLabel();
    }

    private void writeErrorLog(String message)
    {
        try
        {
            // true to append, false to write a new file
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(pathToDesktop, true)));
            writeSystemInformation(dateFormat, printWriter, message);
            printWriter.println("-------------------------------------------------------");
            printWriter.close();
        } catch (IOException ioe)
        {
            System.out.println("IOException, log file not written");
            ioe.printStackTrace();
        }
    }

    private void writeErrorLog(Exception exception)
    {
        try
        {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(pathToDesktop, true)));
            writeSystemInformation(dateFormat, printWriter, "*the exceptional sound of crickets chirping*");
            exception.printStackTrace(printWriter);
            printWriter.println("-------------------------------------------------------");
            printWriter.close();
        } catch (IOException ioe)
        {
            System.out.println("IOException, log file not written");
            ioe.printStackTrace();
        }
    }

    private void writeSystemInformation(DateFormat dateFormat, PrintWriter printWriter, String message)
    {
        printWriter.println("*** Errror log for LessBadApp: " + "Something went wrong, but not horribly wrong");
        // intentional spelling error, even in the stack trace
        printWriter.println("Current system time is: " + dateFormat.format(new Date()));
        printWriter.println("Number of failed exit attempts was " + failedCloseAttemptsCounter);
        printWriter.println("Minimised instead of closing " + minimisedInsteadOfClosedCounter + " times"
                + systemLineSeparator);

        printWriter.println("Additional system message: " + message + systemLineSeparator);

        printWriter.println("Time from launch to fatal exception was " + timeToFatalException + " seconds.");
        printWriter.println("Survived exception " + survivedExceptionAttemptsCounter + " time(s)");
        printWriter.println("Final random number was: " + finalRandomNumber
                + systemLineSeparator);

        printWriter.println("The logged in user is " + System.getProperty("user.name"));
        printWriter.println("IntentionallyBadApp was running on " + System.getProperty("os.name"));
        printWriter.println("Operating system architecture is " + System.getProperty("os.arch")
                + systemLineSeparator);

        printWriter.println("Java version is " + System.getProperty("java.version"));
        printWriter.println("Java Virtual Machine version is " + System.getProperty("java.vm.version"));
        printWriter.println("Java Runtime Environment version is " + System.getProperty("java.specification.version"));
        printWriter.println("Java vendor is " + System.getProperty("java.vendor")
                + systemLineSeparator);
    }
}
