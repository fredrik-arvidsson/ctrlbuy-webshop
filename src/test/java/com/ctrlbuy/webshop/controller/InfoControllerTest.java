package com.ctrlbuy.webshop.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InfoControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private InfoController infoController;

    @BeforeEach
    void setUp() {
        // Setup common test data if needed
    }

    @Test
    void testLeveransinformation() {
        // When
        String viewName = infoController.leveransinformation(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Leveransinformation - CtrlBuy");
        verify(model).addAttribute("feature", "Leveransinformation");
        verify(model).addAttribute("icon", "truck");
        verify(model).addAttribute("message", "Information om frakt, leveranstider och spårning av ditt paket.");
        verify(model).addAttribute("details", "Här kommer du snart att kunna läsa om våra leveransalternativ, fraktkostnader och hur du spårar ditt paket. Vi erbjuder flera leveransmetoder för din bekvämlighet.");
        verify(model).addAttribute("returnUrl", "/info/kontakt");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testBetalningsmetoder() {
        // When
        String viewName = infoController.betalningsmetoder(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Betalningsmetoder - CtrlBuy");
        verify(model).addAttribute("feature", "Betalningsmetoder");
        verify(model).addAttribute("icon", "credit-card");
        verify(model).addAttribute("message", "Information om våra betalningsalternativ och hur betalningen fungerar.");
        verify(model).addAttribute("details", "Vi kommer snart att erbjuda flera betalningsmetoder som kort, Swish, Klarna och bankgiro för din bekvämlighet. Alla betalningar är säkra och krypterade.");
        verify(model).addAttribute("returnUrl", "/info/kontakt");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testReturerReklamationer() {
        // When
        String viewName = infoController.returerReklamationer(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Retur & Reklamationspolicy - CtrlBuy");
        verify(model).addAttribute("feature", "Retur & Reklamationspolicy");
        verify(model).addAttribute("icon", "undo-alt");
        verify(model).addAttribute("message", "Här kan du läsa om vår returpolicy och hur du gör reklamationer.");
        verify(model).addAttribute("details", "Vi erbjuder generös returrätt och enkel reklamationsprocess. Detaljerad information om villkor och procedurer kommer snart att finnas tillgänglig här.");
        verify(model).addAttribute("returnUrl", "/info/kontakt");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testContact() {
        // When
        String viewName = infoController.contact(model);

        // Then
        assertEquals("contact", viewName);
        verify(model).addAttribute("pageTitle", "Kontakta oss - CtrlBuy");
        verify(model).addAttribute("title", "Kontakta oss - CtrlBuy");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testIntegritetspolicy() {
        // When
        String viewName = infoController.integritetspolicy(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Integritetspolicy - CtrlBuy");
        verify(model).addAttribute("feature", "Integritetspolicy");
        verify(model).addAttribute("icon", "user-shield");
        verify(model).addAttribute("message", "Information om hur vi hanterar dina personuppgifter enligt GDPR.");
        verify(model).addAttribute("details", "Vi värnar om din integritet och följer alla GDPR-regler. Detaljerad information om våra rutiner för personuppgifter kommer snart.");
        verify(model).addAttribute("returnUrl", "/info/kontakt");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testAllmännaVillkor() {
        // When
        String viewName = infoController.allmännaVillkor(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Allmänna villkor - CtrlBuy");
        verify(model).addAttribute("feature", "Allmänna villkor");
        verify(model).addAttribute("icon", "file-contract");
        verify(model).addAttribute("message", "Våra allmänna villkor för köp och användning av tjänsten.");
        verify(model).addAttribute("details", "Fullständiga villkor och användaravtal kommer snart att publiceras här för full transparens.");
        verify(model).addAttribute("returnUrl", "/info/kontakt");
        verifyNoMoreInteractions(model);
    }
}