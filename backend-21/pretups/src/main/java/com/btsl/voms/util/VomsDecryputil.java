package com.btsl.voms.util;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;

/**
 * @author rahul.dutt
 * 
 */
public class VomsDecryputil {
    private static String key = null;
    private static JFrame frame = null;
    private static File fileo = null;
    private static File fl = null;

   private VomsDecryputil() {
	// TODO Auto-generated constructor stub
}    

   
    public static boolean readEncWriteDecr(String p_filename, String p_key, String p_filepath) throws IOException, GeneralSecurityException{
        final String methodName = "readEncWriteDecr";
     
        DESedeEncryption myEncryptor = null;
        Writer writer = null;
        boolean filewrite = false;
        FileOutputStream fos = null;
        FileReader fileReader = null;
        CsvWriterSettings settings = null;;
        CsvWriter csvWriter = null;
        try {
            myEncryptor = new DESedeEncryption(p_key);
            fileReader = new FileReader(p_filepath + File.separator + p_filename);
            CsvParserSettings parserSettings = new CsvParserSettings();
			parserSettings.setLineSeparatorDetectionEnabled(true);
			RowListProcessor processor = new RowListProcessor();
			parserSettings.setProcessor(processor);
			parserSettings.setSkipEmptyLines(false);
			CsvParser parser = new CsvParser(parserSettings);
			parser.parse(fileReader);
			
			String tempFileName = p_filepath + File.separator + p_filename + ".temp";
			fos = new FileOutputStream(tempFileName);
			writer = new BufferedWriter(new OutputStreamWriter(fos));
			settings = new CsvWriterSettings();
			csvWriter = new CsvWriter(writer, settings);
			
			List<String[]> rows = processor.getRows();
			boolean header = false;
			StringBuilder builder = new StringBuilder();
			int i = 1;
			for (String[] row : rows) {
				if(i<=10){
					csvWriter.writeRow(row);
					header = true;
					i++;
					continue;
				}

				String decrpted = myEncryptor.decrypt(row[0].trim());
				builder.append(decrpted).append(",").append(row[1]).append(",").append(row[2]).append(",").append(row[3]);
				csvWriter.writeRow(builder.toString());
				builder.setLength(0);
			}
			
			csvWriter.close();
			
			File file = new File(tempFileName);
			String newName = tempFileName.substring(0, tempFileName.lastIndexOf("."));
			File newFile = new File(newName);
			if(newFile.exists()){
				boolean isDeleted = newFile.delete();
			}
			file.renameTo(newFile);
          
        } catch (GeneralSecurityException | IOException fe) {
            filewrite = false;
           
            throw fe;
		} finally {
        	writer.close();
        	fos.close();
        	fileReader.close();
        	csvWriter.close();
        }
        return filewrite;
    }

    public static class WriteApp extends Applet implements ActionListener {
        JTextField textField = null;
        JLabel label = null;

        public void actionPerformed(ActionEvent event) {
            final String METHOD_NAME = "actionPerformed";
            key = textField.getText();
            if (!(key == (null)) || !("".equals(key))) {
                if (key.length() != 32) {
                    JOptionPane.showMessageDialog(null, "Key Length should be 32!");
                } else {
                    try {
                        boolean filewtrite = readEncWriteDecr(fileo.toString().substring(fileo.getParent().length() + 1), key, fileo.getParent());
                        JOptionPane.showMessageDialog(null, "Your request is being processed!");
                        if (filewtrite) {
                            JOptionPane.showMessageDialog(null, "Decrypted File generated successfully:" + fl.toString());
                        }
                    } catch (Exception e) {                     
                        JOptionPane.showMessageDialog(null, "Invalid data in File! Error" + e.getMessage());
                    }
                    // System.exit(0);
                }
            }
        }

        public void go() {
            frame = new JFrame("Voms Decrypt Util");
            // frame.setLocation(200, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel jpanel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            JButton button = new JButton("Comviva Tech Ltd.");
            // --------------------------------------------------
            label = new JLabel("Dec File: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            // c.weighty = 1.0;
            jpanel.add(label, c);
            label = new JLabel(fileo.toString());
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            // c.gridwidth = 2;
            // c.weighty = 1.0;
            jpanel.add(label, c);
            // -------------------------------------------------
            // -------------------------------------------------
            label = new JLabel("Enter Key:");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            // c.weighty = 1.0;
            jpanel.add(label, c);
            textField = new JTextField(16);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 1;
            // c.weighty = 1.0;
            jpanel.add(textField, c);
            JButton jbutton = new JButton("Submit");
            // -------------------------------------------------
            jbutton.addActionListener(this);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 1;
            // c.weighty = 1.0;
            jpanel.add(jbutton, c);
            frame.getContentPane().add(BorderLayout.NORTH, jpanel);
            frame.getContentPane().add(BorderLayout.SOUTH, button);
            frame.setSize(600, 200);
            frame.setVisible(true);
        }

        public void paintComponent(Graphics g) {

        }

    }
}
