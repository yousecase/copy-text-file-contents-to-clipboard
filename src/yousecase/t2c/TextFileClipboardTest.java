package yousecase.t2c;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import yousecase.util.ClipboardManager;

//ISO-2022-CN, x-JISAutoDetect, x-ISO-2022-CN-CNSを除く利用可能なCharsetが対応する
//文字の書き込み、読み取り、クリップボートへの格納を行い、書き込んだ文字とクリップボートの値が一致するかどうか検証する。
@RunWith(Theories.class)
public class TextFileClipboardTest {
    // "ISO-2022-CN", "x-JISAutoDetect"はString#getBytesが全ての文字で失敗する。
    // "x-ISO-2022-CN-CNS"は一部の文字列がエンコードとデコードを繰り返すたびに値が変化する。例:"七丄丅万丈三上下"
    private static final Collection<Charset> CHARSET_TO_EXCLUDE = Arrays
            .asList("ISO-2022-CN", "x-JISAutoDetect", "x-ISO-2022-CN-CNS").stream().map(Charset::forName)
            .collect(Collectors.toSet());

    @DataPoints
    public static final Charset[] CHARSET_TO_USE = Charset.availableCharsets().values().stream()
            .filter(charset -> !CHARSET_TO_EXCLUDE.contains(charset)).toArray(Charset[]::new);

    private static Path TEMP_FILE_PATH;

    @BeforeClass
    public static void beforeClass() throws IOException {
        TEMP_FILE_PATH = Files.createTempFile(null, null);
        TEMP_FILE_PATH.toFile().deleteOnExit();
    }

    @Theory
    public void testCopyTextToClipboard(Charset charset) throws IOException {
        String textData = Stream.iterate(Character.MIN_VALUE, ch -> ++ch).limit(Character.MAX_VALUE + 1)
                .map(String::valueOf)
                // Charsetが対応している文字のみを使用する(String->byte[]->Stringの変換でStringの値が一致するもののみを利用)
                .filter(str -> str.equals(new String(str.getBytes(charset), charset))).collect(Collectors.joining());
        Files.write(TEMP_FILE_PATH, textData.getBytes(charset));
        TextFileClipboard.copyTextToClipboard(TEMP_FILE_PATH, charset);
        assertArrayEquals(textData.toCharArray(), ClipboardManager.getString().toCharArray());
    }
}
