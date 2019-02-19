package io.pp.net_disk_demo.util;

public class JsonUtil {
    /**
     * The unit is indented into the string.
     */
    private static String SPACE = "   ";

    /**
     * Returns a formatted JSON string.
     *
     * @param json Unformatted JSON string.
     * @return Formatted JSON string.
     */
    public static String formatJson(String json) {
        StringBuffer result = new StringBuffer();

        int length = json.length();
        int number = 0;
        char key = 0;

        //Traverse the input string.
        for (int i = 0; i < length; i++) {
            //1. Get the current character.
            key = json.charAt(i);

            //2. If the current character is the front bracket and the front bracket, do the following:
            if ((key == '[') || (key == '{')) {
                //(1) If there are characters in front and the characters are ":", print: line feed and indent character strings.
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }

                //(2) Print: current character.
                result.append(key);

                //(3) The front brackets and the front brackets must be followed by a new line. Print: Wrap.
                result.append('\n');

                //(4) The front brackets and the front brackets appear once each time; the number of indentations increases once. Print: New line indentation.
                number++;
                result.append(indent(number));

                //(5) Carry out the next cycle.
                continue;
            }

            //3. If the current character is a rear bracket and a rear bracket, do the following:
            if ((key == ']') || (key == '}')) {
                //(1) The rear brackets and the rear brackets must be wrapped in front of them. Print: Wrap.
                result.append('\n');

                //(2) Each time the rear brackets and the rear brackets appear; the number of indentations is reduced once. Print: Indent.
                number--;
                result.append(indent(number));

                //(3) Print: current character.
                result.append(key);

                //(4) If there is a character after the current character, and the character is not ",", print: Wrap.
                if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    result.append('\n');
                }

                //(5) Continue to the next cycle.
                continue;
            }

            //4. If the current character is a comma. Wrap the line after the comma and indent it without changing the number of indents.
            if ((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }

            //5. Print: current character.
            result.append(key);
        }

        return result.toString();
    }

    /**
     * Returns the indented string of the specified number of times. Indent three spaces each time, SPACE.
     *
     * @param number The number of indentations.
     * @return A string specifying the number of indents.
     */
    private static String indent(int number) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < number; i++) {
            result.append(SPACE);
        }
        return result.toString();
    }
}