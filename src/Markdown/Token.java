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

    @SuppressWarnings("unchecked")
    private final static Map<TokenType, String> tokenMap = new HashMap();
    static {
        tokenMap.put(TokenType.H1, "#");
        tokenMap.put(TokenType.H2, "##");
        tokenMap.put(TokenType.H3, "###");
        tokenMap.put(TokenType.H4, "####");
        tokenMap.put(TokenType.H5, "#####");
        tokenMap.put(TokenType.H6, "######");

        tokenMap.put(TokenType.STAR, "*");

        tokenMap.put(TokenType.BRACKET_1L, "(");
        tokenMap.put(TokenType.BRACKET_1R, ")");
        tokenMap.put(TokenType.BRACKET_2L, "[");
        tokenMap.put(TokenType.BRACKET_2R, "]");
        tokenMap.put(TokenType.BRACKET_3L, "{");
        tokenMap.put(TokenType.BRACKET_3R, "}");

        tokenMap.put(TokenType.CODE, "`");
        tokenMap.put(TokenType.QUOTE, ">");
        tokenMap.put(TokenType.TABLE_BREAK, "|");
        tokenMap.put(TokenType.SPACE, " ");
        tokenMap.put(TokenType.RETURN, "\n");

    }


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
