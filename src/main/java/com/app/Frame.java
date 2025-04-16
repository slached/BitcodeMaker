package com.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Frame extends WindowAdapter implements ActionListener {

    private final JFrame frame;
    private final JFrame secondFrame;
    private final JPanel historyPanel;
    private final JLabel hexadecimalLabel;
    private final JLabel finalLabel;
    private final JLabel error;
    private final JTextField numberInputField;
    private final JTextField triangleQuantityField;
    private final JButton copyButton;
    private final JButton buttonForEnhancedView;
    private JButton deleteButton;
    private final String resetBinary = "00000000000000000000";
    private String binary = "00000000000000000000";
    private final GridBagConstraints gbc;

    private final int maxByte = 20;
    private final boolean[] errors = new boolean[2];
    private boolean isEnhancedViewActive = true;

    Frame() {

        historyPanel = new JPanel();
        historyPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();

        // insert all history into panel
        updateHistoryData();
        JScrollPane scrollPane = new JScrollPane(historyPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        ImageIcon icon = new ImageIcon(Frame.class.getResource("/open_menu.png"));
        Image resizedIcon = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ImageIcon lastIcon = new ImageIcon(resizedIcon);
        buttonForEnhancedView = new JButton("", lastIcon);
        buttonForEnhancedView.setBounds(255, 145, 30, 30);
        buttonForEnhancedView.setFocusable(false);
        buttonForEnhancedView.setBackground(Color.lightGray);
        buttonForEnhancedView.addActionListener(this);
        buttonForEnhancedView.setEnabled(!isEnhancedViewActive);

        error = new JLabel();
        error.setBounds(75, 120, 300, 20);
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

        finalLabel = new JLabel(binary);
        finalLabel.setBounds(10, 65, 300, 20);
        finalLabel.setFont(new Font("BinCode", Font.PLAIN, 12));

        JLabel triangleLabel = new JLabel("Add triangles:");
        triangleLabel.setBounds(10, 35, 90, 20);

        triangleQuantityField = new JTextField();
        triangleQuantityField.setBounds(160, 35, 90, 20);
        // change listener
        triangleQuantityField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
                addTheTriangles();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
                addTheTriangles();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        JLabel numberLabel = new JLabel("Code Number:");
        numberLabel.setBounds(10, 10, 90, 20);

        hexadecimalLabel = new JLabel();
        hexadecimalLabel.setBounds(260, 10, 50, 20);

        numberInputField = new JTextField();
        numberInputField.setBounds(160, 10, 90, 20);
        // change listener
        numberInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
                addTheTriangles();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
                addTheTriangles();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        int width = 330;
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
        mainPanel.add(buttonForEnhancedView);

        frame = new JFrame("Bincode Maker");
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.add(mainPanel);

        Point frameLocation = frame.getLocation();
        Dimension frameSize = frame.getSize();

        secondFrame = new JFrame("History");
        secondFrame.setLocation(frameLocation.x + frameSize.width - 5, frameLocation.y);
        secondFrame.setLayout(new BorderLayout());
        secondFrame.setSize(340, height);
        secondFrame.setResizable(true);
        secondFrame.setVisible(true);
        secondFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        secondFrame.add(scrollPane, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(frame,
                        "Are you sure?") == JOptionPane.YES_OPTION) {
                    frame.dispose();
                    secondFrame.dispose();
                }
            }
        });

        secondFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isEnhancedViewActive = false;
                buttonForEnhancedView.setEnabled(!isEnhancedViewActive);
                secondFrame.setVisible(isEnhancedViewActive);
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == copyButton) {
            if (isValid()) {
                StringSelection stringSelection = new StringSelection(finalLabel.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                // trigger updateHistory function after new number added
                updateHistoryData();
                historyPanel.revalidate();
                historyPanel.repaint();

                error.setForeground(Color.green);
                error.setText("Sayı kopyalandı.");

            } else {
                error.setForeground(Color.red);
                error.setText("Bu sayı zaten kullanılmış.");
            }
        }
        if (e.getSource() == buttonForEnhancedView) {
            isEnhancedViewActive = true;
            buttonForEnhancedView.setEnabled(!isEnhancedViewActive);
            secondFrame.setVisible(isEnhancedViewActive);
            updateHistoryData();
        }
    }

    boolean isValid() {
        StringBuilder log = new StringBuilder();
        ArrayList<String> invalidNumbers = new ArrayList<>();
        String filePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator
                + "bn_code_log.txt";
        try {
            File myObj = new File(filePath); // Specify the filename
            myObj.createNewFile();

            // read just before add new element
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String nl = myReader.nextLine();
                // in log format i decide to use separate number and date's with x so 0. index
                // present UUID
                // 1. index presents number
                // and 2. index presents the date
                invalidNumbers.add(nl.split(";")[1]);
                log.append(nl).append("\n");
            }
            myReader.close();

            // add new element if element don't exists.
            FileWriter myWriter = new FileWriter(filePath);
            if (invalidNumbers.contains(finalLabel.getText())) {
                // write current log file
                myWriter.write(log.toString());
                myWriter.close();
            }
            // get now
            ZonedDateTime zonedDateTime = ZonedDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedZonedTime = zonedDateTime.format(formatter);

            myWriter.write(log.append(UUID.randomUUID().toString())
                    .append(";").append(finalLabel.getText()).append(";").append(formattedZonedTime).toString());
            myWriter.close();

            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    void onChange() {
        if (!numberInputField.getText().isEmpty()) {
            try {
                // hex
                String hexString = Integer.toHexString(Integer.parseInt(numberInputField.getText())).toUpperCase();
                if (hexString.length() == 1) {
                    hexString = "00" + hexString.toUpperCase();
                } else if (hexString.length() == 2) {
                    hexString = "0" + hexString.toUpperCase();
                }
                hexadecimalLabel.setText(hexString);

                // binary
                StringBuilder binaryString = new StringBuilder(
                        Integer.toBinaryString(Integer.parseInt(numberInputField.getText())));
                int threshold = binaryString.length();
                int bitQuantity = maxByte;
                while (threshold < bitQuantity) {
                    binaryString.insert(0, "0");
                    threshold++;
                }
                if (binaryString.length() > bitQuantity) {
                    throw new Exception("Girdiğiniz sayı üst sınırı aşıyor!");
                }
                binary = binaryString.toString();
                finalLabel.setText(binary);
                errors[0] = false;
            } catch (Exception e) {
                errors[0] = true;
                copyButton.setEnabled(false);
                error.setForeground(Color.RED);
                error.setText(e.getMessage());
                resetValues();
            }
        } else {
            errors[0] = false;
            resetValues();
        }
        // if there is no any error
        if (!errors[0] && !errors[1]) {
            error.setText("");
            copyButton.setEnabled(true);
        }
    }

    void resetValues() {
        hexadecimalLabel.setText("");
        finalLabel.setText(resetBinary);
        binary = resetBinary;
    }

    void addTheTriangles() {
        if (!triangleQuantityField.getText().isEmpty()) {
            try {
                int quantityOfA = Integer.parseInt(triangleQuantityField.getText());
                StringBuilder A = new StringBuilder();
                if (quantityOfA >= 5) {
                    throw new Exception("4 ten fazla rakam giremezsiniz!");
                }
                while (quantityOfA > 0 && quantityOfA < 5) {
                    A.append("A");
                    quantityOfA--;
                }
                finalLabel.setText(binary + A);
                errors[1] = false;
            } catch (Exception e) {
                error.setForeground(Color.RED);
                error.setText(e.getMessage());
                copyButton.setEnabled(false);
                errors[1] = true;
                resetValues();
            }
        } else {
            errors[1] = false;
        }

        // if there is no any error
        if (!errors[0] && !errors[1]) {
            error.setText("");
            copyButton.setEnabled(true);
        }
    }

    void updateHistoryData() {
        String filePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator
                + "bn_code_log.txt";
        ArrayList<History> wholeData = new ArrayList<>();

        try {
            // delete all item inside history and revalidate
            historyPanel.removeAll();
            historyPanel.revalidate();
            historyPanel.repaint();

            File myObj = new File(filePath); // Specify the filename
            myObj.createNewFile();

            // read just before add new element
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String nl = myReader.nextLine();
                String separatedData[] = nl.split(";");
                wholeData.add(new History(Integer.toString(Integer.parseInt(separatedData[1].substring(0, maxByte), 2)),
                        separatedData[2], separatedData[1].substring(maxByte).length(), separatedData[0]));
            }
            myReader.close();
            if (!wholeData.isEmpty()) {
                // this adds guide row to end user
                addTopRowToHistoryPanel(false);
            } else {
                addTopRowToHistoryPanel(true);
            }
            Collections.sort(wholeData, Comparator.comparing(History::getDate));

            int gridY = 2;

            ImageIcon icon = new ImageIcon(Frame.class.getResource("/delete_item.png"));
            Image resizedIcon = icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
            ImageIcon lastIcon = new ImageIcon(resizedIcon);

            // reverse and render at history panel
            for (int i = wholeData.size() - 1; i >= 0; i--) {
                int index = i;
                deleteButton = new JButton("", lastIcon);
                deleteButton.setFocusable(false);
                deleteButton.setBackground(Color.RED);

                deleteButton.addActionListener(e -> {
                    customActionListener(wholeData.get(index).getUUID(), myObj, filePath);
                });

                String decimalNumber = wholeData.get(i).getNumber();
                String date = wholeData.get(i).getDate();
                String triangleCount = Integer.toString(wholeData.get(i).getTriangleCount());

                gbc.gridx = 0;
                gbc.gridy = gridY;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(0, 0, 10, 10);
                historyPanel.add(new JLabel(decimalNumber), gbc);

                gbc.gridx = 1;
                gbc.anchor = GridBagConstraints.CENTER;
                gbc.insets = new Insets(0, 0, 10, 0);
                historyPanel.add(new JLabel(date), gbc);

                gbc.gridx = 2;
                gbc.anchor = GridBagConstraints.EAST;
                gbc.insets = new Insets(0, 35, 10, 35);
                historyPanel.add(new JLabel(triangleCount), gbc);

                gbc.gridx = 3;
                gbc.insets = new Insets(0, 0, 10, 0);
                historyPanel.add(deleteButton, gbc);

                gridY++;
                historyPanel.revalidate();
                historyPanel.repaint();
            }

            myReader.close();

        } catch (IOException ignored) {
        }
    }

    private void addTopRowToHistoryPanel(boolean isEmpty) {
        if (isEmpty) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            historyPanel.add(new JLabel("There is no any number."), gbc);
        } else {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            historyPanel.add(new JLabel("Number"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            historyPanel.add(new JLabel("Date"), gbc);

            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;
            historyPanel.add(new JLabel("Triangle Count"), gbc);
        }
    }

    void customActionListener(String uuidString, File myObj, String filePath) {
        if (JOptionPane.showConfirmDialog(frame,
                String.format("Are you really want to delete %s?", uuidString)) == JOptionPane.YES_OPTION) {
            try {
                StringBuilder log = new StringBuilder();
                // re read the file and delete the selected item
                final Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String nl = myReader.nextLine();
                    String uuidE = nl.split(";")[0];
                    if (uuidE != null && !uuidE.equals(uuidString)) {
                        log.append(nl).append("\n");
                    }
                    try (FileWriter myWriter = new FileWriter(filePath)) {
                        myWriter.write(log.toString());
                    }
                }
                myReader.close();
                // update data after deleting
                updateHistoryData();
            } catch (Exception ignored) {
            }
        }
    }

}
