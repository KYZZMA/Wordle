
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_GRAY_BACKGROUND = "\u001B[100m";
    static Scanner one = new Scanner(System.in);

    public static void main(String[] args) {
        check();
    }

    public static void check() {
        System.out.println("Игра началась!");
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

        if (puzzleWord.equals(running(puzzleWord, list))) {
            System.out.println("You win!");
        }
    }

    public static ArrayList<String> scannerWord(ArrayList<String> list) {

        File path = new File("E:/vocabulary.txt");

        // пробуем считать словарь из файла в список
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String s;
            while ((s = br.readLine()) != null) {
                String[] split = s.split(" ");
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
        System.out.println("Загаданное слово: " + puzzleWord);

        return puzzleWord;
    }


    public static String running(String puzzleWord, ArrayList<String> list) {
        //запускаем цикл с условием для выхода из него
        String newWord = null;
        boolean flag = false;
        while (!flag) {
            flag = true;
            System.out.print("Введите слово из 5 букв: ");
            String supposeWord = one.nextLine();

            //делаем проверку на корректность введенного слова
            if (list.contains(supposeWord)) {

                //при нахождении предполагаемого слова, сравниваем его символы с загаданным словом

                String[] result = new String[puzzleWord.length()];
                for (int i = 0; i < supposeWord.length(); i++) {
                    if (puzzleWord.charAt(i) == supposeWord.charAt(i)) {
                        result[i] = String.valueOf(ANSI_GREEN_BACKGROUND + supposeWord.charAt(i) + ANSI_RESET);
                        continue;
                    } else if (puzzleWord.indexOf(supposeWord.charAt(i)) != -1) {
                        result[i] = "(" + ANSI_YELLOW_BACKGROUND + supposeWord.charAt(i) + ANSI_RESET + ")";
                        flag = false;
                    } else {
                        result[i] = ANSI_GRAY_BACKGROUND + "-" + ANSI_RESET;
                        flag = false;
                    }
                }

                //делаем проверку в словаре со словами, которые подходят по найденным символам
                StringBuilder sb = new StringBuilder();
                for (String ch : result) {
                    sb.append(ch);
                }
                newWord = sb.toString();

                String deliteChar = newWord.replaceAll("[^а-яА-Я]", "");
                if (deliteChar.length() > 0) {
                    sercheChar(list, deliteChar);
                }

                System.out.println(Arrays.toString(result));

            } else {
                // если слово не было в словаре, то сообщаем об этом пользователю и повторяем метод
                System.out.println("Такого слова не существует");
                running(puzzleWord, list);
            }

        }
        return newWord;
    }

    public static List<String> sercheChar(List<String> list, String newWord) {

        //реализуем проверку слов с выявленными символами
        String rar = newWord.replaceAll("(.)(?=.*\\1)", "");
        String[] strings = rar.split("");


        List<String> filtrarray = new ArrayList<>();

        for (String string : strings) {
            StringBuffer sb = new StringBuffer(string);
            filtrarray = list.stream()
                    .filter(x -> x.contains(sb))
                    .collect(Collectors.toList());

        }
        // передаем пользователю найденные слова
        System.out.print("Такие слова могут подойти: ");
        for (int i = 0; i < 3; i++) {
            int random = (int) (Math.random() * filtrarray.size());
            System.out.print(filtrarray.get(random) + " ");
        }

        return filtrarray;
    }


}


