import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WordleGame {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_GRAY_BACKGROUND = "\u001B[100m";
    static Scanner one = new Scanner(System.in);

    public static void main(String[] args) {
        check();
    }

    public static void check() {
        /*
        В этом методе реализуется формирование словаря и передача его
        в следующий метод, а также создание рандомного слова, которое
        будет отгадываться, дополнительно прописано условие, при котором
        игра будет считаться пройденной.
         */
        ArrayList<String> guesslist = new ArrayList<>();
        ArrayList<String> list = scannerWord(guesslist);

        String guess = null;
        String puzzleWord = randomPuzzleString(list, guess);

        System.out.println("Слово загаданно, попробуйте его угадать.");

        running(puzzleWord, list);

    }

    public static ArrayList<String> scannerWord(ArrayList<String> list) {

        File path = new File("E:/vocabulary.txt");

        // пробуем считать словарь из файла в список
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String s;
            while ((s = br.readLine()) != null) {
                String[] split = s.split("\n");
                list.addAll(Arrays.asList(split));
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    public static String randomPuzzleString(ArrayList<String> list, String puzzleWord) {
        // выбираем рандомное слово мз списка, которое будет загадываться
        int random = (int) (Math.random() * list.size());
        puzzleWord = list.get(random);

        return puzzleWord;
    }


    public static String running(String puzzleWord, ArrayList<String> list) {
        //запускаем цикл с условием для выхода из него
        String newWord = null;
        String newMask = null;
        boolean flag = false;
        while (!flag) {
            flag = true;
            System.out.print("Введите слово из 5 букв: ");
            String supposeWord = one.nextLine();

            //делаем проверку на корректность введенного слова
            if (list.contains(supposeWord)) {

                //при нахождении предполагаемого слова, сравниваем его символы с загаданным словом

                String[] result = new String[puzzleWord.length()];
                String[] resultMask = new String[puzzleWord.length()];

                for (int i = 0; i < supposeWord.length(); i++) {
                    if (puzzleWord.charAt(i) == supposeWord.charAt(i)) {
                        result[i] = String.valueOf(ANSI_GREEN_BACKGROUND + supposeWord.charAt(i) + ANSI_RESET);
                        resultMask[i] = "G";
                        continue;
                    } else if (puzzleWord.indexOf(supposeWord.charAt(i)) != -1) {
                        result[i] = ANSI_YELLOW_BACKGROUND + supposeWord.charAt(i) + ANSI_RESET;
                        resultMask[i] = "Y";
                        flag = false;
                    } else {
                        result[i] = ANSI_GRAY_BACKGROUND + supposeWord.charAt(i) + ANSI_RESET;
                        resultMask[i] = "X";
                        flag = false;
                    }
                }

                //делаем проверку в словаре со словами, которые подходят по найденным символам
                StringBuilder sbR = new StringBuilder();
                for (String ch : result) {
                    sbR.append(ch);
                }
                newWord = sbR.toString();

                StringBuilder sbM = new StringBuilder();
                for (String ch : resultMask) {
                    sbM.append(ch);
                }
                newMask = sbM.toString();
                // проверяем маску на сходство с загаданным словом, если маска не идентична, то продолжаем проверку и поиск
                if (newMask.equals("GGGGG")){
                    System.out.println("Результат по схождению: "+ newWord);
                    System.out.println("Вы выиграли!");
                    break;
                }else {
                    System.out.println("Результат по схождению: "+ newWord);
                    sercheChar(list, supposeWord, newMask);
                }

            } else {
                // если слово не было в словаре, то сообщаем об этом пользователю и повторяем метод
                System.out.println("Такого слова не существует");
                running(puzzleWord, list);

            }

        }
        return newWord;
    }


    public static void sercheChar(ArrayList<String> list, String supposeWord, String newMask) {

        //проверка всех символов алфавита
        List<String> regexList = initializeRegex();

        // Символы которые содержат зеленый, или желтый цвет
        List<Character> detectedLetters = new ArrayList<>();

        updateRegex(supposeWord, newMask, detectedLetters, regexList);

        System.out.print("Возможные слова: ");
        printTopMatches(detectedLetters, regexList, list);
        System.out.println();

    }

    private static void printTopMatches(List<Character> detectedLetters, List<String> regexList, List<String> wordList) {
        String regex = regexList.stream().collect(Collectors.joining());
        int printCount = 0;

        for (int wordIndex = 0; wordIndex < wordList.size(); wordIndex++) {
            String currentWord = wordList.get(wordIndex);
            // Проверяем что слово содержит все обнаруженные символы и корректно
            if (currentWord.matches(regex) && allDetectedLettersPresent(currentWord, detectedLetters)) {
                System.out.print(currentWord + ", ");
                if (++printCount == 10) {
                    break;
                }

            }

        }

    }

    private static boolean allDetectedLettersPresent(String currentWord, List<Character> detectedLetters) {
        for (int i = 0; i < detectedLetters.size(); i++) {
            if (!currentWord.contains(detectedLetters.get(i).toString())) {
                return false;
            }
        }
        return true;
    }

    //обновление
    private static void updateRegex(String guess, String feedback, List<Character> detectedLetters, List<String> regexList) {
        for (int pos = 0; pos < 5; pos++) {

            char currentChar = guess.charAt(pos);
            char feedbackForCurrentChar = feedback.charAt(pos);

            if (isGrayed(feedbackForCurrentChar)) {
                /*
                 Если наше предполагаемое слово содержит символы серого цвета, то есть те которые не входят в состав
                 загаданного слова, мы должны удалить эти символы, но, может возникнуть ситуация, когда данный символ маркируется
                 желтым или зеленым цветом, то есть присутствует, но может быть не на своем месте, в таком случае
                 слово необходимо оставить, удалить символ только для текущей позиции и продолжать делать проверки.
                 В других случах, необходимо удалить все позиции данных символов.

                 */
                if (detectedLetters.contains(currentChar)) {
                    String currRegex = regexList.get(pos);
                    String updatedRegex = currRegex.replace(currentChar, '\0');
                    regexList.set(pos, updatedRegex);
                } else {
                    // удаляем текущий символ из всех позиций
                    for (int regexIndex = 0; regexIndex < 5; regexIndex++) {
                        String currRegex = regexList.get(regexIndex);
                        String updatedRegex = currRegex.replace(currentChar, '\0');
                        regexList.set(regexIndex, updatedRegex);
                    }
                }
            } else if (isYellow(feedbackForCurrentChar)) {
                String currRegex = regexList.get(pos);
                String updatedRegex = currRegex.replace(currentChar, '\0');
                regexList.set(pos, updatedRegex);

                detectedLetters.add(guess.charAt(pos));
            } else if (isGreen(feedbackForCurrentChar)) {
                // Обновляем все символы
                regexList.set(pos, String.valueOf(currentChar));
                detectedLetters.add(guess.charAt(pos));
            }
        }
    }

    private static List<String> initializeRegex() {
        List<String> regexList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            regexList.add("[абвгдеёжзийклмнопрстуфхцчшщъыьэюя]");
        }
        return regexList;
    }

    private static boolean isGrayed(char c) {
        return c == 'X' || c == 'x';
    }

    private static boolean isGreen(char c) {
        return c == 'G' || c == 'g';
    }

    private static boolean isYellow(char c) {
        return c == 'Y' || c == 'y';
    }


}

