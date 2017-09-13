package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class CalculadoraTest {

    private static Calculadora calc;

    @BeforeClass
    public static void setup() {
        calc = new Calculadora();
    }

    @Test
    public void deveSomarDoisValores() {
        //Cenário
        int a = 5;
        int b = 3;

        //Ação
        int resultado = calc.somar(a, b);

        //Verificação
        assertEquals(8, resultado);
    }

    @Test
    public void deveSubtrairDoisValores() {
        //Cenário
        int a = 8;
        int b = 5;

        //Ação
        int resultado = calc.subtrair(a, b);

        //Verificação
        assertEquals(3, resultado);
    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
        //Cenário
        int a = 6;
        int b = 3;

        //Ação
        int resultado = calc.dividir(a, b);

        //Verificação
        assertEquals(2, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
        int a = 10;
        int b = 0;
        calc.dividir(a, b);
    }

    @Test
    public void deveDividir() {
        String a = "6";
        String b = "3";
        int resultado = calc.dividir(a, b);
        assertEquals(2, resultado);
    }
}
