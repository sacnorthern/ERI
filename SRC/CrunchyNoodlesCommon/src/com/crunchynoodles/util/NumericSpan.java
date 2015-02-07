/***  Java Commons and Niceties Library from CrunchyNoodles.com
 ***  Copyright (C) 2014 in USA by Brian Witt , bwitt@value.net
 ***
 ***  Licensed under the Apache License, Version 2.0 ( the "License" ) ;
 ***  you may not use this file except in compliance with the License.
 ***  You may obtain a copy of the License at:
 ***        http://www.apache.org/licenses/LICENSE-2.0
 ***
 ***  Unless required by applicable law or agreed to in writing, software
 ***  distributed under the License is distributed on an "AS IS" BASIS,
 ***  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ***  See the License for the specific languatge governing permissions and
 ***  limitations under the License.
 ***/

package com.crunchynoodles.util;

/**
 *  A span of two numbers, inclusive.  There are no gaps in a span.
 *  The hash-code is the difference of high and low.
 *  If these values are very small or very large ( e.g. 1e50 ), the hash-code will be 0.
 *  The low and high end are bounded, i.e. they cannot be infinity.
 *
 *  <p> An alternative implementation could have {@code T} extend {@code Comparable}
 *  instead of {@code Number}.
 *  The class could then allow a {@code String} span.
 *
 * @author brian
 * @see <a href="http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/collect/Range.html">Guava Range</a>
 */
public class NumericSpan<T extends Number> {

    public NumericSpan( T low, T high )
    {
        this.m_low = low;
        this.m_high = high;
    }

    @Override
    public int hashCode()
    {
        return (int) (this.m_low.doubleValue() - this.m_high.doubleValue());
    }

    /***
     *  Convert to a pretty string.  If low is 20 and high is 400, then
     *  returns "20-400".
     * @return string that includes low and high values.
     */
    @Override
    public String toString()
    {
        StringBuilder   sb = new StringBuilder();

        sb.append( m_low.toString() );
        sb.append( '-' );
        sb.append( m_high.toString() );

        return sb.toString();
    }

    /***
     *  Expand the span to include a new value.
     *  Converts values to {@code double} for comparing.
     * @param toInclude value to include in low &lt;= value &lt;= high.
     */
    public void stretch( T toInclude )
    {
        if( toInclude.doubleValue() < m_low.doubleValue() )
        {
            this.m_low = toInclude;
        }
        else if( m_high.doubleValue() < toInclude.doubleValue() )
        {
            this.m_high = toInclude;
        }
    }

    public T getLow() { return this.m_low; }
    public void setLow( T v ) { this.m_low = v; }

    public T getHigh() { return this.m_high; }
    public void setHigh( T v ) { this.m_high = v; }

    //--------------------------  INSTANCE VARS  -------------------------

    private T     m_low;
    private T     m_high;

}
