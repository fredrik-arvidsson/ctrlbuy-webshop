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
class HelpControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private HelpController helpController;

    @BeforeEach
    void setUp() {
        // Setup common test data if needed
    }

    @Test
    void testLeveransinformation() {
        // When
        String viewName = helpController.leveransinformation(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Leveransinformation - CtrlBuy");
        verify(model).addAttribute("feature", "Leveransinformation");
        verify(model).addAttribute("icon", "truck");
        verify(model).addAttribute("message", "Information om frakt, leveranstider och spårning av ditt paket.");
        verify(model).addAttribute("details", "Här kommer du snart att kunna läsa om våra leveransalternativ, fraktkostnader och hur du spårar ditt paket.");
        verify(model).addAttribute("returnUrl", "/contact");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testReturerReklamationer() {
        // When
        String viewName = helpController.returerReklamationer(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Returer & Reklamationer - CtrlBuy");
        verify(model).addAttribute("feature", "Returer & Reklamationer");
        verify(model).addAttribute("icon", "undo-alt");
        verify(model).addAttribute("message", "Här kan du hantera returer och reklamationer enkelt och smidigt.");
        verify(model).addAttribute("details", "Funktionen för att returnera varor och göra reklamationer kommer snart. Du kommer att kunna starta returprocessen direkt här.");
        verify(model).addAttribute("returnUrl", "/contact");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testBetalningsmetoder() {
        // When
        String viewName = helpController.betalningsmetoder(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Betalningsmetoder - CtrlBuy");
        verify(model).addAttribute("feature", "Betalningsmetoder");
        verify(model).addAttribute("icon", "credit-card");
        verify(model).addAttribute("message", "Information om våra betalningsalternativ och hur betalningen fungerar.");
        verify(model).addAttribute("details", "Vi kommer snart att erbjuda flera betalningsmetoder som kort, Swish, Klarna och bankgiro för din bekvämlighet.");
        verify(model).addAttribute("returnUrl", "/contact");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testKundtjanst() {
        // When
        String viewName = helpController.kundtjanst(model);

        // Then
        assertEquals("coming-soon", viewName);
        verify(model).addAttribute("pageTitle", "Kundtjänst - CtrlBuy");
        verify(model).addAttribute("feature", "Kundtjänst");
        verify(model).addAttribute("icon", "headset");
        verify(model).addAttribute("message", "Direktkontakt med vår kundtjänst för personlig hjälp och support.");
        verify(model).addAttribute("details", "Kundtjänstfunktionen utvecklas för närvarande. Snart kan du chatta direkt med våra supportagenter.");
        verify(model).addAttribute("returnUrl", "/contact");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testContact() {
        // When
        String viewName = helpController.contact(model);

        // Then
        assertEquals("contact", viewName);
        verify(model).addAttribute("pageTitle", "Kontakta oss - CtrlBuy");
        verifyNoMoreInteractions(model);
    }
}