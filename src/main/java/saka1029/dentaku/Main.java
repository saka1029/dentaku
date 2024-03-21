package saka1029.dentaku;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {

    interface Term extends Closeable {
        String readLine(String prompt) throws IOException;
        PrintWriter writer();
    }

    static class JlineConsole implements Term {
        final Terminal terminal;
        final LineReader lineReader;
        final PrintWriter out;

        JlineConsole() throws IOException {
            terminal = TerminalBuilder.builder().build();
            org.jline.reader.Parser parser = new DefaultParser().eofOnEscapedNewLine(true);
            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(parser)
                    .build();
            out = terminal.writer();
        }

        @Override
        public void close() throws IOException {
            terminal.close();
        }

        @Override
        public String readLine(String prompt) throws IOException {
            try {
                return lineReader.readLine(prompt);
            } catch (EndOfFileException e) {
                return null;
            }
        }

        @Override
        public PrintWriter writer() {
            return out;
        }
    }

    static void run(Term term, String prompt) throws IOException {
        PrintWriter out = term.writer();
        Functions functions = Functions.of();
        Context context = Context.of(functions);
        L: while (true) {
            String line = term.readLine(prompt);
            if (line == null)
                break;
            line = line.trim();
            switch (line) {
                case ".quit":
                case ".exit":
                case ".end":
                    break L;
            }
            Expression e = Parser.parse(functions, line);
            try {
                Value value = e.eval(context);
                if (value != Value.NaN)
                    out.println(value);
            } catch (ValueException | ArithmeticException ex) {
                out.println(ex.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Term term = new JlineConsole();
        String prompt = "> ";
        run(term, prompt);
    }

}
