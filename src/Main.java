import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Main {
    private static final String NEW_CLASS_NAME = "A";  // Новое имя для класса

    public static void main(String[] args) {
        String filePath = "src\\File.java";
        try {
            String code = new String(Files.readAllBytes(Paths.get(filePath)));

            code = removeComments(code);
            code = removeExtraSpaces(code);
            code = replaceClassName(code);
            code = replaceIdentifiers(code);

            Files.write(Paths.get(filePath), code.getBytes());
            System.out.println("Файл успешно обработан.");
        } catch (IOException e) {
            System.err.println("Ошибка обработки файла: " + e.getMessage());
        }
    }

    // Удаление лишних пробелов и символов новой строки
    private static String removeExtraSpaces(String code) {
        code = code.replaceAll("\n+", "\n");
        code = code.replaceAll("\\s+", " ");
        return code;
    }

    // Удаление комментариев
    private static String removeComments(String code) {
        code = code.replaceAll("//.*?\\n", "");  // Однострочные комментарии
        code = code.replaceAll("/\\*.*?\\*/", "");  // Многострочные комментарии
        return code;
    }

    // Замена имени класса
    private static String replaceClassName(String code) {
        Pattern pattern = Pattern.compile("\\bclass\\s+(\\w+)");  // Поиск имени класса
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            String originalClassName = matcher.group(1);
            code = code.replaceAll("\\b" + originalClassName + "\\b", NEW_CLASS_NAME);
        }
        return code;
    }

    // Замена идентификаторов
    private static String replaceIdentifiers(String code) {
        Set<String> foundIdentifiers = collectIdentifiers(code);

        Map<String, String> identifierMap = new HashMap<>();
        char currentChar = 'a'; // Начинаем с буквы 'a'
        int currentNum = 0; // Счетчик для имен в формате v1, v2 и т.д.

        for (String identifier : foundIdentifiers) {
            String shortName;
            if (currentChar <= 'z') {
                shortName = String.valueOf(currentChar++);
            } else {
                shortName = "v" + currentNum++;
            }
            identifierMap.put(identifier, shortName);
        }

        for (Map.Entry<String, String> entry : identifierMap.entrySet()) {
            if (!entry.getKey().equals("main")) { // Не заменяем метод main
                code = code.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue());
            }
        }

        return code;
    }

    private static Set<String> collectIdentifiers(String code) {
        Set<String> identifiers = new HashSet<>();

        // Шаблоны для нахождения переменных, методов и классов
        Pattern variablePattern = Pattern.compile("\\b(int|double|float|String|boolean|char|long|short|byte)\\s+(\\w+)\\b");
        Pattern methodPattern = Pattern.compile("\\b(\\w+)\\s*\\(");
        Pattern classPattern = Pattern.compile("\\bclass\\s+(\\w+)\\b");

        // Находим переменные
        Matcher variableMatcher = variablePattern.matcher(code);
        while (variableMatcher.find()) {
            String identifier = variableMatcher.group(2);
            if (!isReservedWord(identifier)) {
                identifiers.add(identifier);
            }
        }

        // Находим методы
        Matcher methodMatcher = methodPattern.matcher(code);
        while (methodMatcher.find()) {
            String identifier = methodMatcher.group(1);
            if (!isReservedWord(identifier)) {
                identifiers.add(identifier);
            }
        }

        // Находим классы
        Matcher classMatcher = classPattern.matcher(code);
        while (classMatcher.find()) {
            String identifier = classMatcher.group(1);
            if (!isReservedWord(identifier)) {
                identifiers.add(identifier);
            }
        }

        return identifiers;
    }

    // Проверка зарезервированных слов
    private static boolean isReservedWord(String word) {
        Set<String> reservedWords = Set.of("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "default", "do", "double", "else", "enum",
                "extends", "final", "finally", "float", "for", "goto", "if", "implements",
                "import", "instanceof", "int", "interface", "long", "native", "new",
                "package", "private", "protected", "public", "return", "short", "static",
                "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while");
        return reservedWords.contains(word);
    }
}
