/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc;

/**
 *
 * @author brian
 */
public class MainServer
{

    public static void main(String[] args)
    {
        EriCase   eri = EriCase.getInstance();

        eri.doit();
    }


}
