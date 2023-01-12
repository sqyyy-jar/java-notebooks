import com.github.sqyyy.jnb.Entrypoint;
import com.github.sqyyy.jnb.Page;

@Page("Hey")
public class TestClass {
    @Entrypoint
    public static void entrypointWithoutArgs() {
        System.out.println("entrypointWithoutArgs");
    }
}
