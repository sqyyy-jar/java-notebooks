import com.github.sqyyy.jnb.Entrypoint;
import com.github.sqyyy.jnb.JavaNotebooks;
import com.github.sqyyy.jnb.Page;

@Page(value = "Example-page", description = """
    This is an example-class testing the java notebooks annotation processing capabilities.
    """)
public class TestMain {
    public static void main(String[] args) throws Throwable {
        for (var it : JavaNotebooks.getEntrypointHandles()) {
            it.invoke((Object) new String[0]);
        }
    }

    @Entrypoint
    public static void start(String... a) {
        System.out.println("hi");
    }
}
