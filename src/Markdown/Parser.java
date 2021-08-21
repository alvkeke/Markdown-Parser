package Markdown;

import java.util.ArrayList;

public class Parser
{

    public String result;
    private final ArrayList<Token> tokens;
    private int index;


    private String title_pkg_html(int title_level, String line)
    {
        if (title_level<=0 || title_level>6) return null;
        return "<h" +
                title_level +
                ">" +
                line +
                "</h" +
                title_level +
                ">";
    }

    private String title()
    {
        if (index >= tokens.size()) return null;
        Token t = tokens.get(index);

        int level = t.getType().ordinal()+1;
        if (level > 6) return null;
        index++;
        while (index < tokens.size())
        {
            t = tokens.get(index);
            if (!t.getType().equals(Token.TokenType.SPACE)) break;
            index++;
        }

        String title_data = line();

        return title_pkg_html(level, title_data);
    }

    private String list()
    {
        return null;
    }

    private String line()
    {

        StringBuilder sb = new StringBuilder();
        while (index < tokens.size())
        {
            Token t = tokens.get(index++);
            if (t.getType().equals(Token.TokenType.RETURN))
                return sb.toString();

            sb.append(t);
        }

        return null;
    }



    private String quote()
    {
        return null;
    }


    /**
     * This function was used to get string in a form block.
     * @param sb StringBuilder, it was used for data output.
     * @param strictEnd indicated that if this method allow a line end without TABLE_BREAK
     * @return 0 for success reached TABLE_BREAK, 1 for the end a line. -1 for error.
     */
    private int table_getString(StringBuilder sb, boolean strictEnd)
    {

        while (index < tokens.size())
        {
            Token t = tokens.get(index);
            if (!t.getType().equals(Token.TokenType.SPACE)) break;
            index++;
        }
        if (index >= tokens.size()) return -1;

        Token t = tokens.get(index);
        if (t.getType().equals(Token.TokenType.RETURN))
        {
            index++;
            return 1;
        }

        while (index < tokens.size())
        {
            t = tokens.get(index++);
            if (t.getType().equals(Token.TokenType.RETURN))
            {
                if (strictEnd)
                    return -1;
                else
                    return 0;
            }
            if (t.getType().equals(Token.TokenType.TABLE_BREAK)) return 0;
            sb.append(t);
        }

        return -1;
    }


    /**
     * this function was used to translate table format to integer.
     * @param fmt the format string like `:---:` / `:-----` / `-----:` and so on.
     * @return 1 for middle, 0 for left-align, 2 for right-align, -1 for error.
     */
    private int table_fmt_to_int(String fmt)
    {

        fmt = fmt.trim();
        char[] chars = fmt.toCharArray();
        for (char c : chars)
        {
            if (c != '-' && c != ':') return 3;
        }

        if (chars[0] == ':' && chars[chars.length-1] == ':') return 1;
        if (chars[0] == ':') return 0;
        if (chars[chars.length-1] == ':') return 2;

        return 0;
    }

    private int[] table_getAlign()
    {

        Token t = tokens.get(index++);
        if (!t.getType().equals(Token.TokenType.TABLE_BREAK)) return null;
        ArrayList<String> array = new ArrayList<>();

        while (index < tokens.size())
        {
            StringBuilder sb = new StringBuilder();
            int ret = table_getString(sb, true);
            if (ret == -1) return null;

            if (ret == 1)
            {
                if (array.size() == 0) return null;
                int[] aligns = new int[array.size()];
                for (int i=0; i<aligns.length; i++)
                {
                    aligns[i] = table_fmt_to_int(array.get(i));
                }
                return aligns;
            }
            array.add(sb.toString().trim());
        }


        return null;
    }

    private String[] table_getHead()
    {

        if (index >= tokens.size()) return null;
        if (!tokens.get(index++).getType().equals(Token.TokenType.TABLE_BREAK)) return null;
        ArrayList<String> array = new ArrayList<>();

        while (index < tokens.size())
        {
            StringBuilder sb = new StringBuilder();
            int ret = table_getString(sb, true);
            if (ret == -1) return null;

            if (ret == 1)
            {
                if (array.size() == 0) return null;
                return array.toArray(new String[0]);
            }
            array.add(sb.toString().trim());
        }

        return null;
    }

    private String[] table_getContentLine()
    {

        if (!tokens.get(index++).getType().equals(Token.TokenType.TABLE_BREAK)) return null;
        ArrayList<String> array = new ArrayList<>();

        while (index < tokens.size())
        {
            StringBuilder sb = new StringBuilder();
            int ret = table_getString(sb, false);
            if (ret == -1) return null;

            if (ret == 1)
            {
                return array.toArray(new String[0]);
            }
            array.add(sb.toString().trim());
        }

        return null;
    }

    private String table_pkg_html(String[] headers, int[] aligns, ArrayList<String[]> tableContents)
    {
        // TODO: finish this HTML format packaging method.

        StringBuilder sb = new StringBuilder();

        sb.append("<table>\n");

        // table head
        sb.append("<tr>\n");
        for (String s : headers)
        {
            sb.append("<th>")
                    .append(s)
                    .append("</th>\n");
        }
        sb.append("</tr>\n");

        // table content

        for (String[] content : tableContents)
        {
            sb.append("<tr>\n");

            for (int i=0; i<headers.length; i++)
            {
                if (i >= content.length) break;
                sb.append("<td>")
                        .append(content[i])
                        .append("</td>\n");
            }

            sb.append("</tr>\n");
        }


        sb.append("</table>\n");

        String result = sb.toString();

        return sb.toString();
    }

    private String table()
    {

        String[] headers = table_getHead();
        if (headers == null) return null;

        int[] table_align = table_getAlign();
        if (table_align == null || table_align.length != headers.length) return null;


        ArrayList<String[]> table_contents = new ArrayList<>();

        while (index < tokens.size())
        {
            Token t = tokens.get(index);
            if (!t.getType().equals(Token.TokenType.TABLE_BREAK)) break;
            String[] content = table_getContentLine();
            if (content == null) return null;
            table_contents.add(content);
        }


        return table_pkg_html(headers, table_align, table_contents);
    }

    private boolean codes_checkBegin()
    {
        for (int i=0; i<3; i++)
        {
            index++;
            if (index >= tokens.size()) return false;
            Token t = tokens.get(index);
            if ( ! t.getType().equals(Token.TokenType.CODE)) return false;
        }

        while(index < tokens.size() && tokens.get(index).getType().equals(Token.TokenType.CODE))
            index++;

        return true;
    }

    private String codes_getType()
    {
        StringBuilder sb = new StringBuilder();
        while (index < tokens.size())
        {
            Token t = tokens.get(index++);
            if (t.getType().equals(Token.TokenType.RETURN)) return sb.toString();
            sb.append(t.toString());
        }

        return null;
    }

    private int codes_checkEnd(int ibegin)
    {
        int sym_count = 0;

        while (ibegin < tokens.size())
        {
            Token t = tokens.get(ibegin++);
            if (t.getType().equals(Token.TokenType.CODE))
            {
                sym_count ++;
            }
            else if (t.getType().equals(Token.TokenType.RETURN))
            {
                if (sym_count >= 3)
                    return sym_count;
                else
                    return 0;
            }
            else
            {
                return 0;
            }
        }

        return 0;
    }

    private String codes_getBody()
    {

        StringBuilder sb = new StringBuilder();

        while (index < tokens.size())
        {
            Token t = tokens.get(index++);

            if (t.getType().equals(Token.TokenType.RETURN))
            {
                int ret = codes_checkEnd(index);
                if (ret > 0)
                {
                    index += ret;
                    return sb.toString();
                }
            }

            sb.append(t.toString());
        }

        return null;
    }

    private String codes()
    {

        if (!codes_checkBegin()) return null;

        String codeType = codes_getType();

        if (codeType == null) return null;

        String codeBody = codes_getBody();

        if (codeBody == null) return null;

        return "<code type=\"" + codeType + "\">\n" + codeBody + "\n</code>\n";
    }

    private String block()
    {
        int saveIndex = index;

        String result = codes();
        if (result != null)
            return result;

        index = saveIndex;
        result = table();
        if (result != null) return result;

        index = saveIndex;
        result = quote();
        if (result != null) return result;

        index = saveIndex;
        result = list();
        if (result != null) return result;

        index = saveIndex;
        result = title();
        if (result != null) return result;

        return line();
    }

    private void document()
    {
        StringBuilder sb = new StringBuilder();

        String newStr;

        while((newStr = block()) != null)
        {
            sb.append(newStr);
            sb.append("\n");
        }

        result =  sb.toString();
    }

    public Parser(ArrayList<Token> tokens)
    {
        this.tokens = tokens;
        index = 0;
        document();
    }


}
