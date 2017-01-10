package yousecase.t2c.main;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import yousecase.notice.Notifier;
import yousecase.t2c.TextFileClipboard;

public class T2CMain {
    public static void main(String[] args) {
        try {
            Charset charset = null;
            if (args.length == 1) {
                charset = StandardCharsets.UTF_8;
            } else if (args.length == 2) {
                charset = Charset.forName(args[1]);
            } else {
                JOptionPane.showMessageDialog(null, "Usage: java -jar t2c.jar <filename> [<characterEncoding>]");
                System.exit(0);
            }

            Path file = Paths.get(args[0]);
            TextFileClipboard.copyTextToClipboard(file, charset);
            Notifier.getSystemTrayNotifierBuilder().build()
                    .notifyMessage("「" + file.getFileName() + "」  has been copied to clipboard.");
        } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            JOptionPane.showMessageDialog(null, "The given character encoding is not supported.");
            System.exit(1);
        } catch (Throwable e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to copy.");
            System.exit(1);
        }
    }
}
