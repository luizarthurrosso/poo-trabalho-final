package util;

import java.util.Map;
import java.util.HashMap;

public class TabelasAuxiliares {

    private static final Map<String, String> NOMES_DISCIPLINAS = new HashMap<>();
    private static final Map<Integer, String> DIAS_SEMANA = new HashMap<>();
    private static final Map<Integer, String> TITULOS_DOCENTE = new HashMap<>();

    static {
        NOMES_DISCIPLINAS.put("10850", "Algoritmos e Programação");
        NOMES_DISCIPLINAS.put("10854", "Fundamentos Matemáticos");
        NOMES_DISCIPLINAS.put("10851", "Introd. Ciência da Computação");

        DIAS_SEMANA.put(1, "Domingo");
        DIAS_SEMANA.put(2, "Segunda-Feira");
        DIAS_SEMANA.put(3, "Terça-Feira");
        DIAS_SEMANA.put(4, "Quarta-Feira");
        DIAS_SEMANA.put(5, "Quinta-Feira");
        DIAS_SEMANA.put(6, "Sexta-Feira");
        DIAS_SEMANA.put(7, "Sábado");

        TITULOS_DOCENTE.put(1, "Pós-Graduação");
        TITULOS_DOCENTE.put(2, "Mestrado");
        TITULOS_DOCENTE.put(3, "Doutorado");
        TITULOS_DOCENTE.put(4, "Pós-Doutorado");
    }

    public static String getNomeDisciplina(String codigo) {
        return NOMES_DISCIPLINAS.getOrDefault(codigo, "Desconhecida");
    }

    public static String getDiaSemana(int codigo) {
        return DIAS_SEMANA.getOrDefault(codigo, "Inválido");
    }

    public static String getTituloDocente(int codigo) {
        return TITULOS_DOCENTE.getOrDefault(codigo, "Não informado");
    }
}