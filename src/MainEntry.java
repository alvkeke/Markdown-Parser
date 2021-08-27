import Markdown.Parser;
import Markdown.Token;
import Markdown.Tokenizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

public class MainEntry
{

    public static void main(String[] args) throws Exception
    {


        long t1 = new Date().getTime();

        ArrayList<Token> tokens = new Tokenizer(new File("README.md")).getTokens();

        Parser parser = new Parser(tokens);

        long t2 = new Date().getTime();

        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t2 - t1);

        System.out.println(parser.result);

        File f_html = new File("index.html");
        FileOutputStream fos_html = new FileOutputStream(f_html);
        OutputStreamWriter osw_html = new OutputStreamWriter(fos_html);

        osw_html.write(parser.result);
        osw_html.flush();

        osw_html.close();
        fos_html.close();

        File f_tokens = new File("tokens.txt");
        File f_data = new File("data.txt");

        FileOutputStream fos_tokens = new FileOutputStream(f_tokens);
        FileOutputStream fos_data = new FileOutputStream(f_data);
        OutputStreamWriter osw_tokens = new OutputStreamWriter(fos_tokens);
        OutputStreamWriter osw_data = new OutputStreamWriter(fos_data);

        for (Token t : tokens)
        {
            String xx = t.toString();
            if (xx != null) osw_data.write(t.toString());
            else
                System.out.println(t);
            if (t.getType() == Token.TokenType.RETURN)
            {
                osw_tokens.write("\n");
                continue;
            }
            osw_tokens.write(t.getType().toString());
            if (t.getExtData() != null)
            {
                osw_tokens.write(" : ");
                osw_tokens.write(t.getExtData());
            }
            osw_tokens.write(" | ");
        }

        osw_tokens.close();
        osw_data.close();
        fos_tokens.close();
        fos_data.close();

    }
}
