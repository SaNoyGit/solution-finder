package _usecase.spin.files;

import _usecase.FileHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OutputFileHelper {
    private static final String SPIN_PATH = concatPath("output", "spin.html");

    private static String concatPath(String... names) {
        return FileHelper.concatPath(names);
    }

    public static SpinHTML loadSpinHTML() throws IOException {
        return loadSpinHTML(SPIN_PATH);
    }

    private static SpinHTML loadSpinHTML(String path) throws IOException {
        return loadHTML(path);
    }

    private static SpinHTML loadHTML(String path) throws IOException {
        String html = Files.lines(Paths.get(path)).collect(Collectors.joining());

        String mergedFumen;
        {
            String[] split = html.split("</header>");
            html = split[1];

            List<String> list = extractTetfu(split[0]);
            assert list.size() <= 1 : split[0];
            mergedFumen = list.isEmpty() ? null : list.get(0);
        }

        List<String> fumens = extractTetfu(html);
        return new SpinHTML(html, mergedFumen, fumens);
    }

    private static List<String> extractTetfu(String html) {
        Pattern pattern = Pattern.compile("'http://fumen\\.zui\\.jp/\\?v115@(.+?)'");
        Matcher matcher = pattern.matcher(html);

        ArrayList<String> fumens = new ArrayList<>();
        while (matcher.find()) {
            assert matcher.groupCount() == 1;
            fumens.add(matcher.group(1));
        }
        return fumens;
    }

    public static void deleteSpinHTML() {
        File file = new File(SPIN_PATH);
        FileHelper.deleteFileAndClose(file);
    }
}
