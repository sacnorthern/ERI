/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.block.interfaces;

import org.embeddedrailroad.eri.layoutio.LayoutIoModel;

/**
 *  Interface for presence detectors on layout.
 *  Each detector is owned by a block, and a block may have zero or more detectors.
 *  If detector-thing is not part of a block, then it is a {@link CtcLayoutSensor}.
 *
 * @author brian
 */
public interface CtcLayoutDetector
{
    /*** @return Name of this detector. */
    public String       getName();

    /*** @return Model-specific address "name" of where detector is located. */
    public String       getModelLocationName();

    /*** @return underlying model for this Detector.  */
    public LayoutIoModel<?> getModel();
}
