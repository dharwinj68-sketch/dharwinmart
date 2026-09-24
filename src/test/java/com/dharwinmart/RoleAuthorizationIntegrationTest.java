package com.dharwinmart;

import com.dharwinmart.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RoleAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUnauthenticatedAccessToAdminRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?role=ADMIN&error=auth_required"));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?role=ADMIN&error=auth_required"));
    }

    @Test
    void testUnauthenticatedAccessToSellerRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/seller"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?role=SELLER&error=auth_required"));

        mockMvc.perform(get("/seller/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?role=SELLER&error=auth_required"));
    }

    @Test
    void testUnauthenticatedAccessToBuyerAndWishlistRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/buyer/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?role=BUYER&error=auth_required"));

        mockMvc.perform(get("/wishlist"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?role=BUYER&error=auth_required"));
    }

    @Test
    void testBuyerCannotAccessAdminDashboard() throws Exception {
        MockHttpSession buyerSession = new MockHttpSession();
        buyerSession.setAttribute(LoginController.SESSION_USER_KEY, "buyer1");
        buyerSession.setAttribute(LoginController.SESSION_ROLE_KEY, "BUYER");
        buyerSession.setAttribute(LoginController.SESSION_USER_ID_KEY, 4L);

        mockMvc.perform(get("/admin/dashboard").session(buyerSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void testBuyerCannotAccessSellerDashboard() throws Exception {
        MockHttpSession buyerSession = new MockHttpSession();
        buyerSession.setAttribute(LoginController.SESSION_USER_KEY, "buyer1");
        buyerSession.setAttribute(LoginController.SESSION_ROLE_KEY, "BUYER");
        buyerSession.setAttribute(LoginController.SESSION_USER_ID_KEY, 4L);

        mockMvc.perform(get("/seller/dashboard").session(buyerSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void testSellerCannotAccessAdminDashboard() throws Exception {
        MockHttpSession sellerSession = new MockHttpSession();
        sellerSession.setAttribute(LoginController.SESSION_USER_KEY, "techseller");
        sellerSession.setAttribute(LoginController.SESSION_ROLE_KEY, "SELLER");
        sellerSession.setAttribute(LoginController.SESSION_USER_ID_KEY, 2L);

        mockMvc.perform(get("/admin/dashboard").session(sellerSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void testAjaxWishlistUnauthenticatedReturns401Json() throws Exception {
        mockMvc.perform(post("/wishlist/toggle").param("productId", "1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("auth_required"));
    }

    @Test
    void testAjaxWishlistSellerReturns403Json() throws Exception {
        MockHttpSession sellerSession = new MockHttpSession();
        sellerSession.setAttribute(LoginController.SESSION_USER_KEY, "techseller");
        sellerSession.setAttribute(LoginController.SESSION_ROLE_KEY, "SELLER");
        sellerSession.setAttribute(LoginController.SESSION_USER_ID_KEY, 2L);

        mockMvc.perform(post("/wishlist/toggle").param("productId", "1").session(sellerSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("access_denied"));
    }

    @Test
    void testAjaxWishlistBuyerSuccess() throws Exception {
        MockHttpSession buyerSession = new MockHttpSession();
        buyerSession.setAttribute(LoginController.SESSION_USER_KEY, "buyer1");
        buyerSession.setAttribute(LoginController.SESSION_ROLE_KEY, "BUYER");
        buyerSession.setAttribute(LoginController.SESSION_USER_ID_KEY, 4L);

        mockMvc.perform(post("/wishlist/toggle").param("productId", "2").session(buyerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
