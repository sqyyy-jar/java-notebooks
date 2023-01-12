import com.github.sqyyy.jnb.Entrypoint;
import com.github.sqyyy.jnb.JavaNotebooks;
import com.github.sqyyy.jnb.Page;

@Page(value = "Example-page", description = """
    This is an example-class testing the java notebooks annotation processing capabilities.
    """)
public class TestMain {
    public static void main(String[] args) throws Throwable {
        for (var it : JavaNotebooks.getEntrypointHandles()) {
            if (it.type().parameterCount() == 1) {
                it.invoke((Object) new String[0]);
            } else {
                it.invoke();
            }
        }
    }

    @Entrypoint
    public static void start(String... a) {
        System.out.println("hi");
    }
}
