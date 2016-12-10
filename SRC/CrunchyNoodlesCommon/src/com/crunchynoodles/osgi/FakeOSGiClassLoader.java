/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.osgi;

import java.lang.ref.WeakReference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;

/**
 *  Facade class-type for Java ClassLoader that also knows the bundle it loaded.
 *  Java 1.7 {@link ClassLoader} allows for parallel-ized class loaded; we are single loading.
 *
 * <p> I found this code in {@link org.osgi.framework.FrameworkUtil} that cases the {@link ClassLoader}
 *  into a {@link Bundle} ...
<blockquote><pre>
	 **
	 * Return a {@code Bundle} for the specified bundle class. The returned
	 * {@code Bundle} is the bundle associated with the bundle class loader
	 * which defined the specified class.
	 *
	 * \@param classFromBundle A class defined by a bundle class loader.
	 * \@return A {@code Bundle} for the specified bundle class or {@code null}
	 *         if the specified class was not defined by a bundle class loader.
	 * \@since 1.5
	 *
	public static Bundle getBundle(final Class<?> classFromBundle) {
		// We use doPriv since the caller may not have permission
		// to call getClassLoader.
		Object cl = AccessController.doPrivileged(new PrivilegedAction\<Object\>() {
			public Object run() {
				return classFromBundle.getClassLoader();
			}
		});

		if (cl instanceof BundleReference) {
			return ((BundleReference) cl).getBundle();
		}
		return null;
	}
</pre></blockquote>
 * @author brian
 */
public class FakeOSGiClassLoader extends ClassLoader implements BundleReference
{

    public FakeOSGiClassLoader( Bundle b )
    {
        super();
        bundleWeLoaded = new WeakReference<Bundle>( b );
    }

    public FakeOSGiClassLoader( ClassLoader parent, Bundle b )
    {
        super( parent );
        bundleWeLoaded = new WeakReference<Bundle>( b );
    }

    @Override
    public Bundle getBundle()
    {
        return bundleWeLoaded.get();
    }

    /*** bundle loaded by this class-loader object. */
    protected transient WeakReference< Bundle >  bundleWeLoaded;

}
