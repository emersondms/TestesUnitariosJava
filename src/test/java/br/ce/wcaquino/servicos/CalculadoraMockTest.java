package br.ce.wcaquino.servicos;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CalculadoraMockTest {

    @Mock
    private Calculadora calcMock;

    @Spy
    private Calculadora calcSpy;

    @Spy
    private EmailService email;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void teste() {
        Calculadora calc = mock(Calculadora.class);
        ArgumentCaptor<Integer> argCapt = forClass(Integer.class);
        when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
        assertEquals(5, calc.somar(1, 8));
        System.out.println(argCapt.getAllValues());
    }

    @Test
    public void deveMostrarDiferencaEntreMockSpy() {
        when(calcMock.somar(1, 2)).thenReturn(5);
        doReturn(5).when(calcSpy.somar(1, 2));

        doNothing().when(calcSpy).imprime();
        System.out.println("Mock: " + calcMock.somar(1, 2));
        System.out.println("Spy: " + calcMock.somar(1, 2));

        System.out.println("Mock");
        calcMock.imprime();
        System.out.println("Spy");
        calcSpy.imprime();
    }
}
