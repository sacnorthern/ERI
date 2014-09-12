/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio;

/***
 *  Controller for transport-specific model (MVC scheme).
 *  This general class-type wraps a transport-specific model object.
 *
 * @author brian
 */
public class LayoutIoController
{

    public LayoutIoController( LayoutIoModel model )
    {
        this.m_model = model;
    }

    /***
     *  Get class-type of address {@code TUnitAddr} thing.
     * @return class-type of address {@code TUnitAddr} thing.
     */
    public Class   getUnitAddressType()
    {
        return m_model.getUnitAddressType();
    }

    //---------------------------  INSTANCE VARS  -----------------------------

    protected LayoutIoModel     m_model;
}
