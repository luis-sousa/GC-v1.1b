/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author luis__000
 */
public class MoneyConverter {

    public static int getCentimos(float euros) {
        return (int) (Math.round(euros * 100));
    }

    public static float getEuros(int centimos) {
        return ((float) (centimos * 0.01));
    }

    /**
     * Método que permite arredondar os cálculos
     *
     * @param euros valor arredondar
     * @param numberOfDecimalPlaces numero de casas decimais
     * @return valor depois de arredondado
     */
    public static float round(float euros, int numberOfDecimalPlaces) {
        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = euros * multipicationFactor;
        return (float) (Math.round(interestedInZeroDPs) / multipicationFactor);
    }

}
