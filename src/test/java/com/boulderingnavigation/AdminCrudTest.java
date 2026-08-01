package com.boulderingnavigation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boulderingnavigation.domain.Region;
import com.boulderingnavigation.repository.MountainRepository;
import com.boulderingnavigation.repository.RegionRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminCrudTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private MountainRepository mountainRepository;

    private Region region;

    @BeforeEach
    void setUp() {
        region = regionRepository.save(Region.builder().name("경기도").build());
    }

    @AfterEach
    void tearDown() {
        mountainRepository.deleteAll();
        regionRepository.deleteAll();
    }

    @Test
    void 지역을_등록_수정_삭제할_수_있다() throws Exception {
        String createBody = objectMapper.writeValueAsString(new NameOnly("서울특별시"));

        String response = mockMvc.perform(post("/api/admin/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("서울특별시"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/api/admin/regions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NameOnly("서울"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("서울"));

        mockMvc.perform(delete("/api/admin/regions/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void 이름이_비어있으면_400과_필드_에러를_반환한다() throws Exception {
        mockMvc.perform(post("/api/admin/regions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NameOnly(""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void 존재하지_않는_지역으로_산을_등록하면_404를_반환한다() throws Exception {
        mockMvc.perform(post("/api/admin/mountains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"관악산\",\"regionId\":999999}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 하위_산이_있는_지역을_삭제하면_409를_반환한다() throws Exception {
        mockMvc.perform(post("/api/admin/mountains")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"수락산\",\"regionId\":" + region.getId() + "}"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/admin/regions/{id}", region.getId()))
                .andExpect(status().isConflict());
    }

    private record NameOnly(String name) {
    }
}
