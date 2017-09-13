package br.ce.wcaquino.servicos;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.*;

import java.lang.reflect.Method;
import java.util.List;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builder.LocacaoBuilder.umaLocacao;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterData;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocacaoServiceTest {

    @InjectMocks
    @Spy
    private static LocacaoService service;

    @Mock
    private static SPCService spc;

    @Mock
    private static LocacaoDAO dao;

    @Mock
    private static EmailService email;

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().comValor(5.0).agora());

        Mockito.doReturn(obterData(28, 4, 2017)).when(service).obterData();

        //Ação
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //Verificação
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(isMesmaData(
            locacao.getDataLocacao(), obterData(28, 4, 2017)), is(true)
        );
        error.checkThat(isMesmaData(
            locacao.getDataRetorno(), obterData(29, 4, 2017)), is(true)
        );
    }

    @Test(expected = FilmeSemEstoqueException.class) //Verificação
    public void naoDeveAlugarFilmeSemEstoque() throws Exception { //Elegante
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilmeSemEstoque().agora());

        //Ação
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException { //Robusta
        //Cenário
        List<Filme> filmes = asList(umFilme().agora());

        //Ação
        try {
            service.alugarFilme(null, filmes);
            fail();
        } catch (LocadoraException e) {
            //Verificação
            assertThat(e.getMessage(), is("Usuário vazio"));
        }
    }

    @Test
    public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException { //Nova
        //Cenário
        Usuario usuario = umUsuario().agora();

        //Verificação
        exception.expect(LocadoraException.class);
        exception.expectMessage("Filme vazio");

        //Ação
        service.alugarFilme(usuario, null);
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().agora());

        Mockito.doReturn(obterData(29, 4, 2017)).when(service).obterData();

        //Ação
        Locacao retorno = service.alugarFilme(usuario, filmes);

        //Verificação
        assertThat(retorno.getDataRetorno(), caiNumaSegunda());
    }

    @Test
    public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().agora());

        Mockito.when(spc.possuiNegativacao(any(Usuario.class))).thenReturn(true);

        //Ação
        try {
            service.alugarFilme(usuario, filmes);
            fail();
        } catch (LocadoraException e) {
            //Verificação
            assertThat(e.getMessage(), is("Usuário negativado"));
        }

        verify(spc).possuiNegativacao(usuario);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas() {
        //Cenário
        Usuario usuario = umUsuario().agora();
        Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
        Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
        List<Locacao> locacoes = asList(
            umaLocacao().atrasado().comUsuario(usuario).agora(),
            umaLocacao().comUsuario(usuario2).agora(),
            umaLocacao().atrasado().comUsuario(usuario3).agora(),
            umaLocacao().atrasado().comUsuario(usuario3).agora()
        );

        Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

        //Ação
        service.notificarAtrasos();

        //Verificação
        verify(email, times(3)).notificarAtraso(any(Usuario.class));
        verify(email).notificarAtraso(usuario);
        verify(email, atLeastOnce()).notificarAtraso(usuario3);
        verify(email, never()).notificarAtraso(usuario2);
        Mockito.verifyNoMoreInteractions(email);
    }

    @Test
    public void deveTratarErroNoSPC() throws Exception {
        //Cenário
        Usuario usuario = umUsuario().agora();
        List<Filme> filmes = asList(umFilme().agora());

        Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));

        //Verificação
        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas com SPC, tente novamente");

        //Ação
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveProrrogarUmaLocacao() {
        //Cenário
        Locacao locacao = umaLocacao().agora();

        //Ação
        service.prorrogarLocacao(locacao, 3);

        //Verificação
        ArgumentCaptor<Locacao> argCapt = forClass(Locacao.class);
        verify(dao).salvar(argCapt.capture());
        Locacao locacaoRetornada = argCapt.getValue();

        error.checkThat(locacaoRetornada.getValor(), is(12.0));
        error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
        error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencaDeDias(3));
    }

    @Test
    public void deveCalcularValorLocacao() throws Exception {
        //Cenário
        List<Filme> filmes = asList(umFilme().agora());

        //Ação
        Class<LocacaoService> clazz = LocacaoService.class;
        Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
        metodo.setAccessible(true);
        Double valor = (Double) metodo.invoke(service, filmes);

        //Verificação
        assertThat(valor, is(4.0));
    }
}
