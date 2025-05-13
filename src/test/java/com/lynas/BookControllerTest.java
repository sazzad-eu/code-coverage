package com.lynas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetBook() throws Exception {
        Book book = new Book(null, "1984", "George Orwell");

        // POST - create book
        String response = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        Book created = objectMapper.readValue(response, Book.class);

        // GET - fetch created book
        mockMvc.perform(get("/books/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.author").value("George Orwell"));
    }

    @Test
    void testUpdateBook() throws Exception {
        Book book = new Book(null, "Old Title", "Author");

        String postResponse = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andReturn().getResponse().getContentAsString();

        Book created = objectMapper.readValue(postResponse, Book.class);

        created.setTitle("New Title");

        // PUT - update book
        mockMvc.perform(put("/books/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void testDeleteBook() throws Exception {
        Book book = new Book(null, "To Be Deleted", "Author");

        String postResponse = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andReturn().getResponse().getContentAsString();

        Book created = objectMapper.readValue(postResponse, Book.class);

        // DELETE - remove book
        mockMvc.perform(delete("/books/" + created.getId()))
                .andExpect(status().isNoContent());

        // GET - verify deletion
        mockMvc.perform(get("/books/" + created.getId()))
                .andExpect(status().isNotFound());
    }
}
