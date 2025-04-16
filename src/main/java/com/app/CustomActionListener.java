package com.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

class CustomActionListener implements  ActionListener {

    private final String uuidString;
    private final JFrame frame;

    public CustomActionListener(String uuidString, JFrame frame) {
        this.uuidString = uuidString;
        this.frame = frame;
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

                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String nl = myReader.nextLine();
                    String uuidE = nl.split(";")[0];
                    if (uuidE != null && !uuidE.equals(uuidString)) {
                        log.append(nl).append("\n");
                    }
                }
                myReader.close();

                FileWriter myWriter = new FileWriter(filePath);
                myWriter.write(log.toString());
                myWriter.close();

                // update data after deleting
                //this.updateHistoryData();
            } catch (IOException ignored) {
            }
        }
    }
}
