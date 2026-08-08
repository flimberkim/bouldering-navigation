package com.boulderingnavigation;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boulderingnavigation.domain.CompletionVideo;
import com.boulderingnavigation.domain.Mountain;
import com.boulderingnavigation.domain.Problem;
import com.boulderingnavigation.domain.Region;
import com.boulderingnavigation.domain.Rock;
import com.boulderingnavigation.domain.VideoPlatform;
import com.boulderingnavigation.repository.CompletionVideoRepository;
import com.boulderingnavigation.repository.MountainRepository;
import com.boulderingnavigation.repository.ProblemRepository;
import com.boulderingnavigation.repository.RegionRepository;
import com.boulderingnavigation.repository.RockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MountainAndProblemQueryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private MountainRepository mountainRepository;
    @Autowired
    private RockRepository rockRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private CompletionVideoRepository completionVideoRepository;

    private Problem seededProblem;

    @BeforeEach
    void setUp() {
        Region region = regionRepository.save(Region.builder().name("경기도").build());
        Mountain mountain = mountainRepository.save(Mountain.builder().name("수락산").region(region).build());
        Rock rock = rockRepository.save(Rock.builder().name("치마바위").mountain(mountain).build());
        seededProblem = problemRepository.save(Problem.builder().name("좌측 크랙").grade("V4").rock(rock).build());
        completionVideoRepository.save(CompletionVideo.builder()
                .url("https://youtube.com/watch?v=example")
                .platform(VideoPlatform.YOUTUBE)
                .problem(seededProblem)
                .build());
    }

    @Test
    void 산_이름으로_검색하면_바위와_문제_목록이_내려온다() throws Exception {
        mockMvc.perform(get("/api/mountains").param("q", "수락"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("수락산"))
                .andExpect(jsonPath("$[0].regionName").value("경기도"))
                .andExpect(jsonPath("$[0].rocks", hasSize(1)))
                .andExpect(jsonPath("$[0].rocks[0].name").value("치마바위"))
                .andExpect(jsonPath("$[0].rocks[0].problems[0].name").value("좌측 크랙"))
                .andExpect(jsonPath("$[0].rocks[0].problems[0].grade").value("V4"));
    }

    @Test
    void 문제_이름으로_검색하면_소속된_산과_바위가_함께_내려온다() throws Exception {
        mockMvc.perform(get("/api/mountains").param("q", "좌측"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("수락산"))
                .andExpect(jsonPath("$[0].rocks", hasSize(1)))
                .andExpect(jsonPath("$[0].rocks[0].name").value("치마바위"))
                .andExpect(jsonPath("$[0].rocks[0].problems", hasSize(1)))
                .andExpect(jsonPath("$[0].rocks[0].problems[0].name").value("좌측 크랙"));
    }

    @Test
    void 난이도로_검색하면_소속된_산과_바위가_함께_내려온다() throws Exception {
        mockMvc.perform(get("/api/mountains").param("q", "V4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("수락산"))
                .andExpect(jsonPath("$[0].rocks[0].problems[0].grade").value("V4"));
    }

    @Test
    void 검색어와_일치하는_산_바위_문제가_없으면_빈_목록을_반환한다() throws Exception {
        mockMvc.perform(get("/api/mountains").param("q", "존재하지않음"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void 문제_상세를_조회하면_완등영상_링크가_함께_내려온다() throws Exception {
        mockMvc.perform(get("/api/problems/{id}", seededProblem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("좌측 크랙"))
                .andExpect(jsonPath("$.mountainName").value("수락산"))
                .andExpect(jsonPath("$.videos", hasSize(1)))
                .andExpect(jsonPath("$.videos[0].url").value("https://youtube.com/watch?v=example"))
                .andExpect(jsonPath("$.videos[0].platform").value("YOUTUBE"));
    }

    @Test
    void 존재하지_않는_문제를_조회하면_404를_반환한다() throws Exception {
        mockMvc.perform(get("/api/problems/{id}", 999_999L))
                .andExpect(status().isNotFound());
    }
}
