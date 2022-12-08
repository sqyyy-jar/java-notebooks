import com.github.sqyyy.jnb.Entrypoint;
import com.github.sqyyy.jnb.JavaNotebooks;
import com.github.sqyyy.jnb.Page;

import java.lang.reflect.InvocationTargetException;

@Page(value = "Example-page", description = """
    This is an example-class testing the java notebooks annotation processing capabilities.
    """)
public class TestMain {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        for (var it : JavaNotebooks.getEntrypointMethods()) {
            it.invoke(null, (Object) new String[0]);
        }
    }

    @Entrypoint
    public static void start(String... a) {
        System.out.println("hi");
    }
}
