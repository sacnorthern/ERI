/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.embeddedrailroad.eri.layoutio;

import org.osgi.framework.*;

/**
 *  Kind of like an OSGi activator class-type, to manage starting and stopping layout "bundle" lifetime.
 *  There will be only one instance of each protocol-activator created (though not enforced).
 * <p>
 *  See these URLs for more info:
 * <ul>
 *  <li>See http://felix.apache.org/documentation/subprojects/apache-felix-ipojo/apache-felix-ipojo-gettingstarted/ipojo-in-10-minutes.html
 *  for how simple bundle activation can be!
 *  <li> See http://www.osgi.org/javadoc/r4v43/core/org/osgi/framework/BundleContext.html for documentation.
 * </ul>
 * @author brian
 */
public interface LayoutIoActivator
    extends org.osgi.framework.BundleActivator
{

//    @Override
//    public void start(BundleContext context);
//
//    /***
//     *  "The same BundleContext object will be passed to the BundleActivator.stop(BundleContext) method when the context bundle is stopped."
//     *  "The Framework is the only entity that can create BundleContext objects and they are only valid within the Framework that created them."
//     *
//     * @param context Same context passed to {@link start(BundleContext context)}.
//     */
//    @Override
//    public void stop(BundleContext context);

    /***
     *  Return a version string for this bundle, where "build" is an optional keyword.
     * @return String, e.g. "1.0.0 build 34838"
     */
    public String  versionValue();

}
