package Markdown;

import sun.awt.IconInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Tokenizer
{

    enum TokenState {
        NONE,
        TITLE,
        CODE,
    }

    private final ArrayList<Token> mTokens;

    public Tokenizer(File file) throws Exception
    {


        mTokens = new ArrayList<>();

        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        TokenState state = TokenState.NONE;
        boolean newLine = true;

        int ich;
        int index = 0;
        StringBuilder sb = null; //new StringBuilder();

        while((ich = br.read()) >0)
        {
            char ch = (char)ich;

            if (ch == '\r') continue;

            if (state == TokenState.NONE)
            {
                Token.TokenType newTokenType = Token.TokenType.ERROR;
                if (newLine && ch == '#')
                {
                    index = 1;
                    state = TokenState.TITLE;
                }
                else if (ch == '*')
                    newTokenType = Token.TokenType.STAR;
                else if (ch == '`')
                    newTokenType = Token.TokenType.CODE;
                else if (ch == ' ')
                    newTokenType = Token.TokenType.SPACE;
                else if (ch == '\n')
                {
                    newTokenType = Token.TokenType.RETURN;
                    newLine = true;
                }
                else if (ch == '(')
                    newTokenType = Token.TokenType.BRACKET_1L;
                else if (ch == '[')
                    newTokenType = Token.TokenType.BRACKET_2L;
                else if (ch == '{')
                    newTokenType = Token.TokenType.BRACKET_3L;
                else if (ch == ')')
                    newTokenType = Token.TokenType.BRACKET_1R;
                else if (ch == ']')
                    newTokenType = Token.TokenType.BRACKET_2R;
                else if (ch == '}')
                    newTokenType = Token.TokenType.BRACKET_3R;
//                else if (newLine && ch == '>')
//                    newTokenType = Token.TokenType.QUOTE;
                else if (ch == '>')
                    newTokenType = Token.TokenType.QUOTE;
                else if (ch == '|')
                    newTokenType = Token.TokenType.TABLE_BREAK;
                else    // other characters
                {
                    if (sb == null) sb = new StringBuilder();
                    sb.append(ch);
                    continue;
                }

                if (sb != null) mTokens.add(new Token(Token.TokenType.STRING, sb.toString()));
                sb = null;
                if (newTokenType != Token.TokenType.ERROR) mTokens.add(new Token(newTokenType));

                if (ch != '\n' && ch != ' ') newLine = false;
                // space at front of a line, will not change the
            }
            else if (state == TokenState.TITLE)
            {
                if (ch != '#')
                {
                    br.reset();
                    state = TokenState.NONE;
                    Token.TokenType type;
                    switch (index)
                    {
                        case 1: type = Token.TokenType.H1; break;
                        case 2: type = Token.TokenType.H2; break;
                        case 3: type = Token.TokenType.H3; break;
                        case 4: type = Token.TokenType.H4; break;
                        case 5: type = Token.TokenType.H5; break;
                        case 6: type = Token.TokenType.H6; break;
                        default: type = Token.TokenType.ERROR;
                    }

                    if (type != Token.TokenType.ERROR)
                    {
                        mTokens.add(new Token(type));
                    }

                    continue;
                }
                index++;
            }


            if (state == TokenState.NONE) continue;
            br.mark(1);
        }

        if (sb != null) mTokens.add(new Token(Token.TokenType.STRING, sb.toString()));

        br.close();
        isr.close();
        fis.close();

    }

    public final ArrayList<Token> getTokens()
    {
        return mTokens;
    }
}
