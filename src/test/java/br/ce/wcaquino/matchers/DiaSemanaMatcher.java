package br.ce.wcaquino.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static br.ce.wcaquino.utils.DataUtils.*;

public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {

    private Integer diaSemana;

    public DiaSemanaMatcher(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    @Override
    public void describeTo(Description desc) {
        Calendar data = Calendar.getInstance();
        data.set(Calendar.DAY_OF_WEEK, diaSemana);
        desc.appendText(data.getDisplayName(
            Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR")
        ));
    }

    @Override
    protected boolean matchesSafely(Date data) {
        return verificarDiaSemana(data, diaSemana);
    }
}
