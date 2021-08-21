package Markdown;

import java.util.HashMap;
import java.util.Map;

public class Token
{

    public enum TokenType {
        H1,
        H2,
        H3,
        H4,
        H5,
        H6,

        STRING,

        STAR,

        BRACKET_1L,         // (
        BRACKET_2L,         // [
        BRACKET_3L,         // {
        BRACKET_1R,         // )
        BRACKET_2R,         // ]
        BRACKET_3R,         // }

        CODE,               // `
//        CODE_BLOCK,         // ```

        QUOTE,

//        ORDERED_LIST,
//        UNORDERED_LIST,

        TABLE_BREAK,        // |

        SPACE,
        RETURN,

        ERROR,
    }

    private final static Map<TokenType, String> tokenMap = new HashMap(){{
        put(TokenType.H1, "#");
        put(TokenType.H2, "##");
        put(TokenType.H3, "###");
        put(TokenType.H4, "####");
        put(TokenType.H5, "#####");
        put(TokenType.H6, "######");

        put(TokenType.STAR, "*");

        put(TokenType.BRACKET_1L, "(");
        put(TokenType.BRACKET_1R, ")");
        put(TokenType.BRACKET_2L, "[");
        put(TokenType.BRACKET_2R, "]");
        put(TokenType.BRACKET_3L, "{");
        put(TokenType.BRACKET_3R, "}");

        put(TokenType.CODE, "`");
        put(TokenType.QUOTE, ">");
        put(TokenType.TABLE_BREAK, "|");
        put(TokenType.SPACE, " ");
        put(TokenType.RETURN, "\n");

    }};


    private final TokenType mType;
    private String mExtData;

    public Token(TokenType type)
    {
        mType = type;
    }

    public Token(TokenType type, String extData)
    {
        mType = type;
        mExtData = extData;
    }

    public String getExtData()
    {
        return mExtData;
    }

    public TokenType getType()
    {
        return mType;
    }

    public String toString()
    {
        if (mType == TokenType.ERROR)
            return null;
        else if (mType == TokenType.STRING)
            return getExtData();
        else
            return tokenMap.get(mType);
    }

}
