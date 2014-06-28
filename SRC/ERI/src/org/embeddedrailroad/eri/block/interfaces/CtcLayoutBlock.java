/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.block.interfaces;

import org.embeddedrailroad.eri.layoutio.LayoutIoModel;

/**
 *  Blocks are units of control, signaling and detection.
 *  A block is either occupied or not-occupied.
 *  Blocks own zero or more signals, based on zero or more detectors.
 *
 * @author brian
 */
public interface CtcLayoutBlock
{
    /*** @return Name of this block. */
    public String       getName();

    /*** @return Model-specific address "name" of where detector is located. */
    public String       getModelLocationName();

    /*** @return underlying model for this Detector.  */
    public LayoutIoModel<?> getModel();
}
