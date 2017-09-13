import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;

import static org.junit.runners.MethodSorters.*;

@FixMethodOrder(NAME_ASCENDING)
public class OrdemTest {

    private static int contador = 0;

    @Test
    public void inicia() {
        contador = 1;
    }

    @Test
    public void verifica() {
        Assert.assertEquals(1, contador);
    }
}
