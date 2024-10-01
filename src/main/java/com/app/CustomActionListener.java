package com.app;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class CustomActionListener implements ActionListener {

    private final String uuidString;
    private final JFrame frame;
    private final JPanel historyPanel;
    private final GridBagConstraints gbc;
    private final int maxByte;
    private JButton deleteButton;

    public CustomActionListener(String uuidString, JFrame frame, JPanel historyPanel, GridBagConstraints gbc, int maxByte, JButton deleteButton) {
        this.uuidString = uuidString;
        this.frame = frame;
        this.historyPanel = historyPanel;
        this.deleteButton = deleteButton;
        this.maxByte = maxByte;
        this.gbc = gbc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(frame,
                String.format("Are you really want to delete %s?", uuidString)) == JOptionPane.YES_OPTION) {

            StringBuilder log = new StringBuilder();
            String filePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator
                    + "bn_code_log.txt";
            try {

                File myObj = new File(filePath); // Specify the filename
                myObj.createNewFile();

                //read just before add new element
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String nl = myReader.nextLine();
                    String uuidE = nl.split(";")[0];
                    if (uuidE == null ? this.uuidString != null : !uuidE.equals(this.uuidString)) {
                        log.append(nl).append("\n");
                    }
                }
                myReader.close();

                // add new element if element don't exists.
                FileWriter myWriter = new FileWriter(filePath);
                myWriter.write(log.toString());
                myWriter.close();

                updateHistoryData();

            } catch (IOException ignored) {
            }
        }
    }

    void updateHistoryData() {
        String filePath = System.getProperty("user.home") + File.separator + "Documents" + File.separator
                + "bn_code_log.txt";
        ArrayList<History> wholeData = new ArrayList<>();

        try {
            //delete all item inside history and revalidate
            historyPanel.removeAll();
            historyPanel.revalidate();
            historyPanel.repaint();

            File myObj = new File(filePath); // Specify the filename
            myObj.createNewFile();

            //read just before add new element
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String nl = myReader.nextLine();
                String separatedData[] = nl.split(";");
                wholeData.add(new History(Integer.toString(Integer.parseInt(separatedData[1].substring(0, maxByte), 2)),
                        separatedData[2], separatedData[1].substring(maxByte).length(), separatedData[0]));
            }

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
                deleteButton = new JButton("", lastIcon);
                deleteButton.setFocusable(false);
                deleteButton.setBackground(Color.RED);

                deleteButton.addActionListener(new CustomActionListener(wholeData.get(i).getUUID(), this.frame,
                        this.historyPanel, this.gbc, this.maxByte, this.deleteButton));

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

}
