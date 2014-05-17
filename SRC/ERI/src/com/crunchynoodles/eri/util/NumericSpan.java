/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crunchynoodles.eri.util;

/**
 *  A span of two numbers, inclusive.  There are no gaps in a span.
 *  The hash-code is the difference of high and low.
 *  If these values are very small or very large ( e.g. 1e50 ), the hash-code will be 0.
 *  The low and high end are bounded, i.e. they cannot be infinity.
 *
 *  An alternative implementation has {@code T} extend {@code Comparable} instead of
 *  {@code Number}.  The latter would allow a {@code String} span.
 *
 * @author brian
 * @see http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/collect/Range.html
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
     * @param to_include value to include in low &lt;= value &lt;= high.
     */
    public void stretch( T to_include )
    {
        if( to_include.doubleValue() < m_low.doubleValue() )
        {
            this.m_low = to_include;
        }
        else if( m_high.doubleValue() < to_include.doubleValue() )
        {
            this.m_high = to_include;
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
