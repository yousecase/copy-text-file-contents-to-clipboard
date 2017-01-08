package yousecase.t2c;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import yousecase.util.ClipboardManager;

/**
 * テキストファイル内のテキストをクリップボードににコピーするクラスです。
 */
public class TextFileClipboard {
    /**
     * テキストファイル内のテキストをクリップボードにコピーします。指定したcharsetが対応していない文字がテキストに含まれていた場合、エラーは発生せず対応していない文字が特定の文字に置換されます。
     * 
     * @param textFilePath
     *            テキストファイルのパス
     * @param charset
     *            テキストファイルの文字コード
     * @throws FileNotFoundException
     *             引数で指定したファイルがない場合
     * @throws IOException
     *             引数で指定したファイルの読み込みに失敗した場合
     */
    public static void copyTextToClipboard(Path textFilePath, Charset charset) throws IOException {
        Objects.requireNonNull(textFilePath);
        Objects.requireNonNull(charset);
        if (!Files.exists(textFilePath))
            throw new FileNotFoundException(textFilePath.toString());
        if (!Files.isRegularFile(textFilePath))
            throw new IOException();

        // Charsetが対応していない文字が特定の文字に置換されるのはnew String()の仕様。
        // 文字の置換を検出する場合はbyte[]->String->byte[]で前後のbyte[]を比較する。
        ClipboardManager.setString(new String(Files.readAllBytes(textFilePath), charset));
    }

    private TextFileClipboard() {
    }
}
