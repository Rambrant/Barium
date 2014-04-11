package com.tradedoubler.billing.domain.xml;

import com.tradedoubler.billing.domain.*;
import com.tradedoubler.billing.exception.XmlFileDataException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author Thomas Rambrant (thore)
 */
public class XmlValueMapperTest
{
    @Test
    public void testTransformToObjectBasicTypes() throws Exception
    {
        Assert.assertEquals(
            "Test",
            XmlValueMapper.transformToObject( "Test", String.class));

        Date date = new Date( 0);

        Assert.assertEquals(
            date.toString(),
            ((XmlValueMapper.transformToObject( date.toString(), Date.class))).toString());

        Assert.assertEquals(
            3,
            XmlValueMapper.transformToObject( "3", int.class));

        Assert.assertEquals(
            3,
            XmlValueMapper.transformToObject( "3", Integer.class));

        Assert.assertEquals(
            5L,
            XmlValueMapper.transformToObject( "5", long.class));

        Assert.assertEquals(
            5L,
            XmlValueMapper.transformToObject( "5", Long.class));

        Assert.assertEquals(
            5.25D,
            XmlValueMapper.transformToObject( "5.25", double.class));

        Assert.assertEquals(
            5.25D,
            XmlValueMapper.transformToObject( "5.25", Double.class));

        Assert.assertEquals(
            false,
            XmlValueMapper.transformToObject( "false", boolean.class));

        Assert.assertEquals(
            true,
            XmlValueMapper.transformToObject( "Y", Boolean.class));

        Assert.assertEquals(
            new BigDecimal( 1256),
            XmlValueMapper.transformToObject( "1256", BigDecimal.class));
    }

    @Test
    public void testTransformToObjectDomain() throws Exception
    {
        Assert.assertEquals(
            new Currency( "SEK").getCurrencyCode(),
            ((Currency) XmlValueMapper.transformToObject( "SEK", Currency.class)).getCurrencyCode());

        Assert.assertEquals(
            new DistributionMode( 1).getValue(),
            ((DistributionMode)XmlValueMapper.transformToObject( "1", DistributionMode.class)).getValue());

        Assert.assertEquals(
            new Guid( "3F2504E0-4F89-11D3-9A0C-0305E82C3405").getGuid(),
            ((Guid)XmlValueMapper.transformToObject( "3F2504E0-4F89-11D3-9A0C-0305E82C3405", Guid.class)).getGuid());

        Assert.assertEquals(
            new Market( 51).getOrganizationId(),
            ((Market)XmlValueMapper.transformToObject( "51", Market.class)).getOrganizationId());

        Assert.assertEquals(
            new PaymentMethod( 7).getValue(),
            ((PaymentMethod)XmlValueMapper.transformToObject( "7", PaymentMethod.class)).getValue());

        Assert.assertEquals(
            new PostingProfile( 3).getValue(),
            ((PostingProfile)XmlValueMapper.transformToObject( "3", PostingProfile.class)).getValue());

        Assert.assertEquals(
            new RevenueType( 1).getValue(),
            ((RevenueType)XmlValueMapper.transformToObject( "1", RevenueType.class)).getValue());

        Assert.assertEquals(
            new TermsOfPayment( 30).getDays(),
            ((TermsOfPayment)XmlValueMapper.transformToObject( "30", TermsOfPayment.class)).getDays());

        Assert.assertEquals(
            new Country( "SE").getValue(),
            ((Country) XmlValueMapper.transformToObject( "SE", Country.class)).getValue());

        Assert.assertEquals(
            new AddressType( 2).getValue(),
            ((AddressType) XmlValueMapper.transformToObject( "2", AddressType.class)).getValue());

        Assert.assertEquals(
            new ProductType( 4).getValue(),
            ((ProductType) XmlValueMapper.transformToObject( "4", ProductType.class)).getValue());

        Assert.assertEquals(
            new Timestamp( "2012-06-25T15:30:01.999+02:00").getTimestamp(),
            ((Timestamp) XmlValueMapper.transformToObject( "2012-06-25T15:30:01.999+02:00", Timestamp.class)).getTimestamp());

        Assert.assertEquals(
            new BigDecimal( "12.34"),
            ((Money) XmlValueMapper.transformToObject( "12.34", Money.class)).getValue());

        Assert.assertEquals(
            DebitOrCredit.CREDIT,
            XmlValueMapper.transformToObject( "CREDIT", DebitOrCredit.class));
    }

    class TestType
    {
        private String integer;

        TestType( String integer)
        {
            this.integer = integer;
        }

        public String getInteger()
        {
            return integer;
        }
    }

    @Test( expected = XmlFileDataException.class)
    public void testTransformToObjectIllegalType() throws Exception
    {
        Assert.assertEquals(
            new TestType("5").getInteger(),
            ((TestType) XmlValueMapper.transformToObject( "5", TestType.class)).getInteger());
    }
}
