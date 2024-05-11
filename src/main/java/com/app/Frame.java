package com.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Frame extends WindowAdapter implements ActionListener {

    private static final Logger log = LogManager.getLogger(Frame.class);
    private final JFrame frame;
    private final JLabel hexadecimalLabel;
    private final JLabel finalLabel;
    private final JLabel error;
    private final JTextField numberInputField;
    private final JTextField triangleQuantityField;
    private final JButton copyButton;
    private String binary = "000000000000";

    Frame() {

        error = new JLabel();
        error.setBounds(100, 130, 300, 20);
        error.setForeground(Color.RED);
        error.setVisible(true);

        copyButton = new JButton("Copy to the clipboard");
        copyButton.setBounds(90, 95, 160, 25);
        copyButton.setFocusable(false);
        copyButton.setBackground(Color.lightGray);
        copyButton.addActionListener(this);

        JLabel writtenBy = new JLabel("written by Omer Bircan Sahin");
        writtenBy.setBounds(10, 160, 300, 20);
        writtenBy.setForeground(Color.BLUE);

        finalLabel = new JLabel("000000000000");
        finalLabel.setBounds(10, 65, 300, 20);
        finalLabel.setFont(new Font("BinCode", Font.PLAIN, 12));

        JLabel triangleLabel = new JLabel("Add triangles:");
        triangleLabel.setBounds(10, 35, 90, 20);

        triangleQuantityField = new JTextField();
        triangleQuantityField.setBounds(160, 35, 90, 20);
        //change listener
        triangleQuantityField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                addTheTriangles();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                addTheTriangles();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        JLabel numberLabel = new JLabel("Code Number:");
        numberLabel.setBounds(10, 10, 90, 20);

        hexadecimalLabel = new JLabel("");
        hexadecimalLabel.setBounds(260, 10, 50, 20);

        numberInputField = new JTextField();
        numberInputField.setBounds(160, 10, 90, 20);
        //change listener
        numberInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        int width = 310;
        int height = 220;

        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, width, height);
        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(null);
        mainPanel.add(numberLabel);
        mainPanel.add(numberInputField);
        mainPanel.add(hexadecimalLabel);
        mainPanel.add(triangleLabel);
        mainPanel.add(triangleQuantityField);
        mainPanel.add(finalLabel);
        mainPanel.add(writtenBy);
        mainPanel.add(copyButton);
        mainPanel.add(error);

        frame = new JFrame("Bincode Maker");
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == copyButton) {


            if (isValid()) {
                String myString = finalLabel.getText();
                StringSelection stringSelection = new StringSelection(myString);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                error.setForeground(Color.green);
                error.setText("Sayı kopyalandı.");

            } else {
                error.setForeground(Color.red);
                error.setText("Bu sayı zaten kullanılmış.");
            }
        }

    }

    boolean isValid() {
        StringBuilder log = new StringBuilder();
        ArrayList<String> invalidNumbers = new ArrayList<>();
        String currentDir = System.getProperty("user.dir");
        String path = currentDir + "/log.txt";

        try {
            File myObj = new File(path); // Specify the filename
            myObj.createNewFile();

            //read just before add new element
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String nl = myReader.nextLine();
                invalidNumbers.add(nl);
                log.append(nl).append("\n");
            }
            myReader.close();

            // add new element if element don't exists.
            FileWriter myWriter = new FileWriter(path);
            if (invalidNumbers.contains(finalLabel.getText())) {
                //write current log file
                myWriter.write(log.toString());
                myWriter.close();
            }
            myWriter.write(log.append(finalLabel.getText()).toString());
            myWriter.close();

            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public void windowClosing(WindowEvent e) {
        if (JOptionPane.showConfirmDialog(frame, "Are you sure?") == JOptionPane.YES_OPTION)
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    void onChange() {
        try {
            //hex
            String hexString = Integer.toHexString(Integer.parseInt(numberInputField.getText())).toUpperCase();
            if (hexString.length() == 1) hexString = "00" + hexString.toUpperCase();
            else if (hexString.length() == 2) hexString = "0" + hexString.toUpperCase();
            hexadecimalLabel.setText(hexString);

            //binary
            StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(Integer.parseInt(numberInputField.getText())));
            int threshold = binaryString.length();
            while (threshold < 12) {
                binaryString.insert(0, "0");
                threshold++;
            }
            if (binaryString.length() > 12) throw new Exception();
            finalLabel.setText(binaryString.toString());
            binary = binaryString.toString();

        } catch (Exception ignored) {
            hexadecimalLabel.setText("");
            finalLabel.setText("000000000000");
            binary = "000000000000";
        }

        addTheTriangles();
    }

    void addTheTriangles() {
        try {
            int quantityOfA = Integer.parseInt(triangleQuantityField.getText());
            StringBuilder A = new StringBuilder();
            while (quantityOfA > 0 && quantityOfA < 5) {
                A.append("A");
                quantityOfA--;
            }
            finalLabel.setText(binary + A);
        } catch (Exception ignore) {
            finalLabel.setText(binary);
        }
    }
}