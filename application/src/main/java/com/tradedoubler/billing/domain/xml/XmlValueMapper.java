package com.tradedoubler.billing.domain.xml;

import com.tradedoubler.billing.domain.*;
import com.tradedoubler.billing.exception.XmlFileDataException;
import com.tradedoubler.billing.utils.MathUtil;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Thomas Rambrant (thore)
 */

public class XmlValueMapper
{
    //
    // Replaces the values with the ones given by the XML file. The xml data is represented by NamedNodeMap values
    //
    public static <T> T patchObject( T object, NamedNodeMap attributes)
    {
        if( attributes != null)
        {
            try
            {
                //
                // Loop over all xml node attributes and map them to the corresponding field in the object
                //
                for( int attributeIndex = 0;
                     attributeIndex < attributes.getLength();
                     attributeIndex++)
                {
                    Node item = attributes.item( attributeIndex);

                    if( item != null)
                    {
                        Field field   = getField( object, item.getNodeName());
                        Class<?> type = field.getType();

                        field.setAccessible( true);

                        //
                        // we found the field and we made it accessible. Now we patch it...
                        //
                        field.set( object, XmlValueMapper.transformToObject( item.getNodeValue(), type));
                    }
                }
            }
            catch( NoSuchFieldException e)
            {
                throw new XmlFileDataException( "Field does not exist", e);
            }
            catch( IllegalAccessException e)
            {
                throw new XmlFileDataException( "Field can't be set", e);
            }
        }

        return object;
    }

    private static <T> Field getField( T object, String nodeName) throws NoSuchFieldException
    {
        //
        // search for the fields in the given class and all subclasses.
        //
        for( Class<?> c = object.getClass(); c != null; c = c.getSuperclass())
        {
            try
            {
                return c.getDeclaredField( nodeName);
            }
            catch( NoSuchFieldException e)
            {
                //
                // catch this and swallow it to enable the loop to try again
                //
            }
        }

        //
        // None was found with the given name
        //
        throw new NoSuchFieldException( nodeName);
    }

    //
    // Creates a suitable object of the given type from the xml value ( a string)
    //
    public static Object transformToObject( String value, Class<?> type)
    {
        try
        {
            //
            // Check for null objects
            //
            if( value.equalsIgnoreCase( "null"))
            {
                return null;
            }

            //
            // Primitive types
            //
            if( type.equals( String.class))
            {
                return value;
            }

            if( type.equals( Date.class))
            {
                return new Date( new SimpleDateFormat( "yyyy-mm-dd").parse( value).getTime());
            }

            if( type.equals( Integer.class) ||
                type.equals( int.class))
            {
                return Integer.valueOf( value);
            }

            if( type.equals( Long.class) ||
                type.equals( long.class))
            {
                return Long.valueOf( value);
            }

            if( type.equals( Double.class) ||
                type.equals( double.class))
            {
                return MathUtil.getInDecimals( Double.valueOf( value), 2, BigDecimal.ROUND_DOWN);
            }

            if( type.equals( Boolean.class) ||
                type.equals( boolean.class))
            {
                if( "y".equals(  value.toLowerCase()) ||
                    "n".equals(  value.toLowerCase()) ||
                    "no".equals( value.toLowerCase()) ||
                    "yes".equals( value.toLowerCase()))
                {
                    return value.equalsIgnoreCase( "y") || value.equalsIgnoreCase( "yes)");
                }
                return Boolean.valueOf( value);
            }

            if( type.equals( BigDecimal.class))
            {
                return BigDecimal.valueOf( Long.valueOf( value));
            }

            if( type.equals( DebitOrCredit.class))
            {
                return DebitOrCredit.valueOf( value);
            }

            //
            // Domain types with a String constructor
            //
            if( type.equals( Currency.class) ||
                type.equals( Guid.class)     ||
                type.equals( Country.class)  ||
                type.equals( com.tradedoubler.billing.domain.Timestamp.class))
            {
                Constructor ctor = type.getConstructor( String.class);
                return ctor.newInstance( value);
            }

            //
            // Domain types with a BigDecimal constructor
            //
            if( type.equals( Money.class))
            {
                Constructor ctor = type.getConstructor( BigDecimal.class);
                return ctor.newInstance( new BigDecimal( value));
            }

            //
            // Domain types with a integer constructor
            //
            if( type.equals( DistributionMode.class) ||
                type.equals( Market.class)           ||
                type.equals( PaymentMethod.class)    ||
                type.equals( PostingProfile.class)   ||
                type.equals( RevenueType.class)      ||
                type.equals( TermsOfPayment.class)   ||
                type.equals( AddressType.class)      ||
                type.equals( ProductType.class)      )
            {
                Constructor ctor = type.getConstructor( int.class);
                return ctor.newInstance( Integer.valueOf( value));
            }
        }
        catch( NoSuchMethodException e)
        {
            throw new XmlFileDataException( "Illegal mapping of constructor", e);
        }
        catch( InvocationTargetException e)
        {
            throw new XmlFileDataException( "Could not create " + type.getName(), e);
        }
        catch( InstantiationException e)
        {
            throw new XmlFileDataException( "Could not create " + type.getName(), e);
        }
        catch( IllegalAccessException e)
        {
            throw new XmlFileDataException( "Illegal access rights on constructor", e);
        }
        catch( ParseException e)
        {
            throw new XmlFileDataException( "Could not parse XML string into " + type.getName(), e);
        }

        throw new XmlFileDataException( "Type " + type.getName() + " could not be translated");
    }
}
