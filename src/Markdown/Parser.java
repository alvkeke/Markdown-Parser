package Markdown;

import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;

public class Parser
{

    public String result;
    private final ArrayList<Token> tokens;
    private int index;

    private int star_get_count()
    {
        int i;
        for (i=0; i<3; i++)
        {
            int nIndex = index + i;
            if (nIndex >= tokens.size()) return i;
            Token t = tokens.get(nIndex);
            if (!t.getType().equals(Token.TokenType.STAR)) break;
        }

        return i;
    }

    private String text_pkg_code(String origin)
    {
        return "<code>"+origin+"</code>";
    }

    private String text_pkg_bold(String origin)
    {
        return "<p class=\"fw-bold\">" + origin + "</p>";
    }

    private String text_pkg_italic(String origin)
    {
        return "<p class=\"fst-italic\">" + origin + "</p>";
    }

    private String text_code()
    {
        Token t = tokens.get(index);
        if (!t.getType().equals(Token.TokenType.CODE)) return null;
        index++;

        StringBuilder sb = new StringBuilder();
        while (index < tokens.size())
        {
            t = tokens.get(index++);
            if (t.getType().equals(Token.TokenType.CODE)) return sb.toString();
            if (t.getType().equals(Token.TokenType.RETURN))
            {
                index--;
                return null;
            }
            sb.append(t);
        }

        return null;
    }

    private String text_normal()
    {
        if (index >= tokens.size()) return null;

        Token t = tokens.get(index);
        if (t.getType().equals(Token.TokenType.RETURN)) return null;

        StringBuilder sb = new StringBuilder();
        sb.append(t);
        index++;

        while (index < tokens.size())
        {
            t = tokens.get(index);
            Token.TokenType type = t.getType();
            if (type.equals(Token.TokenType.RETURN) ||
                    type.equals(Token.TokenType.CODE) ||
                    type.equals(Token.TokenType.STAR))
                return sb.toString();

            sb.append(t);
            index++;
        }

        return sb.toString();
    }

    private String text_italic()
    {
        if (index >= tokens.size()) return null;

        Token t = tokens.get(index);
        if (!t.getType().equals(Token.TokenType.STAR)) return null;
        index++;

        StringBuilder sb = new StringBuilder();

        while (index < tokens.size())
        {
            t = tokens.get(index);

            int star_count = star_get_count();
            if (star_count == 1 || star_count > 2)
            {
                index++;
                return sb.toString();
            }

            String s_ret = null;

            int i_save = index;

            if (star_count == 2)
            {
                s_ret = text_bold();
                if (s_ret != null)
                {
                    sb.append(text_pkg_bold(s_ret));
                    continue;
                }
                index = i_save;
            }

            if (t.getType() == Token.TokenType.CODE) s_ret = text_code();
            if (s_ret != null)
            {
                sb.append(text_pkg_code(s_ret));
                continue;
            }

            index = i_save;
            s_ret = text_normal();
            if (s_ret != null)
            {
                sb.append(s_ret);
            }
            else return null;

        }

        return null;
    }

    private String text_bold()
    {
        for (int i=0; i<2; i++)
            if (index >= tokens.size()-i) return null;

        index += 2;

        StringBuilder sb = new StringBuilder();

        while (index < tokens.size())
        {

            int star_count = star_get_count();
            if (star_count >= 2)
            {
                index += 2;
                return sb.toString();
            }

            Token t = tokens.get(index);

            String s_ret = null;

            int i_save = index;

            if (star_count == 1)
            {
                if (t.getType() == Token.TokenType.STAR) s_ret = text_italic();
                if (s_ret != null)
                {
                    sb.append(text_pkg_italic(s_ret));
                    continue;
                }
                index = i_save;
            }

            if (t.getType() == Token.TokenType.CODE) s_ret = text_code();
            if (s_ret != null)
            {
                sb.append(text_pkg_code(s_ret));
                continue;
            }

            index = i_save;
            s_ret = text_normal();
            if (s_ret != null)
            {
                sb.append(s_ret);
            }
            else return null;

        }

        return null;
    }

    private String line()
    {

        boolean check_bold = true;
        boolean check_italic = true;
        boolean check_code = true;

        StringBuilder sb = new StringBuilder();

        while (index < tokens.size())
        {
            Token t = tokens.get(index);
            if (t.getType().equals(Token.TokenType.RETURN))
            {
                index++;
                return sb.toString();
            }

            int star_count = star_get_count();
            String s_get;

            int index_save = index;
            if (check_bold &&star_count == 2)
            {
                s_get = text_bold();
                if (s_get == null)
                    check_bold = false;
                else
                {
                    s_get = text_pkg_bold(s_get);
                }
            }
            else if (check_italic && star_count >= 1)
            {
                s_get = text_italic();
                if (s_get == null)
                    check_italic = false;
                else
                {
                    if (s_get.equals(""))
                        s_get = "**";
                    else
                        s_get = text_pkg_italic(s_get);
                }
            }
            else if (check_code && t.getType().equals(Token.TokenType.CODE))
            {
                s_get = text_code();
                if (s_get == null)
                    check_code = false;
                else
                {
                    s_get = text_pkg_code(s_get);
                }
            }
            else
            {
                s_get = text_normal();
            }

            if (s_get != null)
            {
                sb.append(s_get);
                check_bold = true;
                check_italic = true;
                check_code = true;
            }
            else
            {
                index = index_save;
            }
        }

        return null;
    }

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

    private String quote_pkg_data(ArrayList<Integer> levels, ArrayList<String> lines)
    {

        if (levels == null || lines == null) return null;
        if (levels.size() != lines.size()) return null;

        int last_level = 1;

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"callout callout-default\">\n");

        for (int i=0; i<levels.size(); i++)
        {
            int lv = levels.get(i);

            if (lv > last_level)
            {
                int diff = lv - last_level;
                for (int j=0; j<diff; j++)
                {
                    sb.append("<div class=\"callout callout-default\">\n");
                }
            }
            else if (lv < last_level)
            {
                int diff = last_level - lv;
                for (int j=0; j<diff; j++)
                {
                    sb.append("</div>\n");
                }
            }

            sb.append("<p>").append(lines.get(i)).append("</p>\n");
            last_level = lv;
        }

        if (last_level > 1)
        {
            int diff = last_level - 1;
            for (int j=0; j<diff; j++)
            {
                sb.append("</div>\n");
            }
        }

        sb.append("</div>\n");

        return sb.toString();
    }

    private int quote_move_level()
    {
        int i=0;
        while (index < tokens.size())
        {
            Token t = tokens.get(index++);
            switch (t.getType())
            {
                case QUOTE:
                    i++;
                case SPACE:
                    break;
                default:
                    index--;
                    return i;
            }
        }

        return 0;
    }

    private String quote()
    {
        if (index >= tokens.size()) return null;
        if (!tokens.get(index).getType().equals(Token.TokenType.QUOTE)) return null;

        ArrayList<Integer> levels = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();

        while (index < tokens.size())
        {
            int level = quote_move_level();
            if (level == 0)
                return quote_pkg_data(levels, lines);

            levels.add(level);
            lines.add(line());

        }

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

    private boolean code_block_checkBegin()
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

    private String code_block_getType()
    {
        StringBuilder sb = new StringBuilder();
        while (index < tokens.size())
        {
            Token t = tokens.get(index++);
            if (t.getType().equals(Token.TokenType.RETURN)) return sb.toString();
            sb.append(t);
        }

        return null;
    }

    private int code_block_checkEnd(int i_begin)
    {
        int sym_count = 0;

        while (i_begin < tokens.size())
        {
            Token t = tokens.get(i_begin++);
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

    private String code_block_getBody()
    {

        StringBuilder sb = new StringBuilder();

        while (index < tokens.size())
        {
            Token t = tokens.get(index++);

            if (t.getType().equals(Token.TokenType.RETURN))
            {
                int ret = code_block_checkEnd(index);
                if (ret > 0)
                {
                    index += ret;
                    return sb.toString();
                }
            }

            sb.append(t);
        }

        return null;
    }

    private String codd_block()
    {

        if (!code_block_checkBegin()) return null;

        String codeType = code_block_getType();

        if (codeType == null) return null;

        String codeBody = code_block_getBody();

        if (codeBody == null) return null;

        return "<code type=\"" + codeType + "\">\n" + codeBody + "\n</code>\n";
    }

    private String block()
    {
        int saveIndex = index;

        String result = codd_block();
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

        index = saveIndex;
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
