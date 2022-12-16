import java.util.List;
import java.util.function.Function;
class TestFunction implements Function<List<String>, String> {
    String apply(List<String> args) {
        System.out.println("Input args: "+ args);
        return "ok, This is the result of the function executed ..";
    }
}