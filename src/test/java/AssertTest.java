import br.ce.wcaquino.entidades.Usuario;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssertTest {

    @Test
    public void teste() {
        assertTrue(!false);
        assertFalse(!true);

        assertEquals("Erro de comparação",1, 1);
        assertEquals(0.51, 0.51, 0.01);
        assertEquals(Math.PI, 3.14, 0.01);

        int i = 5;
        Integer i2 = 5;
        assertEquals(Integer.valueOf(i), i2);
        assertEquals(i, i2.intValue());

        assertEquals("teste", "teste");
        assertNotEquals("teste", "teste2");
        assertTrue("teste".equalsIgnoreCase("Teste"));
        assertTrue("teste".startsWith("te"));

        Usuario u1 = new Usuario("Usuario 1");
        Usuario u2 = new Usuario("Usuario 2");
        Usuario u3 = null;
        assertEquals(u1, u2);
        assertSame(u1, u2);
        assertNotSame(u1, u2);
        assertNull(u3);
        assertNotNull(u2);
    }
}
