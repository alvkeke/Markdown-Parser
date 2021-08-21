import Markdown.Parser;
import Markdown.Token;
import Markdown.Tokenizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainEntry
{

    public static void main(String[] args) throws Exception
    {

        ArrayList<Token> tokens = new Tokenizer(new File("README.md")).getTokens();

        Parser parser = new Parser(tokens);

        System.out.println(parser.result);

//        File file = new File("tokens.txt");
//        File file1 = new File("data.txt");
//
//        FileOutputStream fos = new FileOutputStream(file);
//        FileOutputStream fos1 = new FileOutputStream(file1);
//        OutputStreamWriter osw = new OutputStreamWriter(fos);
//        OutputStreamWriter osw1 = new OutputStreamWriter(fos1);
//
//        for (Token t : tokens)
//        {
//            String xx = t.toString();
//            if (xx != null) osw1.write(t.toString());
//            else
//                System.out.println(t);
//            if (t.getType() == Token.TokenType.RETURN)
//            {
//                osw.write("\n");
//                continue;
//            }
//            osw.write(t.getType().toString());
//            if (t.getExtData() != null)
//            {
//                osw.write(" : ");
//                osw.write(t.getExtData());
//            }
//            osw.write(" | ");
//        }
//
//        osw.close();
//        osw1.close();
//        fos.close();
//        fos1.close();

    }
}
