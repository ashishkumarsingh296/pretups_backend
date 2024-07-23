package com.restapi.c2s.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {O2CTransferAckwledgePDFReportGen.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class O2CTransferAckwledgePDFReportGenTest {
    @Autowired
    private O2CTransferAckwledgePDFReportGen o2CTransferAckwledgePDFReportGen;

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#generatePDF(ChannelTransferVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGeneratePDF() {
       com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.O2CTransferAckwledgePDFReportGen.generatePDF(O2CTransferAckwledgePDFReportGen.java:61)
        //   See https://diff.blue/R013 to resolve this issue.

        o2CTransferAckwledgePDFReportGen.generatePDF(ChannelTransferVO.getInstance());
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#generatePDF(ChannelTransferVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGeneratePDF2() {
       com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.O2CTransferAckwledgePDFReportGen.generatePDF(O2CTransferAckwledgePDFReportGen.java:61)
        //   See https://diff.blue/R013 to resolve this issue.

        o2CTransferAckwledgePDFReportGen.generatePDF(new ChannelTransferVO());
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#generatePDF(ChannelTransferVO)}
     */
    @Test
   //@Ignore("TODO: Complete this test")
    public void testGeneratePDF3() {
       com.btsl.util.JUnitConfig.init(); //Auto replace
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.util.Locale.<init>(Locale.java:648)
        //       at java.util.Locale.<init>(Locale.java:677)
        //       at com.restapi.c2s.services.O2CTransferAckwledgePDFReportGen.generatePDF(O2CTransferAckwledgePDFReportGen.java:61)
        //   See https://diff.blue/R013 to resolve this issue.

        o2CTransferAckwledgePDFReportGen.generatePDF(mock(ChannelTransferVO.class));
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#commonFill(String, Font)}
     */
    @Test
    public void testCommonFill() {
        PdfPCell actualCommonFillResult = o2CTransferAckwledgePDFReportGen.commonFill("Data", new Font());
        assertNull(actualCommonFillResult.getAccessibleAttributes());
        assertEquals(0.0f, actualCommonFillResult.getRightIndent(), 0.0f);
        assertEquals(0, actualCommonFillResult.getArabicOptions());
        assertEquals(0.0f, actualCommonFillResult.getRight(), 0.0f);
        assertTrue(actualCommonFillResult.getPhrase() instanceof Paragraph);
        assertEquals(2.0f, actualCommonFillResult.getPaddingTop(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getPaddingRight(), 0.0f);
        assertEquals(10.0f, actualCommonFillResult.getPaddingLeft(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getPaddingBottom(), 0.0f);
        assertEquals(1.0f, actualCommonFillResult.getMultipliedLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getMaxHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getIndent(), 0.0f);
        assertEquals(0, actualCommonFillResult.getHorizontalAlignment());
        assertEquals(15, actualCommonFillResult.getBorder());
        assertNull(actualCommonFillResult.getHeaders());
        assertEquals(0.0f, actualCommonFillResult.getGrayFill(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getFollowingIndent(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getFixedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getExtraParagraphSpace(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getEffectivePaddingTop(), 0.0f);
        assertNull(actualCommonFillResult.getCompositeElements());
        assertEquals(1, actualCommonFillResult.getColspan());
        assertEquals(0.0f, actualCommonFillResult.getCalculatedHeight(), 0.0f);
        assertEquals(-4.0f, actualCommonFillResult.getBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthTop(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthRight(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthLeft(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidth(), 0.0f);
        BaseColor borderColorRight = actualCommonFillResult.getBorderColorRight();
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorTop());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorLeft());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorBottom());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColor());
        assertEquals(4.0f, actualCommonFillResult.getHeight(), 0.0f);
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#commonFill(String, Font)}
     */
    @Test
    public void testCommonFill2() {
        PdfPCell actualCommonFillResult = o2CTransferAckwledgePDFReportGen.commonFill("", new Font());
        assertNull(actualCommonFillResult.getAccessibleAttributes());
        assertEquals(0.0f, actualCommonFillResult.getRightIndent(), 0.0f);
        assertEquals(0, actualCommonFillResult.getArabicOptions());
        assertEquals(0.0f, actualCommonFillResult.getRight(), 0.0f);
        assertTrue(actualCommonFillResult.getPhrase() instanceof Paragraph);
        assertEquals(2.0f, actualCommonFillResult.getPaddingTop(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getPaddingRight(), 0.0f);
        assertEquals(10.0f, actualCommonFillResult.getPaddingLeft(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getPaddingBottom(), 0.0f);
        assertEquals(1.0f, actualCommonFillResult.getMultipliedLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getMaxHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getIndent(), 0.0f);
        assertEquals(0, actualCommonFillResult.getHorizontalAlignment());
        assertEquals(0, actualCommonFillResult.getBorder());
        assertNull(actualCommonFillResult.getHeaders());
        assertEquals(0.0f, actualCommonFillResult.getGrayFill(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getFollowingIndent(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getFixedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getExtraParagraphSpace(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getEffectivePaddingTop(), 0.0f);
        assertNull(actualCommonFillResult.getCompositeElements());
        assertEquals(1, actualCommonFillResult.getColspan());
        assertEquals(0.0f, actualCommonFillResult.getCalculatedHeight(), 0.0f);
        assertEquals(-4.0f, actualCommonFillResult.getBottom(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getBorderWidthTop(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getBorderWidthRight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getBorderWidthLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getBorderWidthBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidth(), 0.0f);
        BaseColor borderColorRight = actualCommonFillResult.getBorderColorRight();
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorTop());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorLeft());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorBottom());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColor());
        assertEquals(4.0f, actualCommonFillResult.getHeight(), 0.0f);
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#commonFill(String, Font)}
     */
    @Test
    public void testCommonFill3() {
        PdfPCell actualCommonFillResult = o2CTransferAckwledgePDFReportGen.commonFill("42", new Font());
        assertNull(actualCommonFillResult.getAccessibleAttributes());
        assertEquals(0.0f, actualCommonFillResult.getRightIndent(), 0.0f);
        assertEquals(0, actualCommonFillResult.getArabicOptions());
        assertEquals(0.0f, actualCommonFillResult.getRight(), 0.0f);
        assertTrue(actualCommonFillResult.getPhrase() instanceof Paragraph);
        assertEquals(2.0f, actualCommonFillResult.getPaddingTop(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getPaddingRight(), 0.0f);
        assertEquals(10.0f, actualCommonFillResult.getPaddingLeft(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getPaddingBottom(), 0.0f);
        assertEquals(1.0f, actualCommonFillResult.getMultipliedLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getMaxHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getIndent(), 0.0f);
        assertEquals(0, actualCommonFillResult.getHorizontalAlignment());
        assertEquals(15, actualCommonFillResult.getBorder());
        assertNull(actualCommonFillResult.getHeaders());
        assertEquals(0.0f, actualCommonFillResult.getGrayFill(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getFollowingIndent(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getFixedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getExtraParagraphSpace(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getEffectivePaddingTop(), 0.0f);
        assertNull(actualCommonFillResult.getCompositeElements());
        assertEquals(1, actualCommonFillResult.getColspan());
        assertEquals(0.0f, actualCommonFillResult.getCalculatedHeight(), 0.0f);
        assertEquals(-4.0f, actualCommonFillResult.getBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthTop(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthRight(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthLeft(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidth(), 0.0f);
        BaseColor borderColorRight = actualCommonFillResult.getBorderColorRight();
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorTop());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorLeft());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorBottom());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColor());
        assertEquals(4.0f, actualCommonFillResult.getHeight(), 0.0f);
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#commonFill(String, Font)}
     */
    @Test
    public void testCommonFill4() {
        PdfPCell actualCommonFillResult = o2CTransferAckwledgePDFReportGen.commonFill("Data", mock(Font.class));
        assertNull(actualCommonFillResult.getAccessibleAttributes());
        assertEquals(0.0f, actualCommonFillResult.getRightIndent(), 0.0f);
        assertEquals(0, actualCommonFillResult.getArabicOptions());
        assertEquals(0.0f, actualCommonFillResult.getRight(), 0.0f);
        assertTrue(actualCommonFillResult.getPhrase() instanceof Paragraph);
        assertEquals(2.0f, actualCommonFillResult.getPaddingTop(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getPaddingRight(), 0.0f);
        assertEquals(10.0f, actualCommonFillResult.getPaddingLeft(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getPaddingBottom(), 0.0f);
        assertEquals(1.0f, actualCommonFillResult.getMultipliedLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getIndent(), 0.0f);
        assertEquals(0, actualCommonFillResult.getHorizontalAlignment());
        assertEquals(15, actualCommonFillResult.getBorder());
        assertNull(actualCommonFillResult.getHeaders());
        assertEquals(0.0f, actualCommonFillResult.getGrayFill(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getFollowingIndent(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getFixedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getExtraParagraphSpace(), 0.0f);
        assertEquals(2.0f, actualCommonFillResult.getEffectivePaddingTop(), 0.0f);
        assertNull(actualCommonFillResult.getCompositeElements());
        assertEquals(1, actualCommonFillResult.getColspan());
        assertEquals(0.0f, actualCommonFillResult.getCalculatedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillResult.getBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthTop(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthRight(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthLeft(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidthBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillResult.getBorderWidth(), 0.0f);
        BaseColor borderColorRight = actualCommonFillResult.getBorderColorRight();
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorTop());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorLeft());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColorBottom());
        assertSame(borderColorRight, actualCommonFillResult.getBorderColor());
        assertEquals(0.0f, actualCommonFillResult.getHeight(), 0.0f);
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#commonFillNoGray(String, Font)}
     */
    @Test
    public void testCommonFillNoGray() {
        PdfPCell actualCommonFillNoGrayResult = o2CTransferAckwledgePDFReportGen.commonFillNoGray("Data", new Font());
        assertNull(actualCommonFillNoGrayResult.getAccessibleAttributes());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getRightIndent(), 0.0f);
        assertEquals(0, actualCommonFillNoGrayResult.getArabicOptions());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getRight(), 0.0f);
        assertTrue(actualCommonFillNoGrayResult.getPhrase() instanceof Paragraph);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingTop(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingRight(), 0.0f);
        assertEquals(10.0f, actualCommonFillNoGrayResult.getPaddingLeft(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingBottom(), 0.0f);
        assertEquals(1.0f, actualCommonFillNoGrayResult.getMultipliedLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getMaxHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getIndent(), 0.0f);
        assertEquals(0, actualCommonFillNoGrayResult.getHorizontalAlignment());
        assertEquals(15, actualCommonFillNoGrayResult.getBorder());
        assertNull(actualCommonFillNoGrayResult.getHeaders());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getGrayFill(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getFollowingIndent(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getFixedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getExtraParagraphSpace(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getEffectivePaddingTop(), 0.0f);
        assertNull(actualCommonFillNoGrayResult.getCompositeElements());
        assertEquals(1, actualCommonFillNoGrayResult.getColspan());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getCalculatedHeight(), 0.0f);
        assertEquals(-4.0f, actualCommonFillNoGrayResult.getBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthTop(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthRight(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthLeft(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidth(), 0.0f);
        BaseColor borderColorRight = actualCommonFillNoGrayResult.getBorderColorRight();
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorTop());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorLeft());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorBottom());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColor());
        assertEquals(4.0f, actualCommonFillNoGrayResult.getHeight(), 0.0f);
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#commonFillNoGray(String, Font)}
     */
    @Test
    public void testCommonFillNoGray2() {
        PdfPCell actualCommonFillNoGrayResult = o2CTransferAckwledgePDFReportGen.commonFillNoGray("", new Font());
        assertNull(actualCommonFillNoGrayResult.getAccessibleAttributes());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getRightIndent(), 0.0f);
        assertEquals(0, actualCommonFillNoGrayResult.getArabicOptions());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getRight(), 0.0f);
        assertTrue(actualCommonFillNoGrayResult.getPhrase() instanceof Paragraph);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingTop(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingRight(), 0.0f);
        assertEquals(10.0f, actualCommonFillNoGrayResult.getPaddingLeft(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingBottom(), 0.0f);
        assertEquals(1.0f, actualCommonFillNoGrayResult.getMultipliedLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getMaxHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getIndent(), 0.0f);
        assertEquals(0, actualCommonFillNoGrayResult.getHorizontalAlignment());
        assertEquals(15, actualCommonFillNoGrayResult.getBorder());
        assertNull(actualCommonFillNoGrayResult.getHeaders());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getGrayFill(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getFollowingIndent(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getFixedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getExtraParagraphSpace(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getEffectivePaddingTop(), 0.0f);
        assertNull(actualCommonFillNoGrayResult.getCompositeElements());
        assertEquals(1, actualCommonFillNoGrayResult.getColspan());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getCalculatedHeight(), 0.0f);
        assertEquals(-4.0f, actualCommonFillNoGrayResult.getBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthTop(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthRight(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthLeft(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidth(), 0.0f);
        BaseColor borderColorRight = actualCommonFillNoGrayResult.getBorderColorRight();
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorTop());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorLeft());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorBottom());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColor());
        assertEquals(4.0f, actualCommonFillNoGrayResult.getHeight(), 0.0f);
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#commonFillNoGray(String, Font)}
     */
    @Test
    public void testCommonFillNoGray3() {
        PdfPCell actualCommonFillNoGrayResult = o2CTransferAckwledgePDFReportGen.commonFillNoGray("42", new Font());
        assertNull(actualCommonFillNoGrayResult.getAccessibleAttributes());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getRightIndent(), 0.0f);
        assertEquals(0, actualCommonFillNoGrayResult.getArabicOptions());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getRight(), 0.0f);
        assertTrue(actualCommonFillNoGrayResult.getPhrase() instanceof Paragraph);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingTop(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingRight(), 0.0f);
        assertEquals(10.0f, actualCommonFillNoGrayResult.getPaddingLeft(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingBottom(), 0.0f);
        assertEquals(1.0f, actualCommonFillNoGrayResult.getMultipliedLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getMaxHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getIndent(), 0.0f);
        assertEquals(0, actualCommonFillNoGrayResult.getHorizontalAlignment());
        assertEquals(15, actualCommonFillNoGrayResult.getBorder());
        assertNull(actualCommonFillNoGrayResult.getHeaders());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getGrayFill(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getFollowingIndent(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getFixedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getExtraParagraphSpace(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getEffectivePaddingTop(), 0.0f);
        assertNull(actualCommonFillNoGrayResult.getCompositeElements());
        assertEquals(1, actualCommonFillNoGrayResult.getColspan());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getCalculatedHeight(), 0.0f);
        assertEquals(-4.0f, actualCommonFillNoGrayResult.getBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthTop(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthRight(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthLeft(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidth(), 0.0f);
        BaseColor borderColorRight = actualCommonFillNoGrayResult.getBorderColorRight();
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorTop());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorLeft());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorBottom());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColor());
        assertEquals(4.0f, actualCommonFillNoGrayResult.getHeight(), 0.0f);
    }

    /**
     * Method under test: {@link O2CTransferAckwledgePDFReportGen#commonFillNoGray(String, Font)}
     */
    @Test
    public void testCommonFillNoGray4() {
        PdfPCell actualCommonFillNoGrayResult = o2CTransferAckwledgePDFReportGen.commonFillNoGray("Data",
                mock(Font.class));
        assertNull(actualCommonFillNoGrayResult.getAccessibleAttributes());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getRightIndent(), 0.0f);
        assertEquals(0, actualCommonFillNoGrayResult.getArabicOptions());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getRight(), 0.0f);
        assertTrue(actualCommonFillNoGrayResult.getPhrase() instanceof Paragraph);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingTop(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingRight(), 0.0f);
        assertEquals(10.0f, actualCommonFillNoGrayResult.getPaddingLeft(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getPaddingBottom(), 0.0f);
        assertEquals(1.0f, actualCommonFillNoGrayResult.getMultipliedLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getLeft(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getLeading(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getIndent(), 0.0f);
        assertEquals(0, actualCommonFillNoGrayResult.getHorizontalAlignment());
        assertEquals(15, actualCommonFillNoGrayResult.getBorder());
        assertNull(actualCommonFillNoGrayResult.getHeaders());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getGrayFill(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getFollowingIndent(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getFixedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getExtraParagraphSpace(), 0.0f);
        assertEquals(2.0f, actualCommonFillNoGrayResult.getEffectivePaddingTop(), 0.0f);
        assertNull(actualCommonFillNoGrayResult.getCompositeElements());
        assertEquals(1, actualCommonFillNoGrayResult.getColspan());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getCalculatedHeight(), 0.0f);
        assertEquals(0.0f, actualCommonFillNoGrayResult.getBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthTop(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthRight(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthLeft(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidthBottom(), 0.0f);
        assertEquals(0.5f, actualCommonFillNoGrayResult.getBorderWidth(), 0.0f);
        BaseColor borderColorRight = actualCommonFillNoGrayResult.getBorderColorRight();
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorTop());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorLeft());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColorBottom());
        assertSame(borderColorRight, actualCommonFillNoGrayResult.getBorderColor());
        assertEquals(0.0f, actualCommonFillNoGrayResult.getHeight(), 0.0f);
    }
}

