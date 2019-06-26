package org.syaku.security.config;

/**
 * @author Seok Kyun. Choi.
 * @since 2019-06-26
 */

import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.syaku.security.web.BlogController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@RunWith(SpringRunner.class)
@WebMvcTest(BlogController.class)
public class WebSecurityConfigurationTest {
    private String URL = "/api/blog";

    @Autowired private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void admin() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    public void manager() throws Exception {
        mvc.perform(put(URL).with(csrf())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void user() throws Exception {
        mvc.perform(post(URL).with(csrf())).andExpect(status().isOk());
    }
}