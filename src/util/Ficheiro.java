package util;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Esta classe tem como objectivo ler e escrever num ficheiro, ficheiro esse que
 * está encarregue de guardar a última base de dados acedida
 *
 * @author Luís Sousa - 8090228
 */
public class Ficheiro {

    private File ficheiro;

    /**
     * Método 
     * @param File 
     */
    public Ficheiro(String File) {
        this.ficheiro = new File(File);
    }

    /**
     * Método que permite escrever no ficheiro
     * @param text texto adicionar no ficheiro 
     * @throws IOException 
     */
    public void write(String text) throws IOException {
        this.ficheiro.createNewFile();
        FileWriter fileWriter = new FileWriter(this.ficheiro, false);
        try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(text);
            printWriter.flush();
        }
    }

    /**
     * Método que permite ler no ficheiro
     * @return uma string com a informação do ficheiro
     */
    public String read() {
        FileReader reader;
        BufferedReader buffer;
        String linha = null;

        try {
            reader = new FileReader(this.ficheiro);
            buffer = new BufferedReader(reader);
            
            //leitura da primeira linha
            linha = buffer.readLine();
        } catch (IOException erro) {
            System.out.println(erro.getMessage());
        }
        return linha;
    }

}
