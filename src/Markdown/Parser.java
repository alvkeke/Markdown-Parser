package Markdown;

import java.util.ArrayList;

public class Parser
{

    public String result;
    private final ArrayList<Token> tokens;
    private int index;

    private String title()
    {
        return null;
    }


    private String quote()
    {
        return null;
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

            sb.append(t.toString());
        }

        return null;
    }

    private String table()
    {
        return null;
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

    private int codes_checkEnd(int iBegin)
    {
        int sym_count = 0;

        while (iBegin < tokens.size())
        {
            Token t = tokens.get(iBegin++);
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

        StringBuilder sb = new StringBuilder();

        sb.append("<code type=\"")
                .append(codeType)
                .append("\">\n")
                .append(codeBody)
                .append("\n")
                .append("</code>");


        return sb.toString();
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
